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
    private HashMap<Integer, Tile> map;
    
    /**
     * a FIFO list for the cache replacement
     */
    private LinkedList<Tile> leastRecentlyUsed; // TODO EXTEND: more elaborate aging algorithm

    public HashRenderCache() {
        map = new HashMap<Integer, Tile>();
        leastRecentlyUsed = new LinkedList<Tile>();
    }
    
    @Override
    public Tile queryCache(int tileSpecifier) {
        return map.get(tileSpecifier);
    }
    
    @Override
    public void addToCache(Tile tile) {
        map.put(tile.hashCode(), tile);
        if (leastRecentlyUsed.size() >= CACHE_SIZE) {
            map.remove(leastRecentlyUsed.removeFirst().hashCode());   
        }
        leastRecentlyUsed.addLast(tile);
    }
    
}
