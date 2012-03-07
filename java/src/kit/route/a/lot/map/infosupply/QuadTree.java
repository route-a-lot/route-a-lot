package kit.route.a.lot.map.infosupply;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.awt.geom.Rectangle2D;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.map.MapElement;

public abstract class QuadTree {

    protected static final int MAX_SIZE = 64;
    
    private static final byte DESCRIPTOR_QUADTREE_NODE = 1;
    private static final byte DESCRIPTOR_QUADTREE_LEAF = 2;

    protected Coordinates topLeft,  bottomRight;

    // CONSTRUCTOR
    
    public QuadTree(Coordinates topLeft, Coordinates bottomRight) {
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
    }
    
    
    // GETTERS & SETTERS
    
    /**
     * Returns the {@link Coordinates} of the northwestern corner of the QuadTree area.
     * @return the nortwestern quad tree corner
     */
    public Coordinates getTopLeft() {
        return topLeft;
    }

    /**
     * Returns the {@link Coordinates} of the southeastern corner of the QuadTree area.
     * @return the southeastern quad tree corner
     */
    public Coordinates getBottomRight() {
        return bottomRight;
    }
    
    /**
     * Returns the number of {@link MapElement}s in the quad tree.
     * @return the number of {@link MapElement}s in the quad tree
     */
    public abstract int getSize(); 
    
    
    // BASIC OPERATIONS
    
    /**
     * Adds the map element to the quad tree base layer, sorting it into all leaves that are intersected.
     * Returns false if the quad tree needs to be splitted (only happens if the QuadTree consists of only one
     * leaf).
     * @param element the map element
     * @return false if the quad tree (which is a {@link QTLeaf}) needs to be splitted
     */
    public abstract boolean addElement(MapElement element);

    public abstract void queryElements(Coordinates upLeft, Coordinates bottomRight,
            Set<MapElement> elements, boolean exact);
 
    
    // I/O OPERATIONS
    
    /**
     * Loads a new quad tree from the given stream.
     * 
     * @param input
     *            the source stream
     * @throws IOException
     *             quadtree could not be loaded from the stream
     */
    public static QuadTree loadFromInput(DataInput input) throws IOException {
        input.readLong(); // skip value => ignore

        Coordinates upLeft = Coordinates.loadFromInput(input);
        Coordinates bottomRight = Coordinates.loadFromInput(input);
        QuadTree tree;
        byte descriptor = input.readByte();
        switch (descriptor) {
            case DESCRIPTOR_QUADTREE_NODE:
                tree = new QTNode(upLeft, bottomRight);
                break;
            default:
                tree = new QTLeaf(upLeft, bottomRight);
        }
        tree.load(input);
        return tree;
    };

    /**
     * Saves the given quad tree to the given stream.
     * 
     * @param output
     *            the destination stream
     * @throws IOException
     *             quadtree could not be saved to the stream
     */
    public static void saveToOutput(DataOutput output, QuadTree tree) throws IOException {
        output.writeLong(0); // TODO: reserved for skip value => implement
        tree.topLeft.saveToOutput(output);
        tree.bottomRight.saveToOutput(output);
        if (tree instanceof QTNode) {
            output.writeByte(DESCRIPTOR_QUADTREE_NODE);
        } else {
            output.writeByte(DESCRIPTOR_QUADTREE_LEAF);
        }

        tree.save(output);
    }

    /**
     * Fills the quad tree with data from the given stream.<br>
     * <i>Should not be called directly. Use loadFromStream() instead.</i>
     * 
     * @param stream
     *            the source stream
     * @param recursive 
     * @throws IOException
     *             quadtree could not be loaded from the stream
     */
    protected abstract void load(DataInput input) throws IOException;

    /**
     * Saves the quad tree to the given stream.<br>
     * <i>Should not be called directly. Use saveToStream() instead.</i>
     * 
     * @param stream
     *            the destination stream
     * @throws IOException
     *             quadtree could not be saved to the stream
     */
    protected abstract void save(DataOutput output) throws IOException;
    
    public abstract void unload();
    
    public abstract void compactify();
    
    
    // MISCELLANEOUS
    
    protected boolean isInBounds(Coordinates upLeft, Coordinates bottomRight) {
        double width = Math.abs(this.bottomRight.getLongitude() - this.topLeft.getLongitude());
        double height = Math.abs(this.topLeft.getLatitude() - this.bottomRight.getLatitude());
        Rectangle2D.Double thiss = new Rectangle2D.Double(Math.min(this.topLeft.getLongitude(), this.bottomRight.getLongitude()), 
                                                          Math.min(this.topLeft.getLatitude(), this.bottomRight.getLatitude()), 
                                                          width, height);
        width = Math.abs(bottomRight.getLongitude() - upLeft.getLongitude());
        height = Math.abs(upLeft.getLatitude() - bottomRight.getLatitude());
        Rectangle2D.Double bounce = new Rectangle2D.Double(Math.min(upLeft.getLongitude(), bottomRight.getLongitude()), 
                                                           Math.min(upLeft.getLatitude(), bottomRight.getLatitude()),  
                                                           width, height);
        return thiss.contains(bounce) || bounce.contains(thiss) || thiss.intersects(bounce);
    }
    
    public boolean equals(Object other) {
        if(other == this) {
            return true;
        }
        if(!(other instanceof QuadTree)) {
            return false;
        }
        QuadTree comparee = (QuadTree) other;
        return topLeft.equals(comparee.topLeft) && bottomRight.equals(comparee.bottomRight);
    }

    /**
     * Go ask someone else. TODO what is this for?
     * @param offset some offset
     * @param last a neat list
     * @return the print output
     */
    public abstract String toString(int offset, List<Integer> last);
}
