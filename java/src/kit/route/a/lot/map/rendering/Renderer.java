package kit.route.a.lot.map.rendering;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Context;
import kit.route.a.lot.common.Projection;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.controller.State;
import kit.route.a.lot.map.Node;
import kit.route.a.lot.map.POINode;
import kit.route.a.lot.map.Street;
import kit.route.a.lot.map.infosupply.MapInfo;
import kit.route.a.lot.map.rendering.Renderer;
import kit.route.a.lot.map.rendering.RenderCache;

public class Renderer {

    /**
     * A cache storing tiles that were previously drawn.
     */
    private RenderCache cache;
    private static Logger logger = Logger.getLogger(Renderer.class);

    protected State state = State.getInstance();

    protected ArrayList<Selection> drawEdges = new ArrayList<Selection>(); //TODO delete
    
    /**
     * Creates a new renderer.
     */
    public Renderer() {
        cache = new HashRenderCache();
    }

    /**
     * Renders a map viewing rectangle using the given rendering context.
     * 
     * @param context
     *            the rendering context
     * @param detail
     *            level of detail of the map view
     */
    public void render(Context context, int detail) {
        int tileDim = (int) (200 * Projection.getZoomFactor(detail));
        if (tileDim < 0) {
            logger.error("tileDim < 0 => seems like an overflow");
        }
        int maxLon = (int) Math.floor(context.getBottomRight().getLongitude() / tileDim);
        int maxLat = (int) Math.floor(context.getBottomRight().getLatitude() / tileDim);
        int minLon = (int) Math.floor(context.getTopLeft().getLongitude() / tileDim) - 1;
        int minLat = (int) Math.floor(context.getTopLeft().getLatitude() / tileDim) - 1;
        for (int i = minLon; i <= maxLon; i++) {
            for (int k = minLat; k <= maxLat; k++) {
                Coordinates topLeft = new Coordinates(k * tileDim, i * tileDim);
                Coordinates bottomRight = new Coordinates((k + 1) * tileDim, (i + 1) * tileDim);
                Tile currentTile = prerenderTile(topLeft, bottomRight, detail);
                context.drawImage(topLeft, currentTile.getData(), detail);
            }
        }
        drawRoute(context, detail);
        drawNavPoints(context, detail);
    }


    /**
     * If necessary, renders and caches the tile with the specified data
     * 
     * @return the rendered tile
     */
    private Tile prerenderTile(Coordinates topLeft, Coordinates bottomRight, int detail) {
        Tile tile = cache.queryCache(Tile.getSpecifier(topLeft, detail));
        if (tile == null) {
            tile = new Tile(topLeft, bottomRight, detail);
            tile.prerender(state);
            cache.addToCache(tile);
        }
        return tile;
    }

    /**
     * Chooses an so far uncached tile in proximity of the visible map viewing rectangle, subsequently drawing
     * and caching it.
     * 
     * @return true if a tile was drawn
     */
    public boolean prerenderIdle() {
        return false; // TODO: implement
    }
    
    private BufferedImage routeImage = null;
    private Coordinates routeTopLeft = new Coordinates();
    private Integer[] drawnRoute = new Integer[0];
    private int drawnRouteDetail = -1;
    private static final int routeSize = 16;

    /**
     * Draws the given route on the given rendering context.
     * 
     */
    private void drawRoute(Context context, int detail) {
        List<Integer> route = state.getCurrentRoute();
        List<Selection> navPoints = state.getNavigationNodes();
        
        if (route != null && !routeIsEqual(route.toArray(new Integer[route.size()]), drawnRoute) || drawnRouteDetail != detail) {
            drawnRoute = route.toArray(new Integer[route.size()]);
            drawnRouteDetail = detail;
            
            if (drawnRoute.length > 0) {
                MapInfo mapInfo = state.getLoadedMapInfo();
                Node[] routeNodes = new Node[drawnRoute.length];
                routeTopLeft = new Coordinates(Float.MAX_VALUE, Float.MAX_VALUE);
                Coordinates routeBottomRight = new Coordinates(Float.MIN_VALUE, Float.MIN_VALUE);
                
                // find route bounding rectangle dimensions
                for (int i = 0; i < routeNodes.length; i++) {
                    routeNodes[i] = mapInfo.getNode(drawnRoute[i]);
                    adjustBorderCoordinates(routeTopLeft, routeBottomRight, routeNodes[i].getPos());
                }
                for (Selection navSelection : navPoints) {
                    Node from = mapInfo.getNode(navSelection.getFrom());
                    Node to = mapInfo.getNode(navSelection.getTo());
                    Coordinates nodeOnEdge = getSelectedNodeOnEdge(from, to, navSelection.getRatio());
                    adjustBorderCoordinates(routeTopLeft, routeBottomRight, nodeOnEdge);
                    adjustBorderCoordinates(routeTopLeft, routeBottomRight, navSelection.getPosition());
                }

                // define bounding rectangle TODO: this can be very inefficient
                int width = (int) Math.abs(routeTopLeft.getLongitude() - routeBottomRight.getLongitude()) / Projection.getZoomFactor(detail);
                int height = (int) Math.abs(routeTopLeft.getLatitude() - routeBottomRight.getLatitude()) / Projection.getZoomFactor(detail);
                routeImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                
                // configure rendering context
                Graphics2D graphics = routeImage.createGraphics();
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                graphics.setComposite(AlphaComposite.Src);
                graphics.setColor(new Color(0, true));
                graphics.fillRect(0, 0, width, height);
                
                // TODO refactor: its not very good using the method from Street better would be for example Util again
                routeNodes = Street.simplifyNodes(routeNodes, Projection.getZoomFactor(detail) * 3);
                int currentRouteSize = routeSize / Projection.getZoomFactor(detail);
                
                // draw route shadow
                graphics.setStroke(new BasicStroke(currentRouteSize + 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                graphics.setColor(new Color(0xaa076a07, true));
                for (int i = 1; i < routeNodes.length; i++) {
                    drawLineBetweenCoordinates(routeNodes[i-1].getPos(), routeNodes[i].getPos(), detail, graphics);
                }
                for (Selection navSelection : navPoints) {
                    Node from = mapInfo.getNode(navSelection.getFrom());
                    Node to = mapInfo.getNode(navSelection.getTo());
                    Coordinates nodeOnEdge = getSelectedNodeOnEdge(from, to, navSelection.getRatio());
                    if (idIsInRoute(from.getID(), drawnRoute)) {
                        drawLineBetweenCoordinates(from.getPos(), nodeOnEdge, detail, graphics);
                    }
                    if (idIsInRoute(to.getID(), drawnRoute)) {
                        drawLineBetweenCoordinates(to.getPos(), nodeOnEdge, detail, graphics);
                    }
                    drawLineBetweenCoordinates(nodeOnEdge, navSelection.getPosition(), detail, graphics);
                }
                
                // draw route
                graphics.setStroke(new BasicStroke(currentRouteSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                graphics.setColor(new Color(0xaa2d0b7b, true));
                for (int i = 1; i < routeNodes.length; i++) {
                    drawLineBetweenCoordinates(routeNodes[i-1].getPos(), routeNodes[i].getPos(), detail, graphics);
                }
                
                for (Selection sel : drawEdges) {   //delete
                    drawLineBetweenCoordinates(State.getInstance().getLoadedMapInfo().getNodePosition(sel.getFrom()),
                            State.getInstance().getLoadedMapInfo().getNodePosition(sel.getFrom()), detail, graphics);
                }
                
                for (Selection navSelection : navPoints) {
                    Node from = mapInfo.getNode(navSelection.getFrom());
                    Node to = mapInfo.getNode(navSelection.getTo());
                    Coordinates nodeOnEdge = getSelectedNodeOnEdge(from, to, navSelection.getRatio());
                    if (idIsInRoute(from.getID(), drawnRoute)) {
                        drawLineBetweenCoordinates(from.getPos(), nodeOnEdge, detail, graphics);
                    }
                    if (idIsInRoute(to.getID(), drawnRoute)) {
                        drawLineBetweenCoordinates(to.getPos(), nodeOnEdge, detail, graphics);
                    }
                    drawLineBetweenCoordinates(nodeOnEdge, navSelection.getPosition(), detail, graphics);
                }
                
//                // draw route nodes
//                graphics.setColor(Color.CYAN);
//                int currentNodeSize = currentRouteSize + 4;
//                for (int i = 0; i < routeNodes.length; i++) {
//                    int curX = (int) (routeNodes[i].getPos().getLongitude() - routeTopLeft.getLongitude()) / Projection.getZoomFactor(detail);
//                    int curY = (int) (routeNodes[i].getPos().getLatitude() - routeTopLeft.getLatitude()) / Projection.getZoomFactor(detail);
//                    graphics.fillOval(curX - currentNodeSize/2, curY - currentNodeSize/2, currentNodeSize, currentNodeSize);
//                }
                
                graphics.dispose();
            } else {
                routeImage = null;
            }
        }
        
        if (routeImage != null) {
            context.drawImage(routeTopLeft, routeImage, detail);
        }
    }
    
    public void drawEdge(Selection sel){    //TODO delete
        drawEdges.add(sel);
    }
    
    private boolean idIsInRoute(int id, Integer[] route) {
        for (int i = 0; i < route.length; i++) {
            if (route[i].equals(id)) {
                return true;
            }
        }
        return false;
    }
    
    private void adjustBorderCoordinates(Coordinates topLeft, Coordinates bottomRight, Coordinates point) {
        float curLat = point.getLatitude();
        float curLon = point.getLongitude();
        int buffer = routeSize;
        if (curLat - buffer < topLeft.getLatitude()) {
            topLeft.setLatitude(curLat - buffer);
        }
        if (curLat + buffer > bottomRight.getLatitude()) {
            bottomRight.setLatitude(curLat + buffer);
        }
        if (curLon - buffer < topLeft.getLongitude()) {
            topLeft.setLongitude(curLon - buffer);
        }
        if (curLon + buffer > bottomRight.getLongitude()) {
            bottomRight.setLongitude(curLon + buffer);
        }
    }

    private void drawLineBetweenCoordinates(Coordinates from, Coordinates to, int detail, Graphics2D graphics) {
        Coordinates start = getLocalCoordinatesFromGlobalCoordinates(from, routeTopLeft, detail);
        Coordinates end = getLocalCoordinatesFromGlobalCoordinates(to, routeTopLeft, detail);
        graphics.drawLine((int) start.getLongitude(), (int) start.getLatitude(), (int) end.getLongitude(), (int) end.getLatitude());
    }
    
    private boolean routeIsEqual(Integer[] route1, Integer[] route2) {
        if (route1.length != route2.length) {
            return false;
        }
        for (int i = 0; i < route1.length; i++) {
            if (!route1[i].equals(route2[i])) {
                return false;
            }
        }
        return true;
    }
    
    protected static Coordinates getLocalCoordinatesFromGlobalCoordinates(Coordinates global, Coordinates topLeft, int detail) {
        Coordinates localCoordinates = new Coordinates();
        localCoordinates.setLatitude((float) ((global.getLatitude() - topLeft.getLatitude()) / Projection
                .getZoomFactor(detail)));
        localCoordinates.setLongitude((float) ((global.getLongitude() - topLeft.getLongitude()) / Projection
                .getZoomFactor(detail)));
        return localCoordinates;
    }
    
    private Coordinates getSelectedNodeOnEdge(Node from, Node to, float ratio) {
        Coordinates node = new Coordinates();
        Coordinates vector = new Coordinates();
        vector.setLatitude((to.getPos().getLatitude() - from.getPos().getLatitude()) * ratio);
        vector.setLongitude((to.getPos().getLongitude() - from.getPos().getLongitude()) * ratio);
        node.setLatitude(from.getPos().getLatitude() + vector.getLatitude());
        node.setLongitude(from.getPos().getLongitude() + vector.getLongitude());
        return node;
    }

    /**
     * Draws a point of interest on the current rendering context.
     * 
     * @param poi
     *            the POI to be drawn
     */
    @SuppressWarnings("unused")
    private void drawPOI(POINode poi) {
    }
    
    private void drawNavPoints(Context context, int detail) {
        List<Selection> points = state.getNavigationNodes();
        for (Selection point : points) {
            BufferedImage image = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = image.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
//            graphics.setColor(Color.ORANGE);
//            graphics.fillOval(0, 0, 5, 5);
//            Node from = state.getLoadedMapInfo().getNode(point.getFrom());
//            Node to = state.getLoadedMapInfo().getNode(point.getTo());
//            context.drawImage(from.getPos(), image, detail);
//            context.drawImage(to.getPos(), image, detail);
            
//            Coordinates selectedNodeOnEdge = getSelectedNodeOnEdge(from, to, point.getRatio());
//            graphics.setColor(Color.CYAN);
//            graphics.fillOval(0, 0, 5, 5);
//            context.drawImage(selectedNodeOnEdge, image, detail);

            graphics.setColor(Color.RED);
            graphics.fillOval(0, 0, 5, 5);
            context.drawImage(point.getPosition(), image, detail);
        }
    }

    /**
     * Adopts the cache from another renderer.
     * 
     * @param source
     */
    public void inheritCache(Renderer source) {
        this.cache = source.cache;
    }

    public void resetRenderCache() {
        cache.resetCache();
    }

}
