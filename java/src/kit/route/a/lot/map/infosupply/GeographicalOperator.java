package kit.route.a.lot.map.infosupply;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;
import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.POIDescription;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.map.MapElement;

public interface GeographicalOperator {

    /**
     * Defines the map boundary, in the process creating the (empty) zoom level layers.
     * 
     * @param upLeft the northwestern corner of the map boundary
     * @param bottomRight the southeastern corner of the map boundary
     */
    public void setBounds(Coordinates upLeft, Coordinates bottomRight);

    /**
     * Creates all zoom level layers, requiring a complete base
     * zoom level base layer and overlay.
     */
    public void buildZoomlevels();

    /**
     * Selects the map element nearest to the given position, incrementally increasing
     * the search radius if needed.
     * 
     * @param pos the given position
     * @return a {@link Selection} derived from the nearest map element
     */
    public Selection select(Coordinates pos);

    /**
     * Retrieves all MapElements belonging to the base layer of the given
     * zoom level within the defined boundary.
     * 
     * @param zoomlevel the zoom level
     * @param upLeft the northwestern corner of the boundary
     * @param bottomRight the southeastern corner of the boundary
     * @return a list containing all base layer MapElements in the queried section
     */
    public Collection<MapElement> getBaseLayer(int zoomlevel, Coordinates upLeft,
            Coordinates bottomRight);

    /**
     * Retrieves all MapElements belonging to the overlay of the given
     * zoom level within the given boundary.
     * 
     * @param zoomlevel the zoom level
     * @param upLeft the northwestern corner of the boundary
     * @param bottomRight the southeastern corner of the boundary
     * @return a list containing all overlay MapElements in the queried section
     */
    public Collection<MapElement> getOverlay(int zoomlevel, Coordinates upLeft,
            Coordinates bottomRight);
    
    /**
     * Returns the overlay corresponding to the last base layer query.
     * 
     * @param upLeft the northwestern corner of the queried boundary
     * @param bottomRight the southeastern corner of the queried boundary
     * @return a list containing all overlay MapElements in the queried section
     */
    public Collection<MapElement> getOverlayToLastBaseLayer(Coordinates upLeft,
            Coordinates bottomRight);

    /**
     * Adds a {@link MapElement} to the base layer.
     * 
     * @param element the MapElement to be added
     */
    public void addToBaseLayer(MapElement element);

    /**
     * Adds a {@link MapElement} to the overlay.
     * 
     * @param element the MapElement to be added
     */
    public void addToOverlay(MapElement element);

    
    /**
     * Deletes the favorite nearest to the given Coordinates and returns it's id.
     * @param pos the given position
     * @return the deleted favorite's id
     */
    public int deleteFavorite(Coordinates pos);
    

    /**
     * Retrieves the description object of the {@link POINode} that is next to the given position.
     * @param pos the given position
     * @return the POI description
     */
    public POIDescription getPOIDescription(Coordinates pos, float radius);
    
    
    /**
     * Loads the geographic representation of a map from the stream.
     * 
     * @param stream the source stream.
     * @throws IOException a stream read error occurred
     */
    public void loadFromStream(DataInputStream stream) throws IOException;

    
    /**
     * Saves the geographic representation of a map to the stream.
     * 
     * @param stream the destination stream.
     * @throws IOException a stream write error occurred
     */
    public void saveToStream(DataOutputStream stream) throws IOException;
    
}
