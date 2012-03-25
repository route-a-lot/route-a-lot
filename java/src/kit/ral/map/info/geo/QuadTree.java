package kit.ral.map.info.geo;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.awt.geom.Rectangle2D;

import kit.ral.common.Bounds;
import kit.ral.map.MapElement;

public abstract class QuadTree {

    protected static final int SIZE_LIMIT = 64;
    
    private static final byte DESCRIPTOR_QUADTREE_NODE = 1;
    private static final byte DESCRIPTOR_QUADTREE_LEAF = 2;

    protected Bounds bounds;

    // CONSTRUCTOR
    
    public QuadTree(Bounds bounds) {
        this.bounds = bounds.clone();
    }
    
    
    // GETTERS & SETTERS
    
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

    public abstract void queryElements(Bounds area, Set<MapElement> elements, boolean exact);
 
    
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
        Bounds newBounds = Bounds.loadFromInput(input);
        QuadTree tree;
        byte descriptor = input.readByte();
        switch (descriptor) {
            case DESCRIPTOR_QUADTREE_NODE:
                tree = new QTNode(newBounds);
                break;
            case DESCRIPTOR_QUADTREE_LEAF:
                tree = new QTLeaf(newBounds);
                break;
            default:
                throw new IOException();
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
        tree.bounds.saveToOutput(output);
        if (tree instanceof QTNode) {
            output.writeByte(DESCRIPTOR_QUADTREE_NODE);
        } else if (tree instanceof QTLeaf) {
            output.writeByte(DESCRIPTOR_QUADTREE_LEAF);
        } else {
            throw new IllegalStateException();
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
    
    protected boolean isInBounds(Bounds bounds) {
        Rectangle2D.Float node = new Rectangle2D.Float(
                this.bounds.getLeft(), this.bounds.getTop(), 
                this.bounds.getWidth(), this.bounds.getHeight());    
        Rectangle2D.Float boundary = new Rectangle2D.Float(
                bounds.getLeft(), bounds.getTop(), 
                bounds.getWidth(), bounds.getHeight());
        return node.contains(boundary) || boundary.contains(node) || node.intersects(boundary);
    }
    
    public boolean equals(Object other) {
        if(other == this) {
            return true;
        }
        if(!(other instanceof QuadTree)) {
            return false;
        }
        QuadTree comparee = (QuadTree) other;
        return bounds.equals(comparee.bounds);
    }

    public abstract String toString(int offset, List<Integer> last);
}
