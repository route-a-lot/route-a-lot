
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

import java.util.HashSet;

import kit.ral.common.Bounds;
import kit.ral.common.Coordinates;
import kit.ral.map.MapElement;

/**
 * Creates a QuadTree with maximum leaf size 16384. Since elements are not saved,
 * the filling of the QuadTree must be repeatedly fully done until
 * <code>isRefillNeeded()</code> returns <code>false</code>.<br><br>
 * 
 * The QuadTree's leaves can be converted to new separate QuadTrees using
 * <code>buildTrunk()</code>. Those QuadTrees then can be filled
 * (for real) separately.
 * 
 */
public class FileQuadTreeDivider {

    private static final int MAX_LEAF_SIZE = 256000;//16384;
    
    private DividerTree root = null;
    private boolean refillNeeded = false;
    
    /**
     * Creates a new empty divider.
     * @param bounds the area that is to be divided.
     */
    public FileQuadTreeDivider(Bounds bounds) {
        root = new DividerTree(bounds);
    }
    
    /**
     * Adds a map element to the divider. The map element is not really stored but instead
     * only inserted, added to a counter and discarded. This guarantees a low memory footprint,
     * but likely will require multiple refill runs.
     * @param element the MapElement that is to be added
     */
    public void add(MapElement element) {
        if (!root.add(element)) {
            refillNeeded = true;
        }
    }
    
    /**
     * Checks whether the full depth of the divider tree has been reached.
     * If not, the method will prepare another run at filling all MapElements
     * into the Divider.<br>
     * @return true if another refill is necessary to reach full depth
     */
    public boolean startRefill() {
        boolean result = refillNeeded;
        refillNeeded = false;
        root.purge();
        return result;
    } 
    
    /**
     * Builds a {@link FileQuadTree} from the current division.
     * While the tree root is assigned to the return value,
     * the quadtree leaves are added to the list that was given as an argument.
     * Those (empty) leaves can then be filled and saved separately (as separate quadtrees).<br>
     * By finally calling the root method <code>saveTree()</code>,
     * the complete tree is saved, integrating already saved subtrees (called "branches").
     * @param divisions list that will be filled with all quadtree branch roots
     * @return the quadtree trunk root
     */
    public FileQuadTree buildTrunk(HashSet<FileQuadTree> divisions) {
        return root.buildDividedQuadTree(divisions);
    }
    
    private class DividerTree {

        private int size = 0;
        private Bounds bounds;
        private DividerTree[] children = null;

        public DividerTree(Bounds bounds) {
            this.bounds = bounds.clone();
        }

        public void purge() {
            size = 0;
            if (children != null) {
                for (DividerTree child : children) {
                    child.purge();
                }
            }
        }

        public FileQuadTree buildDividedQuadTree(HashSet<FileQuadTree> leaves) {
            FileQuadTree result = new FileQuadTree(bounds);
            if (children == null) {
                leaves.add(result);
            } else {
                int i = 0;
                for (DividerTree child : children) {
                    result.setChild(i++, child.buildDividedQuadTree(leaves));
                }
            }
            return result;
        }

        /**
         * @param node
         * @return false if a split was needed
         */
        public boolean add(MapElement element) {
            boolean result = true;
            if (element.isInBounds(bounds)) {
                if (size++ >= MAX_LEAF_SIZE) {
                    if (children == null) {
                        createChildren();
                        result = false;
                    }
                    for (DividerTree child : children) {
                        result &= child.add(element);
                    }
                }
            }
            return result;
        };

        public void createChildren() {
            children = new DividerTree[4];
            float width = bounds.getWidth() / 2;
            float height = bounds.getHeight() / 2;
            for (int i = 0; i < 4; i++) {
                Coordinates topLeft = bounds.getTopLeft().add(height * (i % 2), width * (i / 2));
                children[i] = new DividerTree(new Bounds(topLeft, topLeft.clone().add(height, width)));
            }
        };

    }

}
