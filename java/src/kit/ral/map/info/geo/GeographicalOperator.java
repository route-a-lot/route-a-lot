package kit.ral.map.info.geo;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Set;

import kit.ral.common.Bounds;
import kit.ral.common.Coordinates;
import kit.ral.common.Selection;
import kit.ral.common.description.POIDescription;
import kit.ral.map.MapElement;
import kit.ral.map.info.ElementDB;

public interface GeographicalOperator {

    public static final int NUM_LEVELS = 1;
    public static final float LAYER_MULTIPLIER = 6;
    
    
    // GETTERS & SETTERS
    
    /**
     * Defines the map boundary, in the process creating the (empty) zoom level layers.
     * 
     * @param upLeft the northwestern corner of the map boundary
     * @param bottomRight the southeastern corner of the map boundary
     */
    public void setBounds(Bounds bounds);
    
    /**
     * Writes the topLeft and bottomRight values of the current map to the given variables.
     * 
     */
    public Bounds getBounds();
   
    // BASIC OPERATIONS
   
    /**
     * Fills the operator with all elements stored in the given element database.
     */
    public void fill(ElementDB elementDB);
        
    /**
     * Retrieves all MapElements belonging to the given
     * zoom level within the defined boundary.
     * 
     * @param zoomlevel the zoom level
     * @param topLeft the northwestern corner of the boundary
     * @param bottomRight the southeastern corner of the boundary
     * @return a list containing all base layer MapElements in the queried section
     */
    public Set<MapElement> queryElements(Bounds area, int zoomlevel, boolean exact);

    
    // ADVANCED OPERATIONS
    
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
    
    
    // I/O OPERATIONS
       
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
       
    /**
     * Reduces memory consumption by trimming all internal capacities to the actually used size.
     * Should be called when further change in the data structures is unlikely.
     */
    public void compactify();
    
}
