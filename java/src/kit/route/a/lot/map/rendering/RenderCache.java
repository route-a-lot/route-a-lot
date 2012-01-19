package kit.route.a.lot.map.rendering;

public interface RenderCache {

    /**
     * Returns the tile for the given tile specifier if it is in the cache.
     * Otherwise returns null.
     * 
     * @param tileSpecifier
     *            an long as specifier for the tile (see Tile.getSpecifier(Coordinates, int))
     */
    Tile queryCache(long tileSpecifier);

    /**
     * Adds the given tile to the cache.
     * 
     */
    void addToCache(Tile tile);


    /**
     * resets the cahe
     */
    void resetCache();
}
