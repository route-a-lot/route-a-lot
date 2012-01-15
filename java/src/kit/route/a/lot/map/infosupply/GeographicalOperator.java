package kit.route.a.lot.map.infosupply;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
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
    public void setBounds(Coordinates upLeft, Coordinates bottomRight);

    /**
     * Operation buildZoomlevels
     * 
     * @return
     * 
     * @return
     */
    public void buildZoomlevels();

    /**
     * Operation select
     * 
     * @param pos
     *            -
     * @return Selection
     */
    public Selection select(Coordinates pos);

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
    public Collection<MapElement> getBaseLayer(int zoomlevel, Coordinates upLeft,
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
    public Collection<MapElement> getOverlay(int zoomlevel, Coordinates upLeft,
            Coordinates bottomRight);
    
    public Collection<MapElement> getOverlayToLastBaseLayer(Coordinates upLeft,
            Coordinates bottomRight);

    /**
     * Operation addToBaseLayer
     * 
     * @param element
     *            -
     * @return
     * @return
     */
    public void addToBaseLayer(MapElement element);

    /**
     * Operation addToOverlay
     * 
     * @param element
     *            -
     * @return
     * @return
     */
    public void addToOverlay(MapElement element);

    
    
    /**
     * 
     * @param pos
     * @return
     */
    public int deleteFavorite(Coordinates pos);
    
    /**
     * 
     * @param pos
     * @return
     */
    public POIDescription getPOIDescription(Coordinates pos);
    
    /**
     * Operation loadFromStream
     * 
     * @param stream
     *            -
     * @return
     * @return
     */
    public void loadFromStream(InputStream stream);

    /**
     * Operation saveToStream
     * 
     * @param stream
     *            -
     */
    public void saveToStream(OutputStream stream);
    
}
