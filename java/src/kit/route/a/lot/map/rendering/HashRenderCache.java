package kit.route.a.lot.map.rendering;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.map.rendering.RenderCache;

public class HashRenderCache implements RenderCache {

    /**
     * Maximum number of entries in the cache
     */
    private static final int CACHE_SIZE = 128;
    
    /**
     * Map, mapping tile specifiers to tiles
     */
    private HashMap<Long, Tile> map = new HashMap<Long, Tile>();
    
    /**
     * a FIFO list for the cache replacement
     */
    // TODO EXTEND: more elaborate aging algorithm
    private LinkedList<Long> leastRecentlyUsed = new LinkedList<Long>();
   

    /**
     * Crates a new tile cache which internally uses a
     * hash map for storing / retrieving cache entries.
     */
    public HashRenderCache() {
        resetCache();
    }

    @Override
    public Tile queryCache(Coordinates topLeft, int tileSize, int detail) {
        long specifier = Tile.getSpecifier(topLeft, tileSize, detail);
        if (leastRecentlyUsed.remove(specifier)) {
            leastRecentlyUsed.addLast(specifier);
        }
        return map.get(specifier);
    }
    
    @Override
    public Tile addToCache(Tile tile) {
        long specifier = tile.getSpecifier();
        Tile removedTile = null;
        map.put(specifier, tile);
        if (leastRecentlyUsed.size() > CACHE_SIZE) {
            removedTile = map.remove(leastRecentlyUsed.removeFirst());   
        }
        leastRecentlyUsed.addLast(specifier);
        //if (removedTile != null)
        //    System.err.println("CACHE REPLACE: " + removedTile.getSpecifier() + " by " + specifier);
        return removedTile;        
    }

    @Override
    public Collection<Tile> resetCache() {
        Collection<Tile> cache = map.values();
        map = new HashMap<Long, Tile>();
        leastRecentlyUsed = new LinkedList<Long>();
        return cache;
    }
    
}
