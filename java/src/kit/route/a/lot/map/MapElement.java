package kit.route.a.lot.map;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import kit.route.a.lot.common.Bounds;
import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.controller.State;
import kit.route.a.lot.map.infosupply.MapInfo;

public abstract class MapElement{

    /** constant used in a stream for announcing {@link Node} element data following */
    public static final byte DESCRIPTOR_NODE = 1;
    /** constant used in a stream for announcing {@link Street} element data following */
    public static final byte DESCRIPTOR_STREET = 2;
    /** constant used in a stream for announcing {@link Area} element data following */
    public static final byte DESCRIPTOR_AREA = 3;
    /** constant used in a stream for announcing {@link POI} element data following */
    public static final byte DESCRIPTOR_POI = 4;  
    
    /** the map element identifier */
    protected int id = -1;
    
    private int usesCount = 0;
    
    /**
     * Returns the name of the map element.
     * @return the name of the map element
     */
    public abstract String getName();

    /**
     * Returns the MapElement ID.
     * @return the MapElement ID
     */
    public int getID() {
        return this.id;
    }
    
    public boolean equals(Object other) {
        if(other == this) {
            return true;
        }
        if(!(other instanceof MapElement)) {
            return false;
        }
        MapElement mapElement = (MapElement) other;
        return id == mapElement.id;
        
    }
    
    /**
     * Sets the MapElement ID if no ID was assigned so far.
     * @param id the new MapElement ID
     */
    public void setID(int id) {
        this.id = id;
    }
    
    /**
     * Checks whether the <code>MapElement</code> is (fully or partially) within
     * the given {@link Coordinates} range.
     * 
     * @param topLeft the northwestern corner of the boundary
     * @param bottomRight the southeastern corner of the boundary
     * @return true if the element is in the boundary
     * @throws IllegalArgumentException either argument is <code>null</code>
     */
    // used by QuadTree
    public abstract boolean isInBounds(Bounds bounds);
    
    abstract public MapElement getReduced(int detail, float range);

    /**
     * Calculates an appropriate {@link Selection} from the MapElement
     * and the selected position.
     * @return a Selection
     */
    public abstract Selection getSelection();

    
    /**
     * Loads a {@link MapElement} from the input. Before doing so determines
     * the map element type from the input and creates the map element.
     * 
     * @param input the input
     * @param asID whether the element is stored indirectly via ID
     * @return the loaded {@link MapElement}
     * @throws IllegalArgumentException <b>stream</b> is <code>null</code>
     * @throws UnsupportedOperationException element type could not be determined
     * @throws IOException map element could not be loaded from the input
     */
    public static MapElement loadFromInput(DataInput input, boolean asID) throws IOException {
        if (input == null) {
            throw new IllegalArgumentException();
        }
        MapElement result;
        MapInfo mapInfo = State.getInstance().getMapInfo();
        byte descriptor = input.readByte();
        switch (descriptor) {
            case DESCRIPTOR_POI: result = (asID) ? mapInfo.getNode(input.readInt()) : new POINode(); break;
            case DESCRIPTOR_NODE: result = (asID) ? mapInfo.getNode(input.readInt()) : new Node(); break;
            case DESCRIPTOR_STREET: result = (asID) ? mapInfo.getMapElement(input.readInt()) : new Street(); break;
            case DESCRIPTOR_AREA: result = (asID) ? mapInfo.getMapElement(input.readInt()) : new Area(); break;
            default: throw new UnsupportedOperationException("Cannot determine element type from stream.");         
        }
        if (!asID) {
            result.load(input);
        }
        return result;
    }

    /**
     * Saves a map element (or its ID if <code>asID</code> is set) to the output.
     * Before doing so determines the map element type and saves it to the output.
     * 
     * @param output the output
     * @param element the {@link MapElement} that is to be saved
     * @param asID determines whether the element itself or rather its ID is saved
     * @throws IllegalArgumentException either argument is <code>null</code>
     * @throws IOException <b>element</b> could not be saved to the output
     */
    public static void saveToOutput(DataOutput output, MapElement element, boolean asID) throws IOException {     
        if ((output == null) || (element == null)) {
            throw new IllegalArgumentException();
        }
        byte descriptor = 0;
        if (element instanceof POINode) {
            descriptor = DESCRIPTOR_POI;  
        } else if (element instanceof Node) {
           descriptor = DESCRIPTOR_NODE; 
        } else if (element instanceof Street) {
           descriptor = DESCRIPTOR_STREET; 
        } else if (element instanceof Area) {
           descriptor = DESCRIPTOR_AREA; 
        } else {
           throw new UnsupportedOperationException("Cannot save element: " + element.getName());
        }
        output.writeByte(descriptor);
        if (asID) {
            output.writeInt(element.getID());
        } else {
            element.save(output);
        }
    }

    /**
     * Loads a map element from the input.
     * 
     * @param input the source input
     * @throws NullPointerException <b>input</b> is <code>null</code>
     * @throws IOException element could not be loaded from the input  
     */
    protected abstract void load(DataInput input) throws IOException;

    /**
     * Saves a map element to the output.
     * 
     * @param output the destination output
     * @throws NullPointerException <b>output</b> is <code>null</code>
     * @throws IOException element could not be saved to the output
     */
    protected abstract void save(DataOutput output) throws IOException;
    
    //public abstract boolean equals(MapElement other);

    //public abstract int compare(MapElement one, MapElement other);
       
    public void registerUse() {
        usesCount++;
    }
   
    public void unregisterUse() {
        usesCount--;
    }
    
    public int getUsesCount() {
        return usesCount;
    }
    
 
}
