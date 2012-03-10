package kit.ral.map.rendering;

import static javax.media.opengl.GL.*;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import org.apache.log4j.Logger;

import kit.ral.common.Context3D;
import kit.ral.common.Coordinates;
import kit.ral.common.Context;
import kit.ral.common.Frustum;
import kit.ral.common.Selection;
import kit.ral.common.projection.Projection;
import kit.ral.common.projection.ProjectionFactory;
import kit.ral.common.util.MathUtil;
import kit.ral.controller.State;
import kit.ral.heightinfo.IHeightmap;
import kit.ral.map.info.MapInfo;
import kit.ral.map.rendering.Renderer;

public class Renderer3D extends Renderer {

    private static final Logger logger = Logger.getLogger(Renderer3D.class);
    
    private static final boolean DRAW_BOXES = false;
    private static final float
        HEIGHT_SCALE_FACTOR = 4, // factor multiplied on all height values
        VIEW_HEIGHT_ADAPTION = 0.2f, // [0..1] how fast camera height adapts to ground height
        ROUTE_HEIGHT_OFFSET = 0, // [meters] height value added to route display
        ROUTE_WIDTH = 10; // [pixels] width of the line that is used for route display
    private static final float[] COLOR_STARTNODE = {0.2039f, 0.5922f, 0.1961f};
    private static final float[] COLOR_WAYNODE = {0.7843f, 0.8235f, 0.145f};
    private static final float[] COLOR_ENDNODE = {0.5921f, 0.196f, 0.2157f};
    
    private float viewHeight = Float.NEGATIVE_INFINITY;
    private boolean cacheResetScheduled = false;
    
    // Temporary variables (only guaranteed to be valid when rendering):
    private Context3D context;
    private Frustum frustum;
    private Projection projection;
    private IHeightmap heightmap;
    private int tilesRendered;
    
    /**
     * Renders a map view in three dimensional form,
     * using height data and perspective projection in the process.
     * @param renderingContext an OpenGL rendering context
     */
    @Override
    public void render(Context renderContext) {
        if (!(renderContext instanceof Context3D)) {
            return;
        }
        // RETRIEVE VARIABLES TO WORK WITH
        context = (Context3D) renderContext;
        projection = ProjectionFactory.getCurrentProjection();
        heightmap = State.getInstance().getLoadedHeightmap();
        GL gl = context.getGL();
        Coordinates center = context.getCenter();
        int tileSize = BASE_TILE_SIZE * Projection.getZoomFactor(context.getDetailLevel());
        
        // RESET CACHE IF SCHEDULED
        if (cacheResetScheduled) {
            for (Tile tile : cache.resetCache()) {
                ((Tile3D) tile).freeResources(gl);
            }
            cacheResetScheduled = false;
        }
        
        // FINAL CAMERA TRANSFORMATIONS
        //gl.glScalef(1, 1, HEIGHT_SCALE_FACTOR * (context.getDetailLevel() + 1));
        gl.glScalef(1, 1, HEIGHT_SCALE_FACTOR);
        float centerHeight = heightmap.getHeight(projection.getGeoCoordinates(center));
        viewHeight = (viewHeight == Float.NEGATIVE_INFINITY) ? centerHeight
                : MathUtil.interpolate(viewHeight, centerHeight, VIEW_HEIGHT_ADAPTION);
        gl.glTranslatef(0, 0, -viewHeight);     
        
        // TEST AND RENDER TILES, STARTING AT THE CENTRAL TILE
        frustum = new Frustum(gl);
        tilesRendered = 0;
        Set<Point> testedTiles = new HashSet<Point>();
        int x = (int) (center.getLongitude() / tileSize);
        int y = (int) (center.getLatitude() / tileSize);
        testTile(testedTiles, x+1, y+1, tileSize);
        testTile(testedTiles, x+1, y-1, tileSize);
        testTile(testedTiles, x-1, y-1, tileSize);
        testTile(testedTiles, x-1, y+1, tileSize);
        logger.debug("Tiles rendered/tested: " + tilesRendered + " / " + testedTiles.size());
        
        drawRoute();
        
        // Draw boxes
        if (DRAW_BOXES) {
            for (Point pos: testedTiles) {
                Coordinates topLeft = new Coordinates(pos.y * tileSize, pos.x * tileSize);
                Tile3D tile = (Tile3D) cache.queryCache(topLeft, tileSize, context.getDetailLevel());
                if (tile != null) {
                    tile.renderBox(gl);
                }
            }
        }
    }
    
    /**
     * Tests whether the tile at the coordinates x*tileSize, y*tileSize is in the view frustum.
     * If true renders the tile and forward the test to its so far untested neighbors in the tile grid.
     * @param testedTiles list of all tiles that have been tested so far
     * @param x the tile's x offset in the tile grid
     * @param y the tile's y offset in the tile grid
     * @param tileSize the tile grid unit size
     */
    private void testTile(Set<Point> testedTiles, int x, int y, int tileSize) {
        // ABORT IF TILE HAS ALREADY BEEN TESTED
        if (!testedTiles.add(new Point(x, y))) {
            return;
        }
        // TEST (AND IF POSITIVE RENDER) TILE, TEST NEIGHBORING 8 TILES
        if (renderTile(x, y, tileSize)) {
            tilesRendered++;
            testTile(testedTiles, x+1, y-1, tileSize);
            testTile(testedTiles, x+1, y,   tileSize);
            testTile(testedTiles, x+1, y+1, tileSize);
            testTile(testedTiles, x,   y+1, tileSize);
            testTile(testedTiles, x-1, y+1, tileSize);
            testTile(testedTiles, x-1, y,   tileSize);
            testTile(testedTiles, x-1, y-1, tileSize);
            testTile(testedTiles, x,   y-1, tileSize);
        }
    }
    
    /**
     * Renders the tile at the coordinates x*tileSize, y*tileSize.
     * Tries to fetch the tile from cache, on miss creates and prerenders the tile.
     * The tile will not be rendered if it's not in the view frustum.
     * @param x the tile's x offset in the tile grid
     * @param y the tile's y offset in the tile grid
     * @param tileSize the tile grid unit size
     * @return
     */
    private boolean renderTile(int x, int y, int tileSize) {
        // CHECK WHETHER TILE IS IN CACHE
        Coordinates topLeft = new Coordinates(y * tileSize, x * tileSize);
        Tile3D tile = (Tile3D) cache.queryCache(topLeft, tileSize, context.getDetailLevel());
        // IF NOT, CREATE (EMPTY) TILE
        if (tile == null) {
            tile = new Tile3D(topLeft, tileSize, context.getDetailLevel());
            // PRERENDER AND CACHE TILE IF THERE'S A CHANCE THAT TILE IS IN FRUSTUM
            if (tile.isInFrustum(frustum)) {
                tile.prerender();
                Tile3D deletedTile = (Tile3D) cache.addToCache(tile);
                if (deletedTile != null) {
                    deletedTile.freeResources(context.getGL());
                }
            } else {
                return false;
            }
        }
        // RENDER TILE IF IT'S IN THE FRUSTUM
        if (tile.isInFrustum(frustum)) {
            tile.render(context.getGL());  
            return true;
        }
        return false;
    }
    

    /**
     * Draws the current route (retrieved from state).
     */
    private void drawRoute() {
        List<Selection> navNodes = State.getInstance().getNavigationNodes();
        if (navNodes.size() <= 0) {
            return;
        }
        List<Coordinates> route = getRouteCoordinates(State.getInstance().getCurrentRoute(), navNodes);
        // TODO route simplification
        GL gl = context.getGL();
             
        gl.glDisable(GL.GL_TEXTURE_2D);   
        gl.glDisable(GL.GL_DEPTH_TEST);
        // LINE SHADOWS
        gl.glLineWidth(ROUTE_WIDTH);
        gl.glColor3f(0, 0, 0);
        gl.glBegin(GL.GL_LINE_STRIP);
        for (Coordinates pos: route) {    
            drawVertex(gl, pos);
        }
        gl.glEnd();
        // LINE ROUNDED ENDS SHADOWS
        gl.glPointSize(ROUTE_WIDTH);
        gl.glBegin(GL.GL_POINTS);
        for (Coordinates pos: route) {    
            drawVertex(gl, pos);
        }
        gl.glEnd();
        // LINES
        gl.glLineWidth(ROUTE_WIDTH - 2);
        gl.glColor3f(0.315f, 0.05f, 0.478f);
        gl.glBegin(GL.GL_LINE_STRIP);
        for (Coordinates pos: route) {    
            drawVertex(gl, pos);
        }
        gl.glEnd();     
        // LINE ROUNDED ENDS
        gl.glPointSize(ROUTE_WIDTH - 2);
        gl.glBegin(GL.GL_POINTS);
        for (Coordinates pos: route) {    
            drawVertex(gl, pos);
        }
        gl.glEnd();    
        // NAVNODES
        gl.glColor3f(0.8f, 0, 0);
        gl.glPointSize(ROUTE_WIDTH);
        gl.glBegin(GL.GL_POINTS);
        for (Selection navNode: navNodes) {
            drawVertex(gl, navNode.getPosition());
        }
        gl.glEnd();
        gl.glEnable(GL.GL_DEPTH_TEST);

        // FLAGS
        renderFlag(navNodes.get(0).getPosition(), COLOR_STARTNODE, 0.9f);
        if (navNodes.size() > 1) { 
            renderFlag(navNodes.get(navNodes.size() - 1).getPosition(), COLOR_ENDNODE, 0.9f);
        }
        for (int i = 1; i < navNodes.size() - 1; i++) {
            renderFlag(navNodes.get(i).getPosition(), COLOR_WAYNODE, 0.7f);
        }
    }
    
    private List<Coordinates> getRouteCoordinates(List<Integer> routeIDs, List<Selection> navNodes) {
        MapInfo mapInfo = State.getInstance().getMapInfo();
        List<Coordinates> result = new ArrayList<Coordinates>();
        int k = 0;
        for (int i = -1; i < routeIDs.size(); i++) {
            if ((i < 0) || (routeIDs.get(i) < 0)) {
                Selection navNode = navNodes.get(k++);
                Coordinates edgePos = Coordinates.interpolate(
                        mapInfo.getNodePosition(navNode.getFrom()),
                        mapInfo.getNodePosition(navNode.getTo()),
                        navNode.getRatio());
                result.add(edgePos);
                result.add(navNode.getPosition());
                result.add(edgePos);
            } else {
                result.add(mapInfo.getNodePosition(routeIDs.get(i)));
            }
        }
        return result;
    }

    /**
     * Sends a single vertex to OpenGL, using the height at <code>point</code> as z coordinate.
     * @param gl the active OpenGL context
     * @param point the vertex x and y coordinates
     */
    private void drawVertex(GL gl, Coordinates point) {
        gl.glVertex3f(point.getLongitude(), point.getLatitude(),
                heightmap.getHeight(projection.getGeoCoordinates(point)) + ROUTE_HEIGHT_OFFSET);
    }
    
    /**
     * Draws a colored and scaled mark at the given position.
     * @param position the mark position
     * @param color the mark's color
     * @param size the mark's size
     */
    private void renderFlag(Coordinates position, float[] color, float size) {
        float height = heightmap.getHeight(projection.getGeoCoordinates(position));
        GL gl = context.getGL();
        gl.glPushMatrix();
        double[] model = new double[16];
        gl.glGetDoublev(GL_MODELVIEW_MATRIX, model, 0);
        double zoomH = 0.08 / Math.sqrt((model[0] * model[0]) + (model[1] * model[1]) + (model[2] * model[2]));
        double zoomZ = 0.08 / Math.sqrt((model[8] * model[8]) + (model[9] * model[9]) + (model[10] * model[10]));
        gl.glTranslatef(position.getLongitude(), position.getLatitude(), height);
        gl.glScaled(zoomH * size, zoomH * size, zoomZ * size);
        gl.glDisable(GL_TEXTURE_2D);
        
        //gl.glEnable(GL_LIGHTING);
        GLU glu = new GLU();
        GLUquadric quadric = glu.gluNewQuadric();
        gl.glColor3f(0.3f, 0.3f, 0.3f);
        glu.gluCylinder(quadric, 0.1, 0.1, 3, 7, 1);
        gl.glTranslatef(0, 0, 3);
        gl.glColor3f(0.5f, 0.5f, 0.5f);
        glu.gluDisk(quadric, 0, 0.1, 7, 1);
        gl.glTranslatef(0, 0, -3);
        glu.gluDeleteQuadric(quadric);
        //gl.glDisable(GL_LIGHTING);
        
        gl.glColor3f(color[0], color[1], color[2]);
        gl.glBegin(GL_TRIANGLES);  
            gl.glVertex3f(0, 0, 2);
            gl.glVertex3f(0, 0, 3);
            gl.glVertex3f(1.5f, 0, 2.5f);
        gl.glEnd();
        gl.glPopMatrix();
    }      

    @Override
    public void resetCache() {
        cacheResetScheduled = true;
    }

}
