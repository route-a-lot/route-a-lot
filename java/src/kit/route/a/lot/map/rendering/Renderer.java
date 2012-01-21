package kit.route.a.lot.map.rendering;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.List;

import org.apache.log4j.Logger;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Context;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.controller.State;
import kit.route.a.lot.map.Node;
import kit.route.a.lot.map.POINode;
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
        int maxLat = (int) Math.floor(context.getBottomRight().getLatitude() / tileDim) - 1;
        int minLon = (int) Math.floor(context.getTopLeft().getLongitude() / tileDim);
        int minLat = (int) Math.floor(context.getTopLeft().getLatitude() / tileDim) - 1;
        for (int i = minLon; i <= maxLon; i++) {
            for (int k = minLat; k <= maxLat; k++) {
                Coordinates topLeft = new Coordinates((k + 1) * tileDim, i * tileDim);
                Coordinates bottomRight = new Coordinates(k * tileDim, (i + 1) * tileDim);
                Tile currentTile = prerenderTile(topLeft, bottomRight, detail);
                context.drawImage(topLeft, currentTile.getData(), detail);
            }
        }
        
        drawNavPoints(context, detail);
        drawRoute(context, detail);
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
    
    private BufferedImage routeImage;
    private Coordinates routeTopLeft = new Coordinates();
    private Integer[] drawnRoute;

    /**
     * Draws the given route on the given rendering context.
     * 
     */
    private void drawRoute(Context context, int detail) {
        List<Integer> route = state.getCurrentRoute();
        if (route != null && !route.toArray().equals(drawnRoute)) {
            drawnRoute = route.toArray(new Integer[route.size()]);
            
            if (drawnRoute.length > 0) {
                MapInfo mapInfo = state.getLoadedMapInfo();
                Node[] routeNodes = new Node[drawnRoute.length];
                routeTopLeft = new Coordinates(Float.MAX_VALUE, Float.MAX_VALUE);
                Coordinates routeBottomRight = new Coordinates(Float.MIN_VALUE, Float.MIN_VALUE);
                
                // find route bounding rectangle dimensions
                for (int i = 0; i < routeNodes.length; i++) {
                    routeNodes[i] = mapInfo.getNode(drawnRoute[i]);
                    Coordinates curPos = routeNodes[i].getPos();
                    float curLat = curPos.getLatitude();
                    float curLon = curPos.getLongitude();
                    int buffer = 10;
                    if (curLat < routeTopLeft.getLatitude()) {
                        routeTopLeft.setLatitude(curLat - buffer);
                    }
                    if (curLat > routeBottomRight.getLatitude()) {
                        routeBottomRight.setLatitude(curLat + buffer);
                    }
                    if (curLon < routeTopLeft.getLongitude()) {
                        routeTopLeft.setLongitude(curLon - buffer);
                    }
                    if (curLon > routeBottomRight.getLongitude()) {
                        routeBottomRight.setLongitude(curLon + buffer);
                    }
                }

                // define bounding rectangle TODO: this can be very inefficient
                int width = (int) Math.abs(routeTopLeft.getLongitude() - routeBottomRight.getLongitude());
                int height = (int) Math.abs(routeTopLeft.getLatitude() - routeBottomRight.getLatitude());
                routeImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                
                // configure rendering context
                Graphics2D graphics = routeImage.createGraphics();
                graphics.setComposite(AlphaComposite.Src);
                graphics.setColor(new Color(0, true));
                graphics.fillRect(0, 0, width, height);
                
                // draw route nodes
                graphics.setColor(Color.BLUE);
                int size = 12;
                for (int i = 0; i < routeNodes.length; i++) {
                    int curX = (int) (routeNodes[i].getPos().getLongitude() - routeTopLeft.getLongitude());
                    int curY = (int) (routeNodes[i].getPos().getLatitude() - routeTopLeft.getLatitude());
                    graphics.fillOval(curX - size/2, curY - size/2, size, size);
                }
                
                // draw route shadow
                graphics.setStroke(new BasicStroke(6));
                graphics.setColor(Color.GREEN);
                for (int i = 1; i < routeNodes.length; i++) {
                    int startX = (int) (routeNodes[i-1].getPos().getLongitude() - routeTopLeft.getLongitude());
                    int startY = (int) (routeNodes[i-1].getPos().getLatitude() - routeTopLeft.getLatitude());
                    int endX = (int) (routeNodes[i].getPos().getLongitude() - routeTopLeft.getLongitude());
                    int endY = (int) (routeNodes[i].getPos().getLatitude() - routeTopLeft.getLatitude());
                    graphics.drawLine(startX, startY, endX, endY);
                }
                
                // draw route
                graphics.setStroke(new BasicStroke(4));
                graphics.setColor(Color.BLUE);
                for (int i = 1; i < routeNodes.length; i++) {
                    int startX = (int) (routeNodes[i-1].getPos().getLongitude() - routeTopLeft.getLongitude());
                    int startY = (int) (routeNodes[i-1].getPos().getLatitude() - routeTopLeft.getLatitude());
                    int endX = (int) (routeNodes[i].getPos().getLongitude() - routeTopLeft.getLongitude());
                    int endY = (int) (routeNodes[i].getPos().getLatitude() - routeTopLeft.getLatitude());
                    graphics.drawLine(startX, startY, endX, endY);
                }
                
                graphics.dispose();
            } else {
                routeImage = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);
            }
        } else {
            System.out.println("Getting route from cache");
        }
        
        context.drawImage(routeTopLeft, routeImage, detail);
    }

    /**
     * Draws a point of interest on the current rendering context.
     * 
     * @param poi
     *            the POI to be drawn
     */
    private void drawPOI(POINode poi) {
    }
    
    private void drawNavPoints(Context context, int detail) {
        List<Selection> points = state.getNavigationNodes();
        for (Selection point : points) {
            BufferedImage image = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = image.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            graphics.setColor(Color.ORANGE);
            graphics.fillOval(0, 0, 5, 5);
            Coordinates from = state.getLoadedMapInfo().getNode(point.getFrom()).getPos();
            Coordinates to = state.getLoadedMapInfo().getNode(point.getTo()).getPos();
            context.drawImage(from, image, detail);
            context.drawImage(to, image, detail);

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
