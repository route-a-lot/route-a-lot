package kit.route.a.lot.map.infosupply;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.POIDescription;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.map.MapElement;

public interface GeographicalOperator {

    /**
     * Operation setBounds
     * 
     * @param upLeft
     *            -
     * @param bottomRight
     *            -
     * @return
     * @return
     */
    void setBounds(Coordinates upLeft, Coordinates bottomRight);

    /**
     * Operation buildZoomlevels
     * 
     * @return
     * 
     * @return
     */
    void buildZoomlevels();

    /**
     * Operation select
     * 
     * @param pos
     *            -
     * @return Selection
     */
    Selection select(Coordinates pos);

    /**
     * Operation getBaseLayer
     * 
     * @param zoomlevel
     *            -
     * @param upLeft
     *            -
     * @param bottomRight
     *            -
     * @return Set<MapElement>
     */
    Collection<MapElement> getBaseLayer(int zoomlevel, Coordinates upLeft,
            Coordinates bottomRight);

    /**
     * Operation getOverlay
     * 
     * @param zoomlevel
     *            -
     * @param upLeft
     *            -
     * @param bottomRight
     *            -
     * @return Set<MapElement>
     */
    Collection<MapElement> getOverlay(int zoomlevel, Coordinates upLeft,
            Coordinates bottomRight);
    
    Collection<MapElement> getOverlayToLastOverlay();

    /**
     * Operation addToBaseLayer
     * 
     * @param element
     *            -
     * @return
     * @return
     */
    void addToBaseLayer(MapElement element);

    /**
     * Operation addToOverlay
     * 
     * @param element
     *            -
     * @return
     * @return
     */
    void addToOverlay(MapElement element);

    
    
    /**
     * 
     * @param pos
     * @return
     */
    int deleteFavorite(Coordinates pos);
    
    /**
     * 
     * @param pos
     * @return
     */
    POIDescription getPOIDescription(Coordinates pos);
    
    /**
     * Operation loadFromStream
     * 
     * @param stream
     *            -
     * @return
     * @return
     */
    void loadFromStream(InputStream stream);

    /**
     * Operation saveToStream
     * 
     * @param stream
     *            -
     * @return
     * @return
     */
    void saveToStream(OutputStream stream);

}
