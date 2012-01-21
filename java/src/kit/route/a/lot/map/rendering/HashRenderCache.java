package kit.route.a.lot.map.rendering;

import java.util.HashMap;
import java.util.LinkedList;

import kit.route.a.lot.map.rendering.RenderCache;

public class HashRenderCache implements RenderCache {

    /**
     * Maximum number of entries in the cache
     */
    private static final int CACHE_SIZE = 128;
    
    /**
     * Map, mapping tile specifiers to tiles
     */
    private HashMap<Long, Tile> map;
    
    /**
     * a FIFO list for the cache replacement
     */
    private LinkedList<Long> leastRecentlyUsed; // TODO EXTEND: more elaborate aging algorithm

    public HashRenderCache() {
        resetCache();
    }

    @Override
    public Tile queryCache(long tileSpecifier) {
        if (leastRecentlyUsed.remove(tileSpecifier)) {
            leastRecentlyUsed.addLast(tileSpecifier);
        }
        return map.get(tileSpecifier);
    }
    
    @Override
    public void addToCache(Tile tile) {
        long specifier = tile.getSpecifier();
        map.put(specifier, tile);
        if (leastRecentlyUsed.size() > CACHE_SIZE) {
            map.remove(leastRecentlyUsed.removeFirst());   
        }
        leastRecentlyUsed.addLast(specifier);
    }

    @Override
    public void resetCache() {
        map = new HashMap<Long, Tile>();
        leastRecentlyUsed = new LinkedList<Long>();
    }
    
}
