package kit.route.a.lot.map.rendering;

import java.awt.Image;
import java.util.List;
import java.util.Map;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.map.rendering.RenderCache;

public class HashRenderCache implements RenderCache {

    /**
     * Maximale Anzahl an Kacheln, die im Cache gespeichert werden können.
     */
    private static final int CACHE_SIZE = 20;
    
    /**
     * Ermöglicht den Zugriff auf die Cacheeinträge über eine Hashmap.
     */
    // TODO: Map.get() und put() überschreiben (kein Speichern nach Objektreferenz)
    private Map<Coordinates, Image> map;
    
    /**
     * Verwaltet eine FIFO-Liste der hinzugefügten Einträge, 
     * so dass die ältesten Einträge ggfs. entfernt werden können.
     */
    private List<Coordinates> leastRecentlyUsed;

    public HashRenderCache() {
        map = null;
        leastRecentlyUsed = null;
    }
    
    // specified in interface RenderCache
    public Image queryCache(Coordinates topLeft) {
        return map.get(topLeft);
    }
    
    // specified in interface RenderCache
    public void addToCache(Coordinates topLeft, Image image) {
        map.put(topLeft, image);
        leastRecentlyUsed.add(topLeft);
        if (leastRecentlyUsed.size() > CACHE_SIZE) {
            leastRecentlyUsed.remove(0);
        }
    }
}
