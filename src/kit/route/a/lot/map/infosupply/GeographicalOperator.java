package kit.route.a.lot.map.infosupply;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.map.MapElement;

public interface GeographicalOperator

{
    /**
     * Operation setBounds
     *
     * @param upLeft - 
     * @param bottomRight - 
     * @return 
     */
    protected setBounds ( Coordinates upLeft, Coordinates bottomRight );

    /**
     * Operation buildZoomlevels
     *
     * @return 
     */
    protected buildZoomlevels (  );

    /**
     * Operation select
     *
     * @param pos - 
     * @return Selection
     */
    protected Selection select ( Coordinates pos );

    /**
     * Operation getBaseLayer
     *
     * @param zoomlevel - 
     * @param upLeft - 
     * @param bottomRight - 
     * @return Set<MapElement>
     */
    protected Set<MapElement> getBaseLayer ( int zoomlevel, Coordinates upLeft, Coordinates bottomRight );

    /**
     * Operation getOverlay
     *
     * @param zoomlevel - 
     * @param upLeft - 
     * @param bottomRight - 
     * @return Set<MapElement>
     */
    protected Set<MapElement> getOverlay ( int zoomlevel, Coordinates upLeft, Coordinates bottomRight );

    /**
     * Operation addToBaseLayer
     *
     * @param element - 
     * @return 
     */
    protected addToBaseLayer ( MapElement element );

    /**
     * Operation addToOverlay
     *
     * @param element - 
     * @return 
     */
    protected addToOverlay ( MapElement element );

    /**
     * Operation loadFromStream
     *
     * @param stream - 
     * @return 
     */
    protected loadFromStream ( InputStream stream );

    /**
     * Operation saveToStream
     *
     * @param stream - 
     * @return 
     */
    protected saveToStream ( OutputStream stream );

}

