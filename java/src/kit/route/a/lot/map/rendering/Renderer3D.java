package kit.route.a.lot.map.rendering;

import static javax.media.opengl.GL.*;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import org.apache.log4j.Logger;

import kit.route.a.lot.common.Context3D;
import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Context;
import kit.route.a.lot.common.Frustum;
import kit.route.a.lot.common.OSMType;
import kit.route.a.lot.common.Projection;
import kit.route.a.lot.common.ProjectionFactory;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.common.Util;
import kit.route.a.lot.controller.State;
import kit.route.a.lot.heightinfo.IHeightmap;
import kit.route.a.lot.map.Area;
import kit.route.a.lot.map.MapElement;
import kit.route.a.lot.map.Node;
import kit.route.a.lot.map.POINode;
import kit.route.a.lot.map.Street;
import kit.route.a.lot.map.infosupply.MapInfo;
import kit.route.a.lot.map.rendering.Renderer;

public class Renderer3D extends Renderer {

    private static final Logger logger = Logger.getLogger(Renderer3D.class);
    
    private static final float
        HEIGHT_SCALE_FACTOR = 0.6f, // factor multiplied on all height values
        VIEW_HEIGHT_ADAPTION = 0.2f, // [0..1] how fast camera height adapts to ground height
        ROUTE_HEIGHT_OFFSET = 10f, // [meters] height value added to route display
        ROUTE_WIDTH = 10f; // [pixels] width of the line that is used for route display
    
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
        gl.glScalef(1, 1, HEIGHT_SCALE_FACTOR * (context.getDetailLevel() + 1));
        float centerHeight = heightmap.getHeight(projection.getGeoCoordinates(center));
        viewHeight = (viewHeight == Float.NEGATIVE_INFINITY) ? centerHeight
                : Util.interpolate(viewHeight, centerHeight, VIEW_HEIGHT_ADAPTION);
        gl.glTranslatef(0, 0, -viewHeight);     
        
        // TEST AND RENDER TILES, STARTING AT THE CENTRAL TILE
        frustum = new Frustum(gl);
        tilesRendered = 0;
        Set<Point> testedTiles = new HashSet<Point>();
        testTile(testedTiles,
                (int) (center.getLongitude() / tileSize),
                (int) (center.getLatitude() / tileSize), tileSize);
        logger.debug("Tiles rendered/tested: " + tilesRendered + " / " + testedTiles.size());
        drawRoute();
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
        Point pos = new Point(x, y);
        if (testedTiles.contains(pos)) {
            return;
        }
        testedTiles.add(pos);
        // TEST (AND IF POSITIVE RENDER) TILE
        boolean rendered = renderTile(x, y, tileSize);
        // TEST NEIGHBORING 8 TILES
        if (rendered) {
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
        List<Selection> navPoints = State.getInstance().getNavigationNodes();
        if (navPoints.size() <= 0) {
            return;
        }
        List<Integer> route = State.getInstance().getCurrentRoute();
        MapInfo mapInfo = State.getInstance().getLoadedMapInfo();
        GL gl = context.getGL();

        // Copy route and navPoints into fullRouteList
        List<Node> fullRouteList = new ArrayList<Node>(route.size());
        Iterator<Selection> navNodes = navPoints.iterator();
        // add starting point
        Selection selection = navNodes.next();
        fullRouteList.add(new Node(selection.getPosition()));
        fullRouteList.add(new Node(Coordinates.interpolate(
                mapInfo.getNode(selection.getFrom()).getPos(),
                mapInfo.getNode(selection.getTo()).getPos(),
                selection.getRatio())));
        selection = (navNodes.hasNext()) ? navNodes.next() : null;
        // add other navnodes / route nodes
        for (int i = 0; i < route.size(); i++) {
            int currentRouteNode = route.get(i);
            fullRouteList.add(mapInfo.getNode(currentRouteNode));
            if (selection != null && (currentRouteNode == selection.getFrom()
                    || currentRouteNode == selection.getTo())) {
                Coordinates nodeOnEdge = Coordinates.interpolate(
                        mapInfo.getNode(selection.getFrom()).getPos(),
                        mapInfo.getNode(selection.getTo()).getPos(),
                        selection.getRatio());
                fullRouteList.add(new Node(nodeOnEdge));
                fullRouteList.add(new Node(selection.getPosition()));
                fullRouteList.add(new Node(nodeOnEdge));
                selection = (navNodes.hasNext()) ? navNodes.next() : null;
            }         
        }
        
        // Simplify route TODO redo
        Node[] fullRoute = Street.simplifyNodes(fullRouteList.toArray(new Node[fullRouteList.size()]),
                Projection.getZoomFactor(context.getDetailLevel()) * 3);
        
        gl.glDisable(GL.GL_TEXTURE_2D);   
        gl.glDisable(GL.GL_DEPTH_TEST);
        // LINE SHADOWS
        gl.glLineWidth(ROUTE_WIDTH);
        gl.glColor3f(0, 0, 0);
        gl.glBegin(GL.GL_LINE_STRIP);
        for (Node node: fullRoute) {    
            drawVertex(gl, node.getPos());
        }
        gl.glEnd();
        // LINE ROUNDED ENDS SHADOWS
        gl.glPointSize(ROUTE_WIDTH);
        gl.glBegin(GL.GL_POINTS);
        for (Node node: fullRoute) {
            drawVertex(gl, node.getPos());
        }
        gl.glEnd();
        // LINES
        gl.glLineWidth(ROUTE_WIDTH - 2);
        gl.glColor3f(0.315f, 0.05f, 0.478f);
        gl.glBegin(GL.GL_LINE_STRIP);
        for (Node node: fullRoute) {    
            drawVertex(gl, node.getPos());
        }
        gl.glEnd();     
        // LINE ROUNDED ENDS
        gl.glPointSize(ROUTE_WIDTH - 2);
        gl.glBegin(GL.GL_POINTS);
        for (Node node: fullRoute) {
            drawVertex(gl, node.getPos());
        }
        gl.glEnd();    
        // NAVNODES
        gl.glColor3f(0.8f, 0, 0);
        gl.glPointSize(ROUTE_WIDTH);
        gl.glBegin(GL.GL_POINTS);
        for (Selection navNode: navPoints) {
            drawVertex(gl, navNode.getPosition());
        }
        gl.glEnd();
        gl.glEnable(GL.GL_DEPTH_TEST);

        // FLAGS
        renderFlag(navPoints.get(0).getPosition(), new float[]{0, 0.8f, 0}, 0.9f);
        if (navPoints.size() > 1) { 
            renderFlag(navPoints.get(navPoints.size() - 1).getPosition(),
                    new float[]{0.8f, 0, 0}, 0.9f);
        }
        for (int i = 1; i < navPoints.size() - 1; i++) {
            renderFlag(navPoints.get(i).getPosition(), new float[]{1, 1, 0}, 0.7f);
        }
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
