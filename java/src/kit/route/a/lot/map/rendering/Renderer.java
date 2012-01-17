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
        float tileDim = (float) (Tile.BASE_TILE_DIM * Math.exp(detail * Math.log(2)));
        int maxLon = (int) Math.floor(context.getBottomRight().getLongitude() / tileDim);
        int maxLat = (int) Math.floor(context.getTopLeft().getLatitude() / tileDim) + 1;
        for (int i = (int) Math.floor(context.getTopLeft().getLongitude() / tileDim); i <= maxLon; i++) {
            for (int k = (int) Math.floor(context.getBottomRight().getLatitude() / tileDim) + 1; k <= maxLat; k++) {
                Coordinates topLeft = new Coordinates((k + 1) * tileDim, i * tileDim);
                Coordinates bottomRight = new Coordinates(k * tileDim, (i + 1) * tileDim);
                Tile currentTile = prerenderTile(topLeft, bottomRight, detail, context.getScale());
                context.drawImage(topLeft, currentTile.getData());
            }
        }
    }


    /**
     * If necessary, renders and caches the tile with the specified data
     * 
     * @return the rendered tile
     */
    private Tile prerenderTile(Coordinates topLeft, Coordinates bottomRight, int detail, float scale) {
        Tile tile = cache.queryCache(Tile.getSpecifier(topLeft, detail));
        if (tile == null) {
            tile = new Tile(topLeft, bottomRight, detail, scale);
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

    /**
     * Draws the given route on the current rendering context.
     * 
     * @param route
     *            node ids of the route nodes
     * @param selection
     *            navigation point list
     */
    private void drawRoute(List<Integer> route, List<Selection> selection) {
    }

    /**
     * Draws a point of interest on the current rendering context.
     * 
     * @param poi
     *            the POI to be drawn
     */
    private void drawPOI(POINode poi) {
    }

    /**
     * Adopts the cache from another renderer.
     * 
     * @param source
     */
    public void inheritCache(Renderer source) {
        this.cache = source.cache;
    }

}
