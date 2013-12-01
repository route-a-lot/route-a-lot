
/**
Copyright (c) 2012, Matthias Grundmann, Josua Stabenow
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
        * Redistributions of source code must retain the above copyright
          notice, this list of conditions and the following disclaimer.
        * Redistributions in binary form must reproduce the above copyright
          notice, this list of conditions and the following disclaimer in the
          documentation and/or other materials provided with the distribution.
        * The names of the contributors may not be used to endorse or promote products
          derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
**/

package kit.ral.map.info.geo;

import kit.ral.common.Bounds;
import kit.ral.common.Coordinates;
import kit.ral.common.RandomReadStream;
import kit.ral.common.RandomWriteStream;
import kit.ral.map.MapElement;
import kit.ral.map.MapElementComparator;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.*;


public class FileQuadTree extends QuadTree {

    private RandomReadStream source = null;
    private RandomWriteStream target = null;
    private long fileOffset = 0;

    private FileQuadTree[] children = null;
    private TreeSet<MapElement> elements = null;
    

    // CONSTRUCTORS

    /**
     * Creates a new read only quad tree, which will be loaded from the given file and position on first access.
     * 
     * @param topLeft
     * @param bottomRight
     * @param source
     * @param sourcePointer
     */
    public FileQuadTree(Bounds bounds, RandomReadStream source, long sourcePointer) {
        super(bounds);
        this.source = source;
        if (sourcePointer < 0) {
            throw new IllegalArgumentException();
        }
        fileOffset = sourcePointer;
    }

    /**
     * Creates a new empty quad tree, which may subsequently be filled with elements.
     * 
     * @param topLeft
     * @param bottomRight
     */
    public FileQuadTree(Bounds bounds) {
        super(bounds);
        elements = new TreeSet<MapElement>(new MapElementComparator());
    }


    // GETTERS & SETTERS

    /**
     * Sets/replaces the n-th child quad tree of this quad tree.
     * 
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
     * Returns the number of elements stored in this specific quad tree leaf. Returns -1 if the quad tree is a node or
     * not loaded.
     */
    @Override
    public int getSize() {
        return (elements == null) ? -1 : elements.size();
    }

    long getFileOffset() {
        return fileOffset;
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
        if (element.isInBounds(bounds)) {
            if (children != null) {
                for (FileQuadTree child : children) {
                    child.addElement(element);
                }
            } else {
                elements.add(element);
                element.registerUse();
                if (elements.size() > SIZE_LIMIT) {
                    split();
                }
            }
        }
        return true;
    }

    /**
     * Adds (at least) all elements within the given boundaries to the target list. If the flag <code>exact</code> is
     * set, only elements that really are inside the boundaries are added.
     */
    @Override
    public void queryElements(Bounds area, Set<MapElement> list, boolean exact) {
        if (isInBounds(area)) {
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
                    child.queryElements(area, list, exact);
                }
            } else if (elements != null) {
                if (exact) {
                    for (MapElement element : elements) {
                        if (element.isInBounds(area)) {
                            list.add(element);
                        }
                    }
                } else {
                    list.addAll(elements);
                }
            }
        }
    }

    
    // I/O OPERATIONS

    /**
     * Loads this quad tree node or leaf from the source. Direct sub nodes are created, but won't be loaded.
     * @throws IOException
     */
    public void loadNode() throws IOException {
        if (source == null) {
            throw new IllegalStateException("QuadTree needs to be in load mode.");
        }
        synchronized (source) {
            source.setPosition(getFileOffset());
            
            if (source.readLong() != 1234567890) {
                throw new IllegalArgumentException();
            }

            if (source.readBoolean()) {
                //System.out.println("load leaf " + fileOffset);
                // load all elements
                int size = source.readByte();  
                elements = new TreeSet<MapElement>(new MapElementComparator());
                for (int i = 0; i < size; i++) {
                    MapElement element = MapElement.loadFromInput(source);
                    element.registerUse();
                    elements.add(element);
                }
                FileQuadTreeCache.registerLeaf(this);
            } else {
                //System.out.println("load node " + fileOffset);
                // initialize children
                children = new FileQuadTree[4];
                float width = bounds.getWidth() / 2;
                float height = bounds.getHeight() / 2;
                for (int i = 0; i < 4; i++) {
                    Coordinates topLeft = bounds.getTopLeft().add(height * (i % 2), width * (i / 2));  
                    children[i] = new FileQuadTree(new Bounds(topLeft, topLeft.clone().add(height, width)),
                            source, source.readLong());
                }
            }        
        }

    }

    /**
     * Saves the quad tree to the given output. All nodes and leaves of the quad tree are saved as well. This method
     * will potentially override all data after the current position.
     * 
     * @param output
     * @throws IOException
     */
    public void saveTree(RandomWriteStream output) throws IOException {
        if (output == null) {
            throw new IllegalArgumentException();
        }
        // already saved:
        if ((target != null) && (output.getFileDescriptor().equals(target.getFileDescriptor()))) {
            return;
        }
        target = output;
        fileOffset = target.getPosition();    
          
        target.writeLong(1234567890);
        
        target.writeBoolean(elements != null);
        if (elements != null) {     
            // save all elements
            target.writeByte(elements.size());
            for (MapElement element : elements) {
                if (element == null) {
                    throw new IllegalStateException("Found null element in QT.");
                }
                MapElement.saveToOutput(target, element, true);
            }
        } else if (children != null) {
            // prepare children table
            long childTablePos = target.getPosition();
            for (int i = 0; i < children.length; i++) {
                target.writeLong(-1);
            }
            // save children
            for (FileQuadTree child : children) {
                child.saveTree(target);             
            }
            // write children table
            long lastPos = target.getPosition();
            target.setPosition(childTablePos);
            for (FileQuadTree child : children) {
                target.writeLong(child.getFileOffset());
            }      
            target.setPosition(lastPos);
        } else {
            throw new IllegalStateException("Cannot save an unloaded QT node.");
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
        if (elements != null) {
            for (MapElement element : elements) {
                element.unregisterUse();
            }
        }
        elements = null;
        if (children != null) {
            for (FileQuadTree child : children) {
                child.unload();
            }
        }
        children = null;
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
        float width = bounds.getWidth() / 2;
        float height = bounds.getHeight() / 2;
        for (int i = 0; i < 4; i++) {
            Coordinates topLeft = bounds.getTopLeft().add(height * (i % 2), width * (i / 2));  
            children[i] = new FileQuadTree(new Bounds(topLeft, topLeft.clone().add(height, width)));
            for (MapElement element : elements) {
                children[i].addElement(element);
            }
        }
        for (MapElement element : elements) {
            element.unregisterUse();
        }
        elements = null;
    }

    /*
     * @Override public String toString(int offset, List<Integer> last) { throw new UnsupportedOperationException(); }
     */

    @Override
    public String toString(int offset, List<Integer> last) {
        if ((children == null) && (elements == null)) {
            try {
                loadNode();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        if (children != null) {
            if (offset > 50) {
                return "this seems like a good point to stop printing...\n";
            }
            stringBuilder.append("'" + getSize() + "'\n");

            printOffset(offset, last, stringBuilder);
            stringBuilder.append("├──");
            stringBuilder.append(children[0].toString(offset + 1, last));

            printOffset(offset, last, stringBuilder);
            stringBuilder.append("├──");
            stringBuilder.append(children[1].toString(offset + 1, last));

            printOffset(offset, last, stringBuilder);
            stringBuilder.append("├──");
            stringBuilder.append(children[2].toString(offset + 1, last));

            printOffset(offset, last, stringBuilder);
            stringBuilder.append("└──");
            List<Integer> newLast = new ArrayList<Integer>(last);
            newLast.add(offset);
            stringBuilder.append(children[3].toString(offset + 1, newLast));
        } else {
            stringBuilder.append(" " + getSize() + "\n");
        }
        return stringBuilder.toString();
    }

    private void printOffset(int offset, List<Integer> last, StringBuilder stringBuilder) {
        for (int i = 0; i < offset; i++) {
            if (last.contains(i)) {
                stringBuilder.append("   ");
            } else {
                stringBuilder.append("│  ");
            }
        }
    }

}

final class FileQuadTreeCache {
    
    private static final int QUADTREE_LEAF_CACHE = 256;
    
    private static final LinkedList<FileQuadTree> loadedLeaves = new LinkedList<FileQuadTree>();
   
    private FileQuadTreeCache() {}
    
    static void registerLeaf(FileQuadTree tree) {
        loadedLeaves.offer(tree);
        if (loadedLeaves.size() > QUADTREE_LEAF_CACHE ) {
            FileQuadTree leaf = loadedLeaves.poll();
            //System.out.println("drop leaf " + leaf.getFileOffset());
            leaf.unload();          
        }
    };   
}
