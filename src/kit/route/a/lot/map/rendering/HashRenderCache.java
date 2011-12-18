package kit.route.a.lot.map.rendering;

import java.awt.Image;
import java.util.List;
import java.util.Map;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.map.rendering.RenderCache;

public class HashRenderCache implements RenderCache {

    /** Attributes */
    /**
     * 
     */
    private Map<Coordinates, Image> map;
    /**
     * 
     */
    private List<Coordinates> leastRecentlyUsed;

    @Override
    public Image queryCache(Coordinates topLeft) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addToCache(Coordinates topLeft, Image image) {
        // TODO Auto-generated method stub

    }
}
