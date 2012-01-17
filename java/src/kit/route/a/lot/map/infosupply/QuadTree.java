package kit.route.a.lot.map.infosupply;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.awt.geom.Rectangle2D;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.map.MapElement;

public abstract class QuadTree {

    //private static final byte DESCRIPTOR_QUADTREE_NULL = 0;
    private static final byte DESCRIPTOR_QUADTREE_NODE = 1;
    private static final byte DESCRIPTOR_QUADTREE_LEAF = 2;
    
    private Coordinates upLeft;
    private Coordinates bottomRight;
    
    public QuadTree(Coordinates upLeft, Coordinates bottomRight) {
        this.upLeft = upLeft;
        this.bottomRight = bottomRight;
    }

    protected abstract Collection<QTLeaf> getLeafs(Coordinates upLeft, Coordinates bottomRight);
    
    protected boolean isInBounds(Coordinates upLeft, Coordinates bottomRight) {
        Rectangle2D.Double thiss = new Rectangle2D.Double(this.upLeft.getLongitude(), this.bottomRight.getLatitude(),    
                this.bottomRight.getLongitude() - this.upLeft.getLongitude(),
                this.upLeft.getLatitude() - this.bottomRight.getLatitude());
        Rectangle2D.Double bounce = new Rectangle2D.Double(upLeft.getLongitude(), bottomRight.getLatitude(),    
                bottomRight.getLongitude() - upLeft.getLongitude(),
                upLeft.getLatitude() - bottomRight.getLatitude());
        return thiss.contains(bounce) || bounce.contains(thiss) || thiss.intersects(bounce);
    }
    
    /**
     * Returns the {@link Coordinates} of the northwestern corner of the QuadTree area.
     * @return the nortwestern quad tree corner
     */
    public Coordinates getUpLeft() {
        return upLeft;
    }
    
    /**
     * Returns the {@link Coordinates} of the southeastern corner of the QuadTree area.
     * @return the southeastern quad tree corner
     */
    public Coordinates getBottomRight() {
        return bottomRight;
    }   
    
    /**
     * Adds the map element to the quad tree overlay, sorting it into all leaves
     * that are intersected. Returns false if the quad tree needs to be splitted
     * (only happens if the QuadTree consists of only one leaf).
     * 
     * @param element the map element
     * @return false if the quad tree (which is a {@link QTLeaf}) needs to be splitted
     */
    protected abstract boolean addToOverlay(MapElement element);

    /**
     * Adds the map element to the quad tree base layer, sorting it into all leaves
     * that are intersected. Returns false if the quad tree needs to be splitted
     * (only happens if the QuadTree consists of only one leaf).
     * 
     * @param element the map element
     * @return false if the quad tree (which is a {@link QTLeaf}) needs to be splitted
     */
    protected abstract boolean addToBaseLayer(MapElement element);
    
    /**
     * Go ask someone else.
     * TODO what is this for?
     * @param offset some offset
     * @param last a neat list
     * @return the print output
     */
    public abstract String toString(int offset, List<Integer> last);
    
    /**
     * Returns the number of {@link MapElement}s in the quad tree.
     * @return the number of {@link MapElement}s in the quad tree
     */
    public abstract int countElements();
    
    
    /**
     * Loads a new quad tree from the given stream.
     * 
     * @param stream the source stream
     * @throws IOException quadtree could not be loaded from the stream
     */
    public static QuadTree loadFromStream(DataInputStream stream) throws IOException {
        stream.readLong(); //skip value => ignore
        
        Coordinates upLeft = new Coordinates(stream.readFloat(), stream.readFloat());
        Coordinates bottomRight = new Coordinates(stream.readFloat(), stream.readFloat());
        QuadTree tree;
        switch (stream.readByte()) {
            case DESCRIPTOR_QUADTREE_NODE: tree = new QTNode(upLeft, bottomRight); break;
            default: tree = new QTLeaf(upLeft, bottomRight);
        }        
        tree.load(stream);
        return tree;
    };

    /**
     * Saves the given quad tree to the given stream.
     * 
     * @param stream the destination stream
     * @throws IOException quadtree could not be saved to the stream
     */
    public static void saveToStream(DataOutputStream stream, QuadTree tree) throws IOException {
        stream.writeLong(0); //TODO: reserved for skip value => implement
        stream.writeFloat(tree.upLeft.getLongitude());
        stream.writeFloat(tree.upLeft.getLatitude());
        stream.writeFloat(tree.bottomRight.getLongitude());
        stream.writeFloat(tree.bottomRight.getLatitude());
        if (tree instanceof QTNode) {
            stream.writeByte(DESCRIPTOR_QUADTREE_NODE); 
        } else {
            stream.writeByte(DESCRIPTOR_QUADTREE_LEAF); 
        }
       
        tree.save(stream);
    }

    /**
     * Fills the quad tree with data from the given stream.<br>
     * <i>Should not be called directly. Use loadFromStream() instead.</i>
     * 
     * @param stream the source stream
     * @throws IOException quadtree could not be loaded from the stream
     */
    protected abstract void load(DataInputStream stream) throws IOException;
    
    /**
     * Saves the quad tree to the given stream.<br>
     * <i>Should not be called directly. Use saveToStream() instead.</i>
     * 
     * @param stream the destination stream
     * @throws IOException quadtree could not be saved to the stream
     */
    protected abstract void save(DataOutputStream stream) throws IOException;
    
}
