package kit.route.a.lot.map.rendering;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;

import kit.route.a.lot.map.rendering.RenderCache;

public class HashRenderCache implements RenderCache {

    /**
     * Maximale Anzahl an Kacheln, die im Cache gespeichert werden k�nnen.
     */
    private static final int CACHE_SIZE = 20;
    
    /**
     * Erm�glicht den Zugriff auf die Cacheeintr�ge �ber eine Hashmap.
     */
    private HashMap<Tile, BufferedImage> map;
    
    /**
     * Verwaltet eine FIFO-Liste der hinzugef�gten Eintr�ge, 
     * so dass die �ltesten Eintr�ge ggfs. entfernt werden k�nnen.
     */
    private LinkedList<Tile> leastRecentlyUsed; // TODO EXTEND: more elaborate aging algorithm

    public HashRenderCache() {
        map = new HashMap<Tile, BufferedImage>();
        leastRecentlyUsed = new LinkedList<Tile>();
    }
    
    // specified in interface RenderCache
    public boolean queryCache(Tile tileFrame) {
        BufferedImage result = map.get(tileFrame);
        tileFrame.setData(result);
        return (result != null);
    }
    
    // specified in interface RenderCache
    public void addToCache(Tile tile) {
        map.put(tile, tile.getData());
        if (leastRecentlyUsed.size() >= CACHE_SIZE) {
            map.remove(leastRecentlyUsed.removeFirst());      
        }
        leastRecentlyUsed.addLast(tile);
    }
    
}
