package kit.route.a.lot.map.rendering;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Context;
import kit.route.a.lot.common.Projection;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.controller.State;
import kit.route.a.lot.map.Area;
import kit.route.a.lot.map.MapElement;
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
    
    private BufferedImage routeImage = null;
    private Coordinates routeTopLeft = new Coordinates();
    private Coordinates routeBottomRight = new Coordinates();
    private Integer[] drawnRoute = new Integer[0];
    private int drawnRouteDetail = -1;
    private static final int drawBuffer = 200;
    private static final float routeSize = 20;
    
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
        drawOverlay(context, detail);
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
     * 
     */
    public boolean prerenderIdle() {
        return false; // TODO: implement
    }

    /**
     * Draws the given route on the given rendering context.
     * 
     */
    private void drawRoute(Context context, int detail) {
        List<Integer> route = state.getCurrentRoute();
        List<Selection> navPoints = state.getNavigationNodes();
        
        boolean mustDrawRoute = route != null && !routeIsEqual(route.toArray(new Integer[route.size()]), drawnRoute);
        mustDrawRoute = mustDrawRoute || drawnRouteDetail != detail;
        mustDrawRoute = mustDrawRoute || routeTopLeft.getLatitude() >= context.getTopLeft().getLatitude()
                || routeTopLeft.getLongitude() >= context.getTopLeft().getLongitude();
        mustDrawRoute = mustDrawRoute || routeBottomRight.getLatitude() <= context.getBottomRight().getLatitude()
                || routeBottomRight.getLongitude() <= context.getBottomRight().getLongitude();
        
        if (mustDrawRoute) {
            drawnRoute = route.toArray(new Integer[route.size()]);
            drawnRouteDetail = detail;
            
            if (drawnRoute.length > 0) {
                MapInfo mapInfo = state.getLoadedMapInfo();
                Node[] routeNodes = new Node[drawnRoute.length];
                routeTopLeft = new Coordinates(Float.MAX_VALUE, Float.MAX_VALUE);
                routeBottomRight = new Coordinates(Float.MIN_VALUE, Float.MIN_VALUE);
                
                // find route bounding rectangle dimensions
                for (int i = 0; i < routeNodes.length; i++) {
                    routeNodes[i] = mapInfo.getNode(drawnRoute[i]);
                    adjustBorderCoordinates(routeTopLeft, routeBottomRight, routeNodes[i].getPos(), detail);
                }
                for (Selection navSelection : navPoints) {
                    Node from = mapInfo.getNode(navSelection.getFrom());
                    Node to = mapInfo.getNode(navSelection.getTo());
                    Coordinates nodeOnEdge = getSelectedNodeOnEdge(from, to, navSelection.getRatio());
                    adjustBorderCoordinates(routeTopLeft, routeBottomRight, nodeOnEdge, detail);
                    adjustBorderCoordinates(routeTopLeft, routeBottomRight, navSelection.getPosition(), detail);
                }
                
                // adjust border coordinates if the route is bigger than the context window
                if (routeTopLeft.getLatitude() < context.getTopLeft().getLatitude() - drawBuffer) {
                    routeTopLeft.setLatitude(context.getTopLeft().getLatitude() - drawBuffer);
                }
                if (routeTopLeft.getLongitude() < context.getTopLeft().getLongitude() - drawBuffer) {
                    routeTopLeft.setLongitude(context.getTopLeft().getLongitude() - drawBuffer);
                }
                if (routeBottomRight.getLatitude() > context.getBottomRight().getLatitude() + drawBuffer) {
                    routeBottomRight.setLatitude(context.getBottomRight().getLatitude() + drawBuffer);
                }
                if (routeBottomRight.getLongitude() > context.getBottomRight().getLongitude() + drawBuffer) {
                    routeBottomRight.setLongitude(context.getBottomRight().getLongitude() + drawBuffer);
                }

                int width = (int) Math.abs(routeTopLeft.getLongitude() - routeBottomRight.getLongitude()) / Projection.getZoomFactor(detail);
                int height = (int) Math.abs(routeTopLeft.getLatitude() - routeBottomRight.getLatitude()) / Projection.getZoomFactor(detail);
                if (width == 0 || height == 0) {
                    routeImage = null;
                    return;
                }
                routeImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                
                // configure rendering context
                Graphics2D graphics = routeImage.createGraphics();
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                graphics.setComposite(AlphaComposite.Src);
                graphics.setColor(new Color(0, true));
                graphics.fillRect(0, 0, width, height);
                
                // TODO refactor: its not very good using the method from Street better would be for example Util again
                routeNodes = Street.simplifyNodes(routeNodes, Projection.getZoomFactor(detail) * 3);
                float currentRouteSize = routeSize / Projection.getZoomFactor(detail / 2);
                float currentRouteSizeUnderlay = (routeSize + 2) / Projection.getZoomFactor(detail / 2);
                
                // draw route shadow
                graphics.setStroke(new BasicStroke(currentRouteSizeUnderlay, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                graphics.setColor(new Color(0x9a076a07, true));
                drawRouteLines(routeNodes, navPoints, detail, graphics, mapInfo);
                
                // draw route
                graphics.setStroke(new BasicStroke(currentRouteSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                graphics.setColor(new Color(0x9a370d94, true));
                drawRouteLines(routeNodes, navPoints, detail, graphics, mapInfo);
                
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

    private void drawRouteLines(Node[] routeNodes, List<Selection> navPoints, int detail,
            Graphics2D graphics, MapInfo mapInfo) {
        for (int i = 1; i < routeNodes.length; i++) {
            drawLineBetweenCoordinates(routeNodes[i-1].getPos(), routeNodes[i].getPos(), detail, graphics);
        }
        for (int i = 0; i < navPoints.size(); i++) {
            Selection navSelection = navPoints.get(i);
            Node from = mapInfo.getNode(navSelection.getFrom());
            Node to = mapInfo.getNode(navSelection.getTo());
            Coordinates nodeOnEdge = getSelectedNodeOnEdge(from, to, navSelection.getRatio());
            boolean drawedFrom = false;
            boolean drawedTo = false;
            if (idIsInRoute(from.getID(), drawnRoute)) {
                drawLineBetweenCoordinates(from.getPos(), nodeOnEdge, detail, graphics);
                drawedFrom = true;
            }
            if (idIsInRoute(to.getID(), drawnRoute)) {
                drawLineBetweenCoordinates(to.getPos(), nodeOnEdge, detail, graphics);
                drawedTo = true;
            }
            if (drawedFrom && drawedTo && (i == 0 || i == navPoints.size() - 1)) {
                logger.error("Drawed whole selection a the end of the route.");
            }
            drawLineBetweenCoordinates(nodeOnEdge, navSelection.getPosition(), detail, graphics);
        }
    }
    
    private boolean idIsInRoute(int id, Integer[] route) {
        for (int i = 0; i < route.length; i++) {
            if (route[i].equals(id)) {
                return true;
            }
        }
        return false;
    }
    
    private void adjustBorderCoordinates(Coordinates topLeft, Coordinates bottomRight, Coordinates point, int detail) {
        float curLat = point.getLatitude();
        float curLon = point.getLongitude();
        float buffer = routeSize * Projection.getZoomFactor(detail / 2);
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
     */
    private void drawOverlay(Context context, int detail) {
        MapInfo mapInfo = State.getInstance().getLoadedMapInfo();
        Collection<MapElement> elements = mapInfo.getOverlay(detail, context.getTopLeft(), context.getBottomRight());

        int size = 8;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setComposite(AlphaComposite.Src);
        graphics.setColor(new Color(0, true));
        graphics.fillRect(0, 0, size, size);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setColor(Color.ORANGE);
        graphics.fillOval(0, 0, size, size);

        for (MapElement element : elements) {
            if (element instanceof Node) {
                if (element instanceof POINode && (((POINode) element).getInfo().getName() == null 
                        || ((POINode) element).getInfo().getName().equals(""))){
                    continue;
                }
                Node node = (Node) element;
                Coordinates drawPosition = new Coordinates(node.getPos().getLatitude() - (size/2 * Projection.getZoomFactor(detail)),
                        node.getPos().getLongitude() - (size/2 * Projection.getZoomFactor(detail)));
                context.drawImage(drawPosition, image, detail);
            }
            if (element instanceof Area) {
                Area area = (Area) element;
                if (area.getSelection() != null) {
                    Coordinates pos = area.getSelection().getPosition();
                    Coordinates drawPosition = new Coordinates(pos.getLatitude() - (size/2 * Projection.getZoomFactor(detail)),
                            pos.getLongitude() - (size/2 * Projection.getZoomFactor(detail)));
                    context.drawImage(drawPosition, image, detail);
                } else {
                    logger.warn("Area returned null-selection");
                }
            }
        }
    }
    
    private void drawNavPoints(Context context, int detail) {
        List<Selection> points = state.getNavigationNodes();
        int size = 7;

        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setComposite(AlphaComposite.Src);
        graphics.setColor(new Color(0, true));
        graphics.fillRect(0, 0, size, size);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        graphics.setColor(Color.RED);
        graphics.fillOval(0, 0, size, size);

        for (Selection point : points) {
//            graphics.setColor(Color.ORANGE);
//            graphics.fillOval(0, 0, 5, 5);
//            Node from = state.getLoadedMapInfo().getNode(point.getFrom());
//            Node to = state.getLoadedMapInfo().getNode(point.getTo());
//            context.drawImage(from.getPos(), image, detail);
//            context.drawImage(to.getPos(), image, detail);
//            
//            Coordinates selectedNodeOnEdge = getSelectedNodeOnEdge(from, to, point.getRatio());
//            graphics.setColor(Color.CYAN);
//            graphics.fillOval(0, 0, 5, 5);
//            context.drawImage(selectedNodeOnEdge, image, detail);

            Coordinates drawPosition = new Coordinates(point.getPosition().getLatitude() - (size/2 * Projection.getZoomFactor(detail)),
                    point.getPosition().getLongitude() - (size/2 * Projection.getZoomFactor(detail)));
            context.drawImage(drawPosition, image, detail);
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
