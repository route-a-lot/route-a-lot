package kit.route.a.lot.map.infosupply;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Set;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.map.MapElement;


public class FileQuadTree {
    
    private static final int MAX_SIZE = 64;
    
    private RandomAccessFile source = null;
    private long sourcePointer = 0;
    
    private Coordinates topLeft, bottomRight;
    private FileQuadTree[] children = null;
    private ArrayList<MapElement> elements = null;    
    
    public FileQuadTree(Coordinates topLeft, Coordinates bottomRight,
            RandomAccessFile source, long sourcePointer) {
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
        this.source = source;
        this.sourcePointer = sourcePointer;
    }
    
    public FileQuadTree(Coordinates topLeft, Coordinates bottomRight) {
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
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
    
    public void addElement(MapElement element) {
        if (element.isInBounds(topLeft, bottomRight)) {
            loadIfNeeded();
            if (elements != null) {
                elements.add(element);
                if (elements.size() > MAX_SIZE) {
                    split();
                }
            } else {
                for (FileQuadTree child : children) {
                    child.addElement(element);
                }
            }
        }
    }

    public void queryElements(Coordinates topLeft, Coordinates bottomRight, Set<MapElement> target) {
        if (isInBounds(topLeft, bottomRight)) {
            loadIfNeeded();
            if (elements != null) {
                target.addAll(elements);
            } else {
                for (FileQuadTree child : children) {
                    child.queryElements(topLeft, bottomRight, target);
                }
            }
        }
    }
        
    public void split() {
        loadIfNeeded();
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

    private boolean isInBounds(Coordinates topLeft, Coordinates bottomRight) {
        Rectangle2D.Float bounds = new Rectangle2D.Float(
                this.topLeft.getLongitude(), this.topLeft.getLatitude(),
                this.bottomRight.getLongitude() - this.topLeft.getLongitude(),
                this.bottomRight.getLatitude() - this.topLeft.getLatitude());
        Rectangle2D.Float rect = new Rectangle2D.Float(
                topLeft.getLongitude(), topLeft.getLatitude(),
                bottomRight.getLongitude() - topLeft.getLongitude(),
                bottomRight.getLatitude() - topLeft.getLatitude());      
        return bounds.contains(rect) || rect.contains(bounds) || rect.intersects(bounds);
    }
    
    
    /**
     * Checks whether this Quadtree hasn't been loaded so far.
     * If not loaded yet, the Quadtree will be loaded now.
     */
    private void loadIfNeeded() {
        if ((children == null) && (elements == null)) {
            try {
                load();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
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
        elements = null;
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

}
