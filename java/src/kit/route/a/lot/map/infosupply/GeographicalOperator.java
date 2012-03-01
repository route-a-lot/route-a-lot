package kit.route.a.lot.map.infosupply;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Set;

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
     * Writes the topLeft and bottomRight values of the current map to the given variables.
     * 
     */
    public void getBounds(Coordinates upLeft, Coordinates bottomRight);


    /**
     * Adds a {@link MapElement} to the base layer.
     * @param element the MapElement to be added
     */
    public void addToBaseLayer(MapElement element);

    /**
     * Adds a {@link MapElement} to the overlay.
     * @param element the MapElement to be added
     */
    public void addToOverlay(MapElement element);
    
    /**
     * Retrieves all MapElements belonging to the base layer of the given
     * zoom level within the defined boundary.
     * 
     * @param zoomlevel the zoom level
     * @param upLeft the northwestern corner of the boundary
     * @param bottomRight the southeastern corner of the boundary
     * @return a list containing all base layer MapElements in the queried section
     */
    public Set<MapElement> getBaseLayer(int zoomlevel, Coordinates upLeft, Coordinates bottomRight, boolean exact);

    /**
     * Retrieves all MapElements belonging to the overlay of the given
     * zoom level within the given boundary.
     * 
     * @param zoomlevel the zoom level
     * @param upLeft the northwestern corner of the boundary
     * @param bottomRight the southeastern corner of the boundary
     * @return a list containing all overlay MapElements in the queried section
     */
    public Set<MapElement> getOverlay(int zoomlevel, Coordinates upLeft, Coordinates bottomRight, boolean exact);
    
    public Set<MapElement> getBaseLayer(Coordinates pos, float radius, boolean exact);
    
  
    /**
     * Selects the map element nearest to the given position, incrementally increasing
     * the search radius if needed.
     * 
     * @param pos the given position
     * @return a {@link Selection} derived from the nearest map element
     */
    public Selection select(Coordinates pos);
   
    /**
     * Retrieves the description object of the {@link POINode} that is next to the given position.
     * @param pos the given position
     * @return the POI description
     */
    public POIDescription getPOIDescription(Coordinates pos, float radius, int detailLevel);
    
    
    /**
     * Reduces memory consumption by trimming all internal capacities to the actually used size.
     * Should be called when further change in the data structures is unlikely.
     */
    public void compactifyDatastructures();
    
    
    /**
     * Loads the geographic representation of a map from the stream.
     * 
     * @param stream the source stream.
     * @throws IOException a stream read error occurred
     */
    public void loadFromInput(DataInput input) throws IOException;
    
    /**
     * Saves the geographic representation of a map to the stream.
     * 
     * @param stream the destination stream.
     * @throws IOException a stream write error occurred
     */
    public void saveToOutput(DataOutput output) throws IOException;
       
}
