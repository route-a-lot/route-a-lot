package kit.route.a.lot.map.infosupply;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.map.MapElement;

public class QuadTree

{
    /** Attributes */
    /**
     * 
     */
    private Coordinates upLeft;
    /**
     * 
     */
    private Coordinates bottomRight;
    /**
     * Operation getLeafs
     *
     * @param upLeft - 
     * @param bottomRight - 
     * @return Collection<QTLeaf>
     */
    protected Collection<QTLeaf> getLeafs ( Coordinates upLeft, Coordinates bottomRight ){}
    /**
     * Operation select
     *
     * @param pos - 
     * @return Selection
     */
    protected Selection select ( Coordinates pos ){}
    /**
     * Operation loadFromStream
     *
     * @param stream - 
     * @return 
     */
    protected loadFromStream ( InputStream stream ){}
    /**
     * Operation saveToStream
     *
     * @param stream - 
     * @return 
     */
    protected saveToStream ( OutputStream stream ){}
    /**
     * Operation addToOverlay
     *
     * @param element - 
     * @return 
     */
    protected addToOverlay ( MapElement element ){}
    /**
     * Operation addToBaseLayer
     *
     * @param element - 
     * @return 
     */
    protected addToBaseLayer ( MapElement element ){}
}

