package kit.ral.map.rendering;

import java.util.Collection;

import kit.ral.common.Coordinates;

public interface RenderCache {
    
    /**
     * Maximum number of entries in the cache
     */
    public static final int CACHE_SIZE = 196;

    /**
     * Returns the tile for the given tile specifier if it is in the cache.
     * Otherwise returns null.
     * 
     * @param tileSpecifier
     *            an long as specifier for the tile (see Tile.getSpecifier(Coordinates, int))
     */
    public Tile queryCache(Coordinates topLeft, int tileSize, int detail);

    /**
     * Adds the given tile to the cache. If a tile was deleted from the cache
     * in the process, it is returned.
     */
    public Tile addToCache(Tile tile);

    /**
     * Resets the cache. All stored tiles are returned for finalization.
     */
    public Collection<Tile> resetCache();
}
