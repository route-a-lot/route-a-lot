package kit.route.a.lot.map;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import kit.route.a.lot.common.Selection;
import kit.route.a.lot.common.Coordinates;

public abstract class MapElement {

    /** constant used in a stream for announcing {@link Node} element data following */
    private static final byte DESCRIPTOR_NODE = 1;
    /** constant used in a stream for announcing {@link Street} element data following */
    private static final byte DESCRIPTOR_STREET = 2;
    /** constant used in a stream for announcing {@link Area} element data following */
    private static final byte DESCRIPTOR_AREA = 3;
    /** constant used in a stream for announcing {@link POI} element data following */
    private static final byte DESCRIPTOR_POI = 4;
    //private static final byte DESCRIPTOR_EDGE = 5; should not be needed     
    
    /**
     * Returns the name of the map element.
     * @return the name of the map element
     */
    abstract protected String getName();

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
     * @return the loaded {@link MapElement}
     * @throws IllegalArgumentException <b>stream</b> is <code>null</code>
     * @throws UnsupportedOperationException element type could not be determined
     * @throws IOException map element could not be loaded from the stream
     */
    protected static MapElement loadFromStream(DataInputStream stream) throws IOException {
        if (stream == null) {
            throw new IllegalArgumentException();
        }
        MapElement result;
        switch (stream.readByte()) {
            case DESCRIPTOR_NODE: result = new Node(); break;
            case DESCRIPTOR_STREET: result = new Street(); break;
            case DESCRIPTOR_AREA: result = new Area(); break;
            case DESCRIPTOR_POI: result = new POINode(); break;
            default: throw new UnsupportedOperationException("Cannot determine element type from stream.");         
        }
        result.load(stream);
        return result;
    }

    /**
     * Saves a map element to the stream. Before doing so determines
     * the map element type and saves it to the stream.<br>
     * 
     * @param stream the destination stream
     * @param element the {@link MapElement} that is to be saved
     * @throws IllegalArgumentException either argument is <code>null</code>
     * @throws UnsupportedOperationException <b>element</b> is of an unsupported type
     * @throws IOException <b>element</b> could not be saved to the stream
     */
    protected static void saveToStream(DataOutputStream stream, MapElement element) throws IOException {     
        if ((stream == null) || (element == null)) {
            throw new IllegalArgumentException();
        }
        if (element instanceof Node) {
            stream.writeByte(DESCRIPTOR_NODE);
        } else if (element instanceof Street) {
            stream.writeByte(DESCRIPTOR_STREET);
        } else if (element instanceof Area) {
            stream.writeByte(DESCRIPTOR_AREA);
        } else if (element instanceof POINode) {
            stream.writeByte(DESCRIPTOR_POI);
        } else {
            throw new UnsupportedOperationException("Cannot save elements of type "
                    + element.getClass().getName() + " to stream.");
        }
        element.save(stream);
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
