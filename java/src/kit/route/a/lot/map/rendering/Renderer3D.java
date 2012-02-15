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

    private static final float HEIGHT_SCALE_FACTOR = 1f;
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
    public void render(Context context, int detail) {
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
        Selection selection = navNodes.next();
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
    }
    
    private static void drawVertex(GL gl, Projection projection, Coordinates point) {
        gl.glVertex3f(point.getLongitude(), point.getLatitude(),
                State.getInstance().getLoadedHeightmap().getHeight(
                        projection.localCoordinatesToGeoCoordinates(point))
                + ROUTE_HEIGHT_OFFSET);
    }
       
}
