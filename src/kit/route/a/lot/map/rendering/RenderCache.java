package kit.route.a.lot.map.rendering;

import kit.route.a.lot.common.Coordinates;

public interface RenderCache

{
    /**
     * Operation queryCache
     *
     * @param topLeft - 
     * @return Image
     */
    protected Image queryCache ( Coordinates topLeft );

    /**
     * Operation addToCache
     *
     * @param topLeft - 
     * @param image - 
     * @return 
     */
    protected addToCache ( Coordinates topLeft, Image image );

}

