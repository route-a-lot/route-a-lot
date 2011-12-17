package kit.route.a.lot.map.rendering;

import kit.route.a.lot.map.rendering.RenderCache;

public class HashRenderCache
 implements RenderCache
{
    /** Attributes */
    /**
     * 
     */
    private Map<Coordinates,Image> map;
    /**
     * 
     */
    private List<Coordinates> leastRecentlyUsed;
}

