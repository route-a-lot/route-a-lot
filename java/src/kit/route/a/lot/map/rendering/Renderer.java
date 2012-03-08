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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kit.route.a.lot.common.Bounds;
import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Context;
import kit.route.a.lot.common.Context2D;
import kit.route.a.lot.common.Listener;
import kit.route.a.lot.common.OSMType;
import kit.route.a.lot.common.Projection;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.controller.State;
import kit.route.a.lot.map.*;
import kit.route.a.lot.map.infosupply.MapInfo;

public class Renderer {
    protected static final int BASE_TILE_SIZE = 256;
    private static final boolean THREADED = true;
    private static final int DRAW_BUFFER = 200;
    private static final float ROUTE_SIZE = 20;
    private static final BufferedImage STARTNODE = createNavNodeImage(new Color(52, 151, 50), 14);
    private static final BufferedImage WAYNODE = createNavNodeImage(new Color(179, 186, 62), 12);
    private static final BufferedImage ENDNODE = createNavNodeImage(new Color(151, 50, 55), 14);
    
    private static final ExecutorService executorService = Executors.newCachedThreadPool();
    
    /**
     * A cache storing tiles that were previously drawn.
     */
    protected RenderCache cache;
    
    // Route caching:
    private BufferedImage routeImage = null;
    private Bounds routeBounds = new Bounds();
    private Integer[] drawnRoute = new Integer[0];
    private Selection[] drawnSelections = new Selection[0];
    private int drawnRouteDetail = -1;
    
    // Temporary variable (only guaranteed to be valid when rendering):
    public Context2D myContext;
    
    public boolean drawAreas = false;
    
    
    /**
     * Creates a new renderer.
     */
    public Renderer() {
        cache = new HashRenderCache();
    }

    /**
     * Renders a map viewing rectangle using the given rendering context.
     * @param context the rendering context
     * @param detail level of detail of the map view
     */
    public void render(Context context) {
        if (!(context instanceof Context2D)) {
            return;
        }
        Context2D ctx = (Context2D) context;
        myContext = ctx;
        Bounds bounds = ctx.getBounds();
        int detail = context.getDetailLevel();
        int tileSize = BASE_TILE_SIZE * Projection.getZoomFactor(detail);
        // FILL BACKGROUND
        Graphics graphics = ((Context2D) context).getGraphics();
        graphics.setColor(new Color(210, 230, 190));
        graphics.fillRect(0, 0, (int) bounds.getWidth(), (int) bounds.getHeight());      
        // DRAW TILES
        int maxLon = (int) (bounds.getRight() / tileSize);
        int maxLat = (int) (bounds.getBottom() / tileSize);
        int minLon = (int) (bounds.getLeft() / tileSize);
        int minLat = (int) (bounds.getTop() / tileSize);
        for (int i = minLon; i <= maxLon; i++) {
            for (int k = minLat; k <= maxLat; k++) {
                Coordinates topLeft = new Coordinates(k * tileSize, i * tileSize);
                Tile currentTile = prerenderTile(topLeft, tileSize, detail);
                if (currentTile.isFinished()) {
                    drawImage(ctx, topLeft, currentTile.getImage(), detail);
                }
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
     * @return the rendered tile
     */
    private Tile prerenderTile(Coordinates topLeft, int tileSize, int detail) {
        Tile tile = cache.queryCache(topLeft, tileSize, detail);
        if (tile == null) {
            final Tile newTile = new Tile(topLeft, tileSize, detail);
            cache.addToCache(newTile);
            if (THREADED) {
                executorService.submit(new Runnable() {
                    public void run() {
                        newTile.prerender();
                        newTile.drawPOIs();
                        newTile.markAsFinished();
                        Listener.fireEvent(Listener.TILE_RENDERED, null);
                    }   
                });
            } else {
                newTile.prerender();
                newTile.drawPOIs();
                newTile.markAsFinished();
            }            
            return newTile;
        }
        return tile;
    }

    /**
     * Chooses an so far uncached tile in proximity of the visible map viewing rectangle,
     * subsequently drawing and caching it.
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
        State state = State.getInstance();
        MapInfo mapInfo = state.getMapInfo();
        Integer[] route = state.getCurrentRoute().toArray(new Integer[state.getCurrentRoute().size()]);
        Selection[] sel = state.getNavigationNodes().toArray(new Selection[state.getNavigationNodes().size()]);
        List<Selection> navPoints = state.getNavigationNodes();
        Bounds bounds = context.getBounds();       
        
        boolean mustDrawRoute = (!Arrays.equals(route, drawnRoute))
                    || (!Arrays.equals(sel, drawnSelections))
                    || (drawnRouteDetail != detail)
                    || (routeBounds.getTop() >= bounds.getTop())
                    || (routeBounds.getLeft() >= bounds.getLeft())
                    || (routeBounds.getBottom() <= bounds.getBottom())
                    || (routeBounds.getRight() <= bounds.getRight());
        
        
        if (mustDrawRoute) {
            drawnSelections = sel;
            drawnRoute = route;
            drawnRouteDetail = detail;
            int size = 0;
            for (int i = 0; i < drawnRoute.length; i++) {   //count actual route size (without coded -1)
                if (drawnRoute[i] != -1) {
                    size++;
                }
            }
            
            Node[] routeNodes = new Node[size];
            if (route.length != 0) {    
                routeBounds = new Bounds(mapInfo.getNode(drawnRoute[0]).getPos(), 0);
                    // find route bounding rectangle dimensions and fill route nodes
                    int pos = 0;
                    for (int i = 0; i < drawnRoute.length; i++) {
                        if (drawnRoute[i] != -1) {
                            routeNodes[pos] = mapInfo.getNode(drawnRoute[i]);
                            adjustBorderCoordinates(routeBounds, routeNodes[pos].getPos(), detail);
                            pos++;
                        } 
                    }
                } else if (navPoints.size() != 0){
                    routeBounds = new Bounds(navPoints.get(0).getPosition(), 0);    //no route but navNodes
                } else {
                    return; //no route, or navNodes
                }
                for (Selection navSelection : navPoints) {
                    Node from = mapInfo.getNode(navSelection.getFrom());
                    Node to = mapInfo.getNode(navSelection.getTo());
                    Coordinates nodeOnEdge = Coordinates.interpolate(
                            from.getPos(), to.getPos(), navSelection.getRatio());
                    adjustBorderCoordinates(routeBounds, nodeOnEdge, detail);
                    adjustBorderCoordinates(routeBounds, navSelection.getPosition(), detail);
                }
                
                // adjust border coordinates if the route is bigger than the context window
                routeBounds.extend(bounds.getTopLeft(), DRAW_BUFFER);
                routeBounds.extend(bounds.getBottomRight(), DRAW_BUFFER);               

                int width = (int) routeBounds.getWidth() / Projection.getZoomFactor(detail);
                int height = (int) routeBounds.getHeight() / Projection.getZoomFactor(detail);
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
                
                routeNodes = Street.simplifyNodes(routeNodes, Projection.getZoomFactor(detail) * 3);
                float currentRouteSize = ROUTE_SIZE / Projection.getZoomFactor(detail / 2);
                float currentRouteSizeUnderlay = (ROUTE_SIZE + 2) / Projection.getZoomFactor(detail / 2);
                
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
        
        if (routeImage != null) {
            drawImage(context, routeBounds.getTopLeft(), routeImage, detail);
        }
    }

    private void drawRouteLines(Node[] routeNodes, List<Selection> navPoints, int detail,
            Graphics2D graphics, MapInfo mapInfo) {
        for (int i = 1; i < routeNodes.length; i++) {   
            drawLineBetweenCoordinates(routeNodes[i-1].getPos(), routeNodes[i].getPos(), detail, graphics);
        }
        int navNodesPos = 1;    //navNode we have to look at first, later
        for (int i = 0; i < navPoints.size() - 1; i++) {    //draw lines between selection on the same edge
            boolean first = true;   //important for counting navNodesPos
            if (navPoints.get(i).isOnSameEdge(navPoints.get(i+1))) {
                if (i == 0 || first) {
                    navNodesPos++;
                }
                Selection navSelection = navPoints.get(i);  //till loop we treat start and target
                Node from = mapInfo.getNode(navSelection.getFrom());
                Node to = mapInfo.getNode(navSelection.getTo());
                Coordinates nodeOnEdge1 = Coordinates.interpolate(from.getPos(),
                        to.getPos(), navSelection.getRatio());
                drawLineBetweenCoordinates(nodeOnEdge1, navSelection.getPosition(), detail, graphics);
                navSelection = navPoints.get(i + 1); 
                from = mapInfo.getNode(navSelection.getFrom());
                to = mapInfo.getNode(navSelection.getTo());
                Coordinates nodeOnEdge2 = Coordinates.interpolate(from.getPos(),
                        to.getPos(), navSelection.getRatio());
                drawLineBetweenCoordinates(nodeOnEdge2, navSelection.getPosition(), detail, graphics);
                drawLineBetweenCoordinates(nodeOnEdge1, nodeOnEdge2, detail, graphics);
            } else {
                first = false;
            }
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
            
            for (int i = 1; i < drawnRoute.length - 1; i++) {   //for navigationNodes which aren't start, or target
                if (drawnRoute[i] == -1) {//we have to draw lines to a selection
                    navSelection = navPoints.get(navNodesPos);
                    from = mapInfo.getNode(navSelection.getFrom());
                    to = mapInfo.getNode(navSelection.getTo());
                    nodeOnEdge = Coordinates.interpolate(from.getPos(),
                            to.getPos(), navSelection.getRatio());
                    drawLineBetweenCoordinates(mapInfo.getNodePosition(drawnRoute[i - 1]), nodeOnEdge, detail, graphics);
                    drawLineBetweenCoordinates(mapInfo.getNodePosition(drawnRoute[i + 1]), nodeOnEdge, detail, graphics);
                    drawLineBetweenCoordinates(nodeOnEdge, navSelection.getPosition(), detail, graphics);
                    while (navPoints.get(navNodesPos).isOnSameEdge(navPoints.get(navNodesPos + 1))) { //no route till the next navPoint
                        navNodesPos++;
                        navSelection = navPoints.get(navNodesPos);
                        from = mapInfo.getNode(navSelection.getFrom());
                        to = mapInfo.getNode(navSelection.getTo());
                        nodeOnEdge = Coordinates.interpolate(from.getPos(),
                                to.getPos(), navSelection.getRatio());
                        drawLineBetweenCoordinates(nodeOnEdge, navSelection.getPosition(), detail, graphics);
                    }
                    navNodesPos++;
                }
            }
        }
    }
    
    
    private void adjustBorderCoordinates(Bounds bounds, Coordinates point, int detail) {
        bounds.extend(point, ROUTE_SIZE * Projection.getZoomFactor(detail / 2));
    }

    private void drawLineBetweenCoordinates(Coordinates from, Coordinates to, int detail, Graphics2D graphics) {
        Coordinates start = getLocalCoordinates(from, routeBounds.getTop(), routeBounds.getLeft(), detail);
        Coordinates end = getLocalCoordinates(to, routeBounds.getTop(), routeBounds.getLeft(), detail);
        graphics.drawLine((int) start.getLongitude(), (int) start.getLatitude(),
                          (int) end.getLongitude(), (int) end.getLatitude());
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
    public static Coordinates getLocalCoordinates(Coordinates global, float top, float left, int detail) {
        return global.clone().add(-top, -left).scale(1f / Projection.getZoomFactor(detail));
    }
    
    /**
     * Draws the map overlay for the current context.
     */
    private void drawOverlay(Context2D context, int detail) {
        MapInfo mapInfo = State.getInstance().getMapInfo();
        Collection<MapElement> elements = mapInfo.queryElements(detail, context.getBounds(), false);
        // TODO test if true is faster

        int size = 8;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setComposite(AlphaComposite.Src);
        graphics.setColor(new Color(0, true));
        graphics.fillRect(0, 0, size, size);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setColor(Color.BLUE);
        graphics.fillOval(0, 0, size, size);
        
        float offset = -size/2 * Projection.getZoomFactor(detail);
        for (MapElement element : elements) {
            if (element instanceof POINode) {
                POINode fav = (POINode) element;
                if (((fav.getInfo().getName() != null) && (fav.getInfo().getName().length() > 0)
                        && (fav.getInfo().getCategory() == OSMType.FAVOURITE))){
                    drawImage(context, ((Node) element).getPos().add(offset, offset), image, detail);
                }                     
            }
        }
    }
    
    private void drawNavPoints(Context2D context, int detail) {
        List<Selection> points = State.getInstance().getNavigationNodes();
        int i = 0;
        for (Selection point : points) {
            BufferedImage image = (i == 0) ? STARTNODE : (i == points.size() - 1) ? ENDNODE : WAYNODE;
            float offset = - image.getHeight()/2 * Projection.getZoomFactor(detail);
            drawImage(context, point.getPosition().clone().add(offset, offset), image, detail);
            i++;
        }
    }
    
    private static BufferedImage createNavNodeImage(Color color, int size) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setComposite(AlphaComposite.Src);
        graphics.setColor(new Color(0, true));
        graphics.fillRect(0, 0, size, size);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        graphics.setColor(Color.BLACK);
        graphics.fillOval(0, 0, size, size);
        graphics.setColor(color);
        graphics.fillOval(1, 1, size-2, size-2);
        return image;
    }
    


    private void drawImage(Context2D context, Coordinates topLeft, Image image, int detail) {
        int x = (int) ((topLeft.getLongitude() - context.getBounds().getLeft())
                / Projection.getZoomFactor(detail));
        int y = (int) ((topLeft.getLatitude() - context.getBounds().getTop())
                / Projection.getZoomFactor(detail));
        ((Context2D) context).getGraphics().drawImage(image, x, y, null);
    }
    
    private void drawRect(Context2D context, Coordinates topLeft, int width, int height, int detail, Color c) {
        int size = 10 / Projection.getZoomFactor(detail);
        int x = (int) ((topLeft.getLongitude() - context.getBounds().getLeft())
                / Projection.getZoomFactor(detail)) - size/2;
        int y = (int) ((topLeft.getLatitude() - context.getBounds().getTop())
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
    
    public void addFrameToDraw(Bounds bounds, Color c) {
        Object[] frame = new Object[3];
        frame[0] = bounds.getTopLeft();
        frame[1] = bounds.getBottomRight();
        frame[2] = c;
        framesToDraw.add(frame);
        System.out.println("Added " + frame[0] + "  " + frame[1]);
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
    
    public void resetCache() {
        cache.resetCache();
    }

}
