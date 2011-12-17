package kit.route.a.lot.map.rendering;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Context;
import kit.route.a.lot.map.rendering.Renderer;
import kit.route.a.lot.map.POINode;
import kit.route.a.lot.map.Area;
import kit.route.a.lot.map.Edge;
import kit.route.a.lot.map.rendering.RenderCache;

public class Renderer

{
    /** Associations */
    private RenderCache detailCaches;
    /**
     * Operation render
     * 
    render holt sich die aktuelle Route aus dem State und zeichnet sie mit in das Overlay
     *
     * @param detail - 
     * @param topLeft - 
     * @param bottomRight - 
     * @param renderingContext - 
     * @return 
     */
    public render ( int detail, Coordinates topLeft, Coordinates bottomRight, Context renderingContext ){}
    /**
     * Operation prerenderIdle
     *
     * @return boolean
     */
    public boolean prerenderIdle (  ){}
    /**
     * Operation inheritCache
     *
     * @param source - 
     * @return 
     */
    public inheritCache ( Renderer source ){}
    /**
     * Operation prerenderTile
     *
     * @param detail - 
     * @param topLeft - 
     * @return 
     */
    private prerenderTile ( int detail, Coordinates topLeft ){}
    /**
     * Operation drawRoute
     *
     * @param route - 
     * @param selection - 
     * @return 
     */
    private drawRoute ( List<int> route, List<Selection> selection ){}
    /**
     * Operation draw
     *
     * @param poi - 
     * @return 
     */
    private draw ( POINode poi ){}
    /**
     * Operation draw
     *
     * @param area - 
     * @return 
     */
    private draw ( Area area ){}
    /**
     * Operation draw
     *
     * @param edge - 
     * @return 
     */
    private draw ( Edge edge ){}
}

