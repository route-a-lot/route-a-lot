package kit.route.a.lot.map.rendering;

public interface RenderCache {

    /**
     * Fills tile data (in tileFrame) with the stored tile image on cache hit.
     * 
     * @param tileFrame an empty tile, offering coordinates and a level of detail
     * @return true on cache hit
     */
    boolean queryCache(Tile tileFrame);

    /**
     * Adds the given tile to the cache.
     * 
     * @param tile the given tile
     */
    void addToCache(Tile tile);

}
