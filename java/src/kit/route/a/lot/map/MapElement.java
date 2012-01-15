package kit.route.a.lot.map;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import kit.route.a.lot.common.Selection;
import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.controller.State;
import kit.route.a.lot.map.infosupply.MapInfo;

public abstract class MapElement {

    /** constant used in a stream for announcing {@link Node} element data following */
    protected static final byte DESCRIPTOR_NODE = 1;
    /** constant used in a stream for announcing {@link Street} element data following */
    protected static final byte DESCRIPTOR_STREET = 2;
    /** constant used in a stream for announcing {@link Area} element data following */
    protected static final byte DESCRIPTOR_AREA = 3;
    /** constant used in a stream for announcing {@link POI} element data following */
    protected static final byte DESCRIPTOR_POI = 4;  
    
    /** the map element identifier */
    protected int id = -1;
    
    /**
     * Returns the name of the map element.
     * @return the name of the map element
     */
    abstract protected String getName();

    /**
     * Returns the MapElement ID.
     * @return the MapElement ID
     */
    public int getID() {
        return this.id;
    }
    
    /**
     * Sets the MapElement ID if no ID was assigned so far.
     * @param id the new MapElement ID
     * @return whether the ID was set
     */
    // TODO not really good name
    public boolean initID(int id) {
        if (this.id < 0) {
            this.id = id;
            return true;
        }
        return false;
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
    abstract public boolean isInBounds(Coordinates topLeft, Coordinates bottomRight);

    /**
     * Calculates an appropriate {@link Selection} from the MapElement
     * and the selected position.
     * @param pos the position
     * @return a Selection
     */
    abstract public Selection getSelection(Coordinates pos);
    
    /**
     * Calculates the distance between the <code>MapElement</code> and
     * the given {@link Coordinates}.
     * 
     * @param pos the given coordinates
     * @return the distance to the coordinates
     */
    abstract public float getDistanceTo(Coordinates pos);
       
    
    /**
     * Loads a {@link MapElement} from the stream. Before doing so determines
     * the map element type from the stream and creates the map element.
     * 
     * @param stream the source stream
     * @param whether the element is stored indirectly via ID
     * @return the loaded {@link MapElement}
     * @throws IllegalArgumentException <b>stream</b> is <code>null</code>
     * @throws UnsupportedOperationException element type could not be determined
     * @throws IOException map element could not be loaded from the stream
     */
    public static MapElement loadFromStream(DataInputStream stream, boolean asID) throws IOException {
        if (stream == null) {
            throw new IllegalArgumentException();
        }
        MapElement result;
        MapInfo mapInfo = State.getInstance().getLoadedMapInfo();
        switch (stream.readByte()) {
            case DESCRIPTOR_NODE: result = (asID) ? mapInfo.getNode(stream.readInt()) : new Node(); break;
            case DESCRIPTOR_STREET: result = (asID) ? mapInfo.getMapElement(stream.readInt()) : new Street(); break;
            case DESCRIPTOR_AREA: result = (asID) ? mapInfo.getMapElement(stream.readInt()) : new Area(); break;
            case DESCRIPTOR_POI: result = (asID) ? mapInfo.getNode(stream.readInt()) : new POINode(); break;
            default: throw new UnsupportedOperationException("Cannot determine element type from stream.");         
        }
        if (!asID) {
            result.load(stream);
        }
        return result;
    }

    /**
     * Saves a map element (or its ID if <code>asID</code> is set) to the stream.
     * Before doing so determines the map element type and saves it to the stream.
     * 
     * @param stream the destination stream
     * @param element the {@link MapElement} that is to be saved
     * @param asID determines whether the element itself or rather its ID is saved
     * @throws IllegalArgumentException either argument is <code>null</code>
     * @throws IOException <b>element</b> could not be saved to the stream
     */
    public static void saveToStream(DataOutputStream stream, MapElement element, boolean asID) throws IOException {     
        if ((stream == null) || (element == null)) {
            throw new IllegalArgumentException();
        }
        byte descriptor = 0;
        if (element instanceof Node) {
           descriptor = DESCRIPTOR_NODE; 
        } else if (element instanceof Street) {
           descriptor = DESCRIPTOR_STREET; 
        } else if (element instanceof Area) {
           descriptor = DESCRIPTOR_AREA; 
        } else if (element instanceof POINode) {
           descriptor = DESCRIPTOR_POI; 
        }
        stream.writeByte(descriptor);
        if (asID) {
            stream.writeInt(element.getID());
        } else {
            element.save(stream);
        }
    }

    /**
     * Loads a map element from the stream.
     * 
     * @param stream the source stream
     * @throws NullPointerException <b>stream</b> is <code>null</code>
     * @throws IOException element could not be loaded from the stream  
     */
    abstract protected void load(DataInputStream stream) throws IOException;

    /**
     * Saves a map element to the stream.
     * 
     * @param stream the destination stream
     * @throws NullPointerException <b>stream</b> is <code>null</code>
     * @throws IOException element could not be saved to the stream
     */
    abstract protected void save(DataOutputStream stream) throws IOException;
 
}
