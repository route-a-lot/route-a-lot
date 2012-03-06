package kit.route.a.lot.map.infosupply;

import java.awt.geom.Rectangle2D;
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
    
    public FileQuadTree(Coordinates topLeft, Coordinates bottomRight,
            RandomAccessFile source, long sourcePointer) {
        super(topLeft, bottomRight);
        this.source = source;
        this.sourcePointer = sourcePointer;
    }
    
    public FileQuadTree(Coordinates topLeft, Coordinates bottomRight) {
        super(topLeft, bottomRight);
        this.elements = new ArrayList<MapElement>();
    }
    
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
 
    public void setChild(int index, FileQuadTree child) {
        if (children == null) {
            split();
        }    
        children[index] = child;
    } 
    
    public int getSize() {
        return (elements == null) ? -1 : elements.size();
    }
    
    @Override
    public boolean addElement(MapElement element) {
        if (element.isInBounds(topLeft, bottomRight)) {
            if (children != null) {
                for (FileQuadTree child : children) {
                    child.addElement(element);
                }
            } else {
                if (elements == null) {
                    elements = new ArrayList<MapElement>();
                }
                elements.add(element);
                if (elements.size() > MAX_SIZE) {
                    split();
                }
            }
        }
        return true;
    }

    public void queryElements(Coordinates topLeft, Coordinates bottomRight, Set<MapElement> target, boolean exact) {
        if (isInBounds(topLeft, bottomRight)) {
            if ((children == null) && (elements == null)) {
                try {
                    load();
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
        
    public void split() {
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
        elements = null;
    }
   
    /**
     * Loads this Quadtree node or leaf from the source.
     * Direct sub nodes may be created, but won't be loaded.
     * @throws IOException
     */
    public void load() throws IOException {
        source.seek(sourcePointer);
        source.seek(source.readLong()); // allow indirect addressing
        if (source.readBoolean()) {
            int size = source.readByte();
            elements = new ArrayList<MapElement>(size);
            for (int i = 0; i < size; i++) {
                elements.add(MapElement.loadFromInput(source, true));
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
    
    public void unload() {
        for (MapElement element : elements) {
        //    elementDB.releaseElement(element.getID()); 
        }
        elements = null;
        for (FileQuadTree child : children) {
            child.unload();
        }
        children = null;
    }
    
    /**
     * Saves the Quadtree to the given output. All nodes and leaves of the
     * Quadtree are saved as well. This method will potentially override
     * all data after the current position.
     * @param output
     * @throws IOException
     */
    public void save(RandomAccessFile output) throws IOException {  
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
                children[i].save(output);
            }
        }
    }
    
    public void compactifyDataStructures() {
        if (elements != null) {
            elements.trimToSize();
        }
        if (children != null) {
            for (FileQuadTree child : children) {
                child.compactifyDataStructures();
            }
        }
    }

     

    @Override
    public int countElements() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void load(DataInput input) throws IOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void save(DataOutput output) throws IOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String toString(int offset, List<Integer> last) {
        // TODO Auto-generated method stub
        return null;
    }

}
