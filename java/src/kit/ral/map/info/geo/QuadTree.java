
/**
Copyright (c) 2012, Matthias Grundmann, Jan Jacob, Daniel Krauß, Josua Stabenow
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

    public abstract void queryElements(Bounds area, Set<MapElement> target, boolean exact);
 
    
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
        QuadTree tree = (input.readBoolean()) ?
                new QTLeaf(newBounds) : new QTNode(newBounds);
        tree.load(input);
        return tree;
    }

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
        output.writeBoolean(tree instanceof QTLeaf);
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
