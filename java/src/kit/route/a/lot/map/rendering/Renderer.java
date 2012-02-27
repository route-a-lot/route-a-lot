package kit.route.a.lot.map.rendering;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Context;
import kit.route.a.lot.common.Context2D;
import kit.route.a.lot.common.OSMType;
import kit.route.a.lot.common.Projection;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.controller.State;
import kit.route.a.lot.map.*;
import kit.route.a.lot.map.infosupply.MapInfo;

public class Renderer {
    protected static final int BASE_TILE_SIZE = 200;
    
    /**
     * A cache storing tiles that were previously drawn.
     */
    protected RenderCache cache;
    protected State state;
    
    private BufferedImage routeImage = null;
    private Coordinates routeTopLeft = new Coordinates();
    private Coordinates routeBottomRight = new Coordinates();
    private Integer[] drawnRoute = new Integer[0];
    private int drawnRouteDetail = -1;
    private static final int drawBuffer = 200;
    private static final float routeSize = 20;
    
    public Context2D myContext;
    
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
    public void render(Context context) {
        if (!(context instanceof Context2D)) {
            return;
        }
        Context2D ctx = (Context2D) context;
        myContext = ctx;
        state = State.getInstance();
        int detail = context.getDetailLevel();
        int tileSize = BASE_TILE_SIZE * Projection.getZoomFactor(detail);
        // FILL BACKGROUND
        Graphics graphics = ((Context2D) context).getGraphics();
        graphics.setColor(new Color(210, 230, 190));
        graphics.fillRect(0, 0, (int)(ctx.getBottomRight().getLongitude() - ctx.getTopLeft().getLongitude()),
                                (int)(ctx.getBottomRight().getLatitude() - ctx.getTopLeft().getLatitude()));      
        // DRAW TILES
        int maxLon = (int) (ctx.getBottomRight().getLongitude() / tileSize);
        int maxLat = (int) (ctx.getBottomRight().getLatitude() / tileSize);
        int minLon = (int) (ctx.getTopLeft().getLongitude() / tileSize);
        int minLat = (int) (ctx.getTopLeft().getLatitude() / tileSize);
        for (int i = minLon; i <= maxLon; i++) {
            for (int k = minLat; k <= maxLat; k++) {
                Coordinates topLeft = new Coordinates(k * tileSize, i * tileSize);
                Tile currentTile = prerenderTile(topLeft, tileSize, detail);
                drawImage(ctx, topLeft, currentTile.getImage(), detail);
            }
        }
        // DRAW OVERLAY
        drawRoute(ctx, detail);
        drawNavPoints(ctx, detail);
        drawOverlay(ctx, detail);
        for (Object[] frame : framesToDraw) {
            drawFrame((Coordinates) frame[0], (Coordinates) frame[1], detail, (Color) frame[2]);
        }
    }


    /**
     * If necessary, renders and caches the tile with the specified data
     * 
     * @return the rendered tile
     */
    private Tile prerenderTile(Coordinates topLeft, int tileSize, int detail) {
        Tile tile = cache.queryCache(topLeft, tileSize, detail);
        if (tile == null) {
            tile = new Tile(topLeft, tileSize, detail);
            tile.prerender();
            tile.drawPOIs();
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
     */
    private void drawRoute(Context2D context, int detail) {
        MapInfo mapInfo = state.getMapInfo();
        Integer[] route = state.getCurrentRoute().toArray(new Integer[state.getCurrentRoute().size()]);
        List<Selection> navPoints = state.getNavigationNodes();
               
        boolean mustDrawRoute = (!Arrays.equals(route, drawnRoute))
                    || (drawnRouteDetail != detail)
                    || (routeTopLeft.getLatitude() >= context.getTopLeft().getLatitude())
                    || (routeTopLeft.getLongitude() >= context.getTopLeft().getLongitude())
                    || (routeBottomRight.getLatitude() <= context.getBottomRight().getLatitude())
                    || (routeBottomRight.getLongitude() <= context.getBottomRight().getLongitude());
        
        if (mustDrawRoute) {
            drawnRoute = route;
            drawnRouteDetail = detail;
            int size = 0;
            for (int i = 0; i < drawnRoute.length; i++) {
                if (drawnRoute[i] != -1) {
                    size++;
                }
            }
            if (route.length == 0) { 
                routeImage = null;
            } else {
                Node[] routeNodes = new Node[size];
                routeTopLeft = new Coordinates(Float.MAX_VALUE, Float.MAX_VALUE);
                routeBottomRight = new Coordinates(Float.MIN_VALUE, Float.MIN_VALUE);
                
                // find route bounding rectangle dimensions
                int pos = 0;
                for (int i = 0; i < drawnRoute.length; i++) {
                    if (drawnRoute[i] != -1) {
                        routeNodes[pos] = mapInfo.getNode(drawnRoute[i]);
                        adjustBorderCoordinates(routeTopLeft, routeBottomRight, routeNodes[pos].getPos(), detail);
                        pos++;
                    }
                    
                }
                
                for (Selection navSelection : navPoints) {
                    Node from = mapInfo.getNode(navSelection.getFrom());
                    Node to = mapInfo.getNode(navSelection.getTo());
                    Coordinates nodeOnEdge = Coordinates.interpolate(
                            from.getPos(), to.getPos(), navSelection.getRatio());
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

                Coordinates dimensions = routeBottomRight.clone().subtract(routeTopLeft);
                int width = (int) dimensions.getLongitude() / Projection.getZoomFactor(detail);
                int height = (int) dimensions.getLatitude() / Projection.getZoomFactor(detail);
                if (width <= 0 || height <= 0) {
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
            }
        }
        
        if (routeImage != null) {
            drawImage(context, routeTopLeft, routeImage, detail);
        }
    }

    private void drawRouteLines(Node[] routeNodes, List<Selection> navPoints, int detail,
            Graphics2D graphics, MapInfo mapInfo) {
        for (int i = 1; i < routeNodes.length; i++) {
            drawLineBetweenCoordinates(routeNodes[i-1].getPos(), routeNodes[i].getPos(), detail, graphics);
        }
        if (drawnRoute.length > 0) {
            Selection navSelection = navPoints.get(0);  //till loop we treat start and target
            Node from = mapInfo.getNode(navSelection.getFrom());
            Node to = mapInfo.getNode(navSelection.getTo());
            Coordinates nodeOnEdge = Coordinates.interpolate(from.getPos(),
                    to.getPos(), navSelection.getRatio());
            drawLineBetweenCoordinates(routeNodes[0].getPos(), nodeOnEdge, detail, graphics);
            drawLineBetweenCoordinates(nodeOnEdge, navSelection.getPosition(), detail, graphics);
            navSelection = navPoints.get(navPoints.size() - 1);
            from = mapInfo.getNode(navSelection.getFrom());
            to = mapInfo.getNode(navSelection.getTo());
            nodeOnEdge = Coordinates.interpolate(from.getPos(),
                    to.getPos(), navSelection.getRatio());
            drawLineBetweenCoordinates(routeNodes[routeNodes.length - 1].getPos(), nodeOnEdge, detail, graphics);
            drawLineBetweenCoordinates(nodeOnEdge, navSelection.getPosition(), detail, graphics);
            int navNodesPos = 1;
            for (int i = 1; i < drawnRoute.length - 1; i++) {   //for navigationNodes which aren't start, or target
                if (drawnRoute[i] == -1) {
                    navSelection = navPoints.get(navNodesPos);
                    from = mapInfo.getNode(navSelection.getFrom());
                    to = mapInfo.getNode(navSelection.getTo());
                    nodeOnEdge = Coordinates.interpolate(from.getPos(),
                            to.getPos(), navSelection.getRatio());
                    drawLineBetweenCoordinates(mapInfo.getNodePosition(drawnRoute[i - 1]), nodeOnEdge, detail, graphics);
                    drawLineBetweenCoordinates(mapInfo.getNodePosition(drawnRoute[i - 1]), nodeOnEdge, detail, graphics);
                    drawLineBetweenCoordinates(nodeOnEdge, navSelection.getPosition(), detail, graphics);
                    navNodesPos++;
                }
            }
        }
        
        
//        for (int i = 0; i < navPoints.size(); i++) {
//            Selection navSelection = navPoints.get(i);
//            Node from = mapInfo.getNode(navSelection.getFrom());
//            Node to = mapInfo.getNode(navSelection.getTo());
//            Coordinates nodeOnEdge = Coordinates.interpolate(from.getPos(),
//                    to.getPos(), navSelection.getRatio());
//            boolean drawedFrom = false;
//            boolean drawedTo = false;
//            if (idIsInRoute(from.getID(), drawnRoute) && idIsInRoute(to.getID(), drawnRoute)) {
//                if (navSelection.getRatio() < 0.5) {
//                    drawLineBetweenCoordinates(from.getPos(), nodeOnEdge, detail, graphics);
//                } else {
//                    drawLineBetweenCoordinates(to.getPos(), nodeOnEdge, detail, graphics);
//                }
//            } else {
//                if (idIsInRoute(from.getID(), drawnRoute)) {
//                    drawLineBetweenCoordinates(from.getPos(), nodeOnEdge, detail, graphics);
//                    drawedFrom = true;
//                }
//                if (idIsInRoute(to.getID(), drawnRoute)) {
//                    drawLineBetweenCoordinates(to.getPos(), nodeOnEdge, detail, graphics);
//                    drawedTo = true;
//                }
//            }
//            if (drawedFrom && drawedTo && (i == 0 || i == navPoints.size() - 1)) {
//                System.out.println("ERROR: Drawed whole selection a the end of the route.");
//            }
//            drawLineBetweenCoordinates(nodeOnEdge, navSelection.getPosition(), detail, graphics);
//        }
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
        Coordinates start = getLocalCoordinates(from, routeTopLeft, detail);
        Coordinates end = getLocalCoordinates(to, routeTopLeft, detail);
        graphics.drawLine((int) start.getLongitude(), (int) start.getLatitude(), (int) end.getLongitude(), (int) end.getLatitude());
    }
        
    /**
     * Accepts global coordinates (reference system origin: map origin) and converts then into
     * local coordinates (reference system origin: topLeft).
     * Additionally scales the coordinates to fir the current zoom factor.
     * @param global set of global coordinates
     * @param topLeft origin of local coordinates
     * @param detail level of detail / zoomlevel
     * @return corresponding set of local coordinates
     */
    protected static Coordinates getLocalCoordinates(Coordinates global, Coordinates topLeft, int detail) {
        return global.clone().subtract(topLeft).scale(1f / Projection.getZoomFactor(detail));
    }
    
    /**
     * Draws the map overlay for the current context.
     */
    private void drawOverlay(Context2D context, int detail) {
        MapInfo mapInfo = State.getInstance().getMapInfo();
        Collection<MapElement> elements = mapInfo.getOverlay(detail, context.getTopLeft(), context.getBottomRight(), false); // TODO test if true is faster

        int size = 8;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setComposite(AlphaComposite.Src);
        graphics.setColor(new Color(0, true));
        graphics.fillRect(0, 0, size, size);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setColor(Color.BLUE);
        graphics.fillOval(0, 0, size, size);

        for (MapElement element : elements) {
            if (element instanceof Node) {
                if (element instanceof POINode && (((POINode) element).getInfo().getName() == null 
                        || ((POINode) element).getInfo().getName().equals("")
                        || ((POINode) element).getInfo().getCategory() != OSMType.FAVOURITE)){
                    continue;
                }
                float offset = -size/2 * Projection.getZoomFactor(detail);
                drawImage(context, ((Node) element).getPos().add(offset, offset), image, detail);
            }
            if (element instanceof Area) {
                continue;
            }
        }
    }
    
    private void drawNavPoints(Context2D context, int detail) {
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

            float offset = - size/2 * Projection.getZoomFactor(detail);
            drawImage(context, point.getPosition().clone().add(offset, offset), image, detail);
        }
    }

    private void drawImage(Context2D context, Coordinates topLeft, Image image, int detail) {
        int x = (int) ((topLeft.getLongitude() - context.getTopLeft().getLongitude())
                / Projection.getZoomFactor(detail));
        int y = (int) ((topLeft.getLatitude() - context.getTopLeft().getLatitude())
                / Projection.getZoomFactor(detail));
        ((Context2D) context).getGraphics().drawImage(image, x, y, null);
    }
    
    private void drawRect(Context2D context, Coordinates topLeft, int width, int height, int detail, Color c) {
        int size = 10 / Projection.getZoomFactor(detail);
        int x = (int) ((topLeft.getLongitude() - context.getTopLeft().getLongitude())
                / Projection.getZoomFactor(detail)) - size/2;
        int y = (int) ((topLeft.getLatitude() - context.getTopLeft().getLatitude())
                / Projection.getZoomFactor(detail)) - size/2;
        int newWidth = width/ Projection.getZoomFactor(detail) - size;
        int newHeight = height/ Projection.getZoomFactor(detail) - size;
        Graphics2D graphics = (Graphics2D) ((Context2D) context).getGraphics();
        graphics.setStroke(new BasicStroke(size));
        graphics.setColor(c);
        graphics.drawRect(x, y, newWidth, newHeight);
    }
    
    private Collection<Object[]> framesToDraw = new HashSet<Object[]>();
    
    public void resetFramesToDraw() {
        framesToDraw = new HashSet<Object[]>();
    }
    
    public void addFrameToDraw(Coordinates topLeft, Coordinates bottomRight, Color c) {
        Object[] frame = new Object[3];
        frame[0] = topLeft;
        frame[1] = bottomRight;
        frame[2] = c;
        framesToDraw.add(frame);
        System.out.println("Added " + topLeft + "  " + bottomRight);
        System.out.println("Size: " + framesToDraw.size());
    }
    
    private void drawFrame(Coordinates topLeft, Coordinates bottomRight,int detail, Color c) {
        int width = (int) (bottomRight.getLongitude() - topLeft.getLongitude());
        int height = (int) (bottomRight.getLatitude() - topLeft.getLatitude());
        if (width < 0 || height < 0) {
            return;
        }
        drawRect(myContext, topLeft, width, height, detail, c);
    }
    
    public void redraw() {
        render(myContext);
    }
    
    public void resetCache() {
        cache.resetCache();
    }

}
