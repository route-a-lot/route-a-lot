package kit.route.a.lot.map.rendering;

import java.util.Collection;

import kit.route.a.lot.common.Coordinates;

public interface RenderCache {

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
