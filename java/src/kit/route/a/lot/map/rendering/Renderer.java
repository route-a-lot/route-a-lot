package kit.route.a.lot.map.rendering;

import java.util.List;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Context;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.controller.State;
import kit.route.a.lot.map.POINode;
import kit.route.a.lot.map.rendering.Renderer;
import kit.route.a.lot.map.rendering.RenderCache;

public class Renderer {

    /**
     * A cache storing tiles that were previously drawn.
     */
    private RenderCache cache;
    
    protected State state = State.getInstance();

    /**
     * Creates a new renderer.
     */
    public Renderer() {
        cache = null;
    }
    
    /**
     * Renders a map viewing rectangle using the given rendering context.
     * 
     * @param topLeft northwestern corner of the viewing rectangle
     * @param bottomRight southeastern corner of the viewing rectangle
     * @param detail level of detail of the map view
     * @param renderingContext the output rendering context
     */
    public void render(Context context, int detail) {
        // TEMPORARY: create one big tile only, don't cache
        Tile out = new Tile(context.getTopLeft(), context.getBottomRight(), detail,
                context.getWidth(), context.getHeight());
        out.prerender(state);
        context.drawImage(context.getTopLeft(), out.getData());
        /*
         * prerenderTiles(rc.getTopLeft(), rc.getBottomRight(), detail);
         * ...
         */
    }

    

    /**
     * Determines all tiles being part of the given map rectangle and pre-renders them if necessary.
     * 
     * @param topLeft northwestern corner of the map rectangle
     * @param bottomRight southeastern corner of the map rectangle
     * @param detail level of detail of the map view
     */
    @SuppressWarnings("unused")
    private void prerenderTiles(Coordinates topLeft, Coordinates bottomRight, int detail) {
        double tileDim = Tile.BASE_TILE_DIM * Math.exp(detail * Math.log(2));
        int maxLon = (int) Math.floor(bottomRight.getLongitude() / tileDim);
        int maxLat = (int) Math.floor(bottomRight.getLatitude() / tileDim);
        for (int i = (int) Math.floor(topLeft.getLongitude() / tileDim); i <= maxLon; i++) {
            for (int k = (int) Math.floor(topLeft.getLatitude() / tileDim); k <= maxLat; k++) {
                prerenderTile(new Tile(new Coordinates(i * tileDim, k * tileDim),
                                       new Coordinates((i+1) * tileDim, (k+1) * tileDim),
                                       detail));
            }
        }
    }

    /**
     * If necessary, draws (and caches) the given tile.
     * 
     * @param tile the given tile frame
     */
    private void prerenderTile(Tile tile) {
        if (!cache.queryCache(tile)) {
            tile.prerender(state);
            cache.addToCache(tile);
        }
    }

    /**
     * Chooses an so far uncached tile in proximity of the visible map
     * viewing rectangle, subsequently drawing and caching it.
     * 
     * @return true if a tile was drawn
     */
    public boolean prerenderIdle() {
        return false; // TODO: implement
    }
    
    /**
     * Draws the given route on the current rendering context.
     * 
     * @param route node ids of the route nodes
     * @param selection navigation point list
     */
    @SuppressWarnings("unused")
    private void drawRoute(List<Integer> route, List<Selection> selection) {
    }
    
    /**
     * Draws a point of interest on the current rendering context.
     * 
     * @param poi the POI to be drawn
     */
    @SuppressWarnings("unused")
    private void drawPOI(POINode poi) {
    }

    /**
     * Adopts the cache from another renderer.
     * @param source
     */
    public void inheritCache(Renderer source) {
        this.cache = source.cache;
    }

}
