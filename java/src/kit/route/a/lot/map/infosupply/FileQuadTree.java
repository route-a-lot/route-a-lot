package kit.route.a.lot.map.infosupply;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.map.MapElement;


public class FileQuadTree extends QuadTree {
       
    private RandomAccessFile source = null;
    private long sourcePointer = 0;
    
    private FileQuadTree[] children = null;
    private ArrayList<MapElement> elements = null;    
    
    
    // CONSTRUCTORS
    
    /**
     * Creates a new read only quad tree, which will be
     * loaded from the given file and position on first access.
     * @param topLeft
     * @param bottomRight
     * @param source
     * @param sourcePointer
     */
    public FileQuadTree(Coordinates topLeft, Coordinates bottomRight,
            RandomAccessFile source, long sourcePointer) {
        super(topLeft, bottomRight);
        this.source = source;
        this.sourcePointer = sourcePointer;
    }
    
    /**
     * Creates a new empty quad tree, which may subsequently
     * be filled with elements.
     * @param topLeft
     * @param bottomRight
     */
    public FileQuadTree(Coordinates topLeft, Coordinates bottomRight) {
        super(topLeft, bottomRight);
        this.elements = new ArrayList<MapElement>();
    }

    
    // GETTERS & SETTERS
    
    /**
     * Sets/replaces the n-th child quad tree of this quad tree.
     * @param index
     * @param child
     */
    public void setChild(int index, FileQuadTree child) {
        if (children == null) {
            split();
        }    
        children[index] = child;
    } 
    
    /**
     * Returns the number of elements stored in this specific quad tree leaf.
     * Returns -1 if the quad tree is a node or not loaded.
     */
    @Override
    public int getSize() {
        return (elements == null) ? -1 : elements.size();
    }
    
    
    // BASIC OPERATIONS
    
    /**
     * Adds a map element to the quad tree.
     */
    @Override
    public boolean addElement(MapElement element) {
        if (source != null) {
            throw new IllegalStateException();
        }
        if (element.isInBounds(topLeft, bottomRight)) {
            if (children != null) {
                for (FileQuadTree child : children) {
                    child.addElement(element);
                }
            } else {
                elements.add(element);
                element.registerUse();
                if (elements.size() > MAX_SIZE) {
                    split();
                }
            }
        }
        return true;
    }

    /**
     * Adds (at least) all elements within the given boundaries to the target list.
     * If the flag <code>exact</code> is set, only elements that really are inside
     * the boundaries are added.
     */
    @Override
    public void queryElements(Coordinates topLeft, Coordinates bottomRight,
                                Set<MapElement> target, boolean exact) {
        if (isInBounds(topLeft, bottomRight)) {
            if ((children == null) && (elements == null)) {
                try {
                    loadNode();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
            if (children != null) {
                for (FileQuadTree child : children) {
                    child.queryElements(topLeft, bottomRight, target, exact);
                } 
            } else if (elements != null) {
                if (exact) {
                    for (MapElement element : elements) {
                        if (element.isInBounds(topLeft, bottomRight)) {
                            target.add(element);
                        }
                    }
                } else {
                    target.addAll(elements);
                }
            }
        }
    }
       
    
    // I/O OPERATIONS
        
    /**
     * Loads this quad tree node or leaf from the source.
     * Direct sub nodes may be created, but won't be loaded.
     * @throws IOException
     */
    public void loadNode() throws IOException {
        if (source == null) {
            throw new IllegalStateException();
        }
        source.seek(sourcePointer);
        source.seek(source.readLong()); // allow indirect addressing
        if (source.readBoolean()) {
            int size = source.readByte();
            elements = new ArrayList<MapElement>(size);
            for (int i = 0; i < size; i++) {
                MapElement element = MapElement.loadFromInput(source, true);
                element.registerUse();
                elements.add(element);               
            }
        } else {
            children = new FileQuadTree[4];
            Coordinates dim = bottomRight.clone().subtract(topLeft).scale(0.5f);
            for (int i = 0; i < 4; i++) {
                Coordinates origin = topLeft.clone().add(
                        dim.getLatitude() * (i % 2), dim.getLongitude() * (i / 2));
                children[i] = new FileQuadTree(origin, origin.clone().add(dim), source, source.readLong());
            }
        }
        
    }
       
    /**
     * Saves the quad tree to the given output. All nodes and leaves of the
     * quad tree are saved as well. This method will potentially override
     * all data after the current position.
     * @param output
     * @throws IOException
     */
    public void saveTree(RandomAccessFile output) throws IOException {  
        if (output == null) {
            throw new IllegalArgumentException();
        }
        boolean alreadySaved = output.equals(source) && (sourcePointer >= 0);
        source = output;
        sourcePointer = source.getFilePointer();
        output.writeLong(sourcePointer + 8);
        if (alreadySaved) {
            return;
        }   
        output.writeBoolean(elements != null);
        if (elements != null) {
            output.writeByte(elements.size());
            for (MapElement element : elements) {
                MapElement.saveToOutput(output, element, true);
            }
        } else {
            // save children, write each child's position at position mark
            long mark = output.getFilePointer();
            output.skipBytes(4 * 8);
            for (int i = 0; i < 4; i++) {
                long pos = output.getFilePointer();
                output.seek(mark + i * 8);
                output.writeLong(pos);
                output.seek(pos);
                children[i].saveTree(output);
            }
        }
    }

    @Override
    protected void load(DataInput input) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void save(DataOutput output) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Removes this complete quad tree from the RAM. All contained elements will be released as well.
     */
    @Override
    public void unload() {
        for (MapElement element : elements) {
            element.unregisterUse();
        }
        elements = null;
        for (FileQuadTree child : children) {
            child.unload();
        }
        children = null;
    }
    
    /**
     * Reduces memory footprint by cutting all arrays to actual content size.
     */
    @Override
    public void compactify() {
        if (elements != null) {
            elements.trimToSize();
        }
        if (children != null) {
            for (FileQuadTree child : children) {
                child.compactify();
            }
        }
    }

    
    // MISCELLANEOUS
    
    /**
     * Turns a leaf into a node, distributing it's elements to the new sub leafs.
     */
    private void split() {
        if (children != null || source != null) {
            throw new IllegalStateException();
        }
        children = new FileQuadTree[4];
        Coordinates dim = bottomRight.clone().subtract(topLeft).scale(0.5f);
        for (int i = 0; i < 4; i++) {
            Coordinates origin = topLeft.clone().add(
                    dim.getLatitude() * (i % 2), dim.getLongitude() * (i / 2));
            children[i] = new FileQuadTree(origin, origin.clone().add(dim));
            for (MapElement element : elements) {
                children[i].addElement(element);
            }
        }
        for (MapElement element : elements) {
            element.unregisterUse();
        }
        elements = null;
    }
    
    @Override
    public String toString(int offset, List<Integer> last) {
        throw new UnsupportedOperationException();
    }

}
