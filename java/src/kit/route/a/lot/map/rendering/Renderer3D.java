package kit.route.a.lot.map.rendering;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.media.opengl.GL;
import org.apache.log4j.Logger;

import kit.route.a.lot.common.Context3D;
import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Context;
import kit.route.a.lot.common.Frustum;
import kit.route.a.lot.common.Projection;
import kit.route.a.lot.common.ProjectionFactory;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.common.Util;
import kit.route.a.lot.controller.State;
import kit.route.a.lot.heightinfo.IHeightmap;
import kit.route.a.lot.map.Node;
import kit.route.a.lot.map.Street;
import kit.route.a.lot.map.infosupply.MapInfo;
import kit.route.a.lot.map.rendering.Renderer;

public class Renderer3D extends Renderer {

    private static final float HEIGHT_SCALE_FACTOR = 0.5f;
    private static final float VIEW_HEIGHT_ADAPTION = 0.2f;
    private static final float ROUTE_HEIGHT_OFFSET = 10f, ROUTE_WIDTH = 10f;
    private static Logger logger = Logger.getLogger(Renderer3D.class);
    private float viewHeight = Float.NEGATIVE_INFINITY;
    
    /**
     * Renders a map viewing rectangle in three dimensional form,
     * using height data and perspective projection in the process.
     * 
     * @param detail level of detail of the map view
     * @param topLeft north western corner of the viewing rectangle
     * @param bottomRight south eastern corner of the viewing rectangle
     * @param renderingContext an OpenGL rendering context
     */
    @Override
    public void render(Context context) {
        int detail = context.getZoomlevel();
        int tileDim = (int) (BASE_TILEDIM * Projection.getZoomFactor(detail));
        if (tileDim < 0) {
            logger.error("tileDim < 0 => seems like an overflow");
        }
        Context3D context3D = (Context3D) context;
        Coordinates center = context.getBottomRight().clone().add(context.getTopLeft()).scale(0.5f);
        int lat = (int) Math.floor(center.getLatitude() / tileDim) - 1;
        int lon = (int) Math.floor(center.getLongitude() / tileDim);
        
        GL gl = context3D.getGL();
        gl.glScalef(1, 1, HEIGHT_SCALE_FACTOR * (detail + 1));
        Projection projection = ProjectionFactory.getProjectionForCurrentMap();
        IHeightmap heightmap = State.getInstance().getLoadedHeightmap();
        float centerHeight = heightmap.getHeight(projection.localCoordinatesToGeoCoordinates(center));
        viewHeight = (viewHeight == Float.NEGATIVE_INFINITY) ? centerHeight
                : Util.interpolate(viewHeight, centerHeight, VIEW_HEIGHT_ADAPTION);
        gl.glTranslatef(0, 0, -viewHeight);      
        Frustum frustum = new Frustum(gl);
        renderTile(gl, frustum, lon, lat, tileDim, detail);
        int radius = 1;
        boolean found = true;
        while (found) {
            found = false;
            int y1 = lat-radius;
            int y2 = lat+radius;
            int x1 = lon-radius;
            int x2 = lon+radius;
            for (int x = x1; x <= x2; x++) {
                if (renderTile(gl, frustum, x, y1, tileDim, detail)) {
                    found = true;
                }
                if (renderTile(gl, frustum, x, y2, tileDim, detail)) {
                    found = true;
                }     
            }
            for (int y = y1+1; y < y2; y++) {
                if (renderTile(gl, frustum, x1, y, tileDim, detail)) {
                    found = true;
                }
                if (renderTile(gl, frustum, x2, y, tileDim, detail)) {
                    found = true;
                }       
            }
            radius++;
        }      
        drawRoute(context3D, detail);
    }
       
    private boolean renderTile(GL gl, Frustum frustum, int x, int y, int tileDim, int detail) {
        Coordinates topLeft = new Coordinates(y * tileDim, x * tileDim);
        Tile3D tile = (Tile3D) cache.queryCache(topLeft, detail);
        if (tile == null) {
            tile = new Tile3D(topLeft, tileDim, detail);
            if (tile.isInFrustum(frustum)) {
                tile.prerender();
                Tile3D deletedTile = (Tile3D) cache.addToCache(tile);
                if (deletedTile != null) {
                    deletedTile.freeResources(gl);
                }
            } else {
                return false;
            }
        }
        if (tile.isInFrustum(frustum)) {
            tile.render(gl);  
            return true;
        }
        return false;
    }
    
    private void drawRoute(Context3D context, int detail) {
        List<Selection> navPoints = State.getInstance().getNavigationNodes();
        if (navPoints.size() <= 0) {
            return;
        }
        List<Integer> route = State.getInstance().getCurrentRoute();
        MapInfo mapInfo = State.getInstance().getLoadedMapInfo();
        Projection projection = ProjectionFactory.getProjectionForCurrentMap();
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
                Projection.getZoomFactor(detail) * 3);
        
        gl.glDisable(GL.GL_TEXTURE_2D);   
        gl.glDisable(GL.GL_DEPTH_TEST);
        // LINE SHADOWS
        gl.glLineWidth(ROUTE_WIDTH);
        gl.glColor3f(0, 0, 0);
        gl.glBegin(GL.GL_LINE_STRIP);
        for (Node node: fullRoute) {    
            drawVertex(gl, projection, node.getPos());
        }
        gl.glEnd();
        // LINE ROUNDED ENDS SHADOWS
        gl.glPointSize(ROUTE_WIDTH);
        gl.glBegin(GL.GL_POINTS);
        for (Node node: fullRoute) {
            drawVertex(gl, projection, node.getPos());
        }
        gl.glEnd();
        // LINES
        gl.glLineWidth(ROUTE_WIDTH - 2);
        gl.glColor3f(0.315f, 0.05f, 0.478f);
        gl.glBegin(GL.GL_LINE_STRIP);
        for (Node node: fullRoute) {    
            drawVertex(gl, projection, node.getPos());
        }
        gl.glEnd();     
        // LINE ROUNDED ENDS
        gl.glPointSize(ROUTE_WIDTH - 2);
        gl.glBegin(GL.GL_POINTS);
        for (Node node: fullRoute) {
            drawVertex(gl, projection, node.getPos());
        }
        gl.glEnd();    
        // NAVNODES
        gl.glColor3f(0.8f, 0, 0);
        gl.glPointSize(ROUTE_WIDTH);
        gl.glBegin(GL.GL_POINTS);
        for (Selection navNode: navPoints) {
            drawVertex(gl, projection, navNode.getPosition());
        }
        gl.glEnd();
        gl.glEnable(GL.GL_DEPTH_TEST);

        // FLAGS
        renderFlag(context, projection, navPoints.get(0).getPosition(), new float[]{0, 0.8f, 0}, 10f);
        if (navPoints.size() > 1) { 
            renderFlag(context, projection, navPoints.get(navPoints.size() - 1).getPosition(),
                    new float[]{0.8f, 0, 0}, 10f);
        }
        for (int i = 1; i < navPoints.size() - 1; i++) {
            renderFlag(context, projection, navPoints.get(i).getPosition(), new float[]{1, 1, 0}, 5f);
        }
    }
    
    private static void drawVertex(GL gl, Projection projection, Coordinates point) {
        gl.glVertex3f(point.getLongitude(), point.getLatitude(),
                State.getInstance().getLoadedHeightmap().getHeight(
                        projection.localCoordinatesToGeoCoordinates(point))
                + ROUTE_HEIGHT_OFFSET);
    }
    
    private static void renderFlag(Context3D context, Projection projection, Coordinates position, float[] color, float size) {
        float height = State.getInstance().getLoadedHeightmap().getHeight(
                        projection.localCoordinatesToGeoCoordinates(position));
        GL gl = context.getGL();
        gl.glPushMatrix();
        gl.glTranslatef(position.getLongitude(), position.getLatitude(), height);
        float zoom = context.getZoomlevel() * context.getZoomlevel();
        gl.glScalef(size * zoom, size * zoom, size * zoom);
        gl.glBegin(GL.GL_TRIANGLE_FAN);  
            gl.glColor3f(1, 1, 1);
            gl.glVertex3f(0, 0, 0);
            gl.glColor3f(color[0], color[1], color[2]);
            gl.glVertex3f(-1, -1, 1);
            gl.glVertex3f(-1, 1, 1);
            gl.glVertex3f(1, 1, 1);
            gl.glVertex3f(1, -1, 1);
            gl.glVertex3f(-1, -1, 1);
        gl.glEnd();
        gl.glPopMatrix();
    }      
}
