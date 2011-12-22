package kit.route.a.lot.map.rendering;

import java.util.List;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Context;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.map.POINode;
import kit.route.a.lot.map.rendering.Renderer;
import kit.route.a.lot.map.rendering.RenderCache;

public class Renderer {

    /**
     * A cache storing tiles that were previously drawn.
     */
    private RenderCache cache;

    /**
     * Creates a new renderer.
     */
    public Renderer() {
        cache = null;
    }
    
    /**
     * Renders a map viewing rectangle using the given rendering context.
     * 
     * @param detail level of detail of the map view
     * @param topLeft northwestern corner of the viewing rectangle
     * @param bottomRight southeastern corner of the viewing rectangle
     * @param renderingContext the output rendering context
     */
    public void render(int detail, Coordinates topLeft,
            Coordinates bottomRight, Context renderingContext) {
    }

    /**
     * Chooses an so far uncached tile in proximity of the visible map
     * viewing rectangle, subsequently drawing and caching it.
     * 
     * @return true if a tile was drawn
     */
    public boolean prerenderIdle() {
        return false;
    }

    /**
     * Adopts the cache from another renderer.
     * @param source
     */
    public void inheritCache(Renderer source) {
        this.cache = source.cache;
    }

    /**
     * If necessary, draws (and caches) the given tile.
     * 
     * @param tile the given tile frame
     */
    @SuppressWarnings("unused")
    private void prerenderTile(Tile tile) {
        if (!cache.queryCache(tile)) {
            tile.prerender();
            cache.addToCache(tile);
        }
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


}
