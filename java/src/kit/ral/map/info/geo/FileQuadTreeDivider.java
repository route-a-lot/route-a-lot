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

    private static final int MAX_SIZE = 16384;
    
    private CountingQuadTree root = null;
    private boolean refillNeeded = false;
    
    public FileQuadTreeDivider(Bounds bounds) {
        root = new CountingQuadTree(bounds);
    }
    
    public void add(MapElement element) {
        if (!root.add(element)) {
            refillNeeded = true;
        }
    }
    
    public boolean startRefill() {
        boolean result = refillNeeded;
        refillNeeded = false;
        root.purge();
        return result;
    } 
    
    public FileQuadTree buildTrunk(HashSet<FileQuadTree> divisions) {
        return root.buildDividedQuadTree(divisions);
    }
    
    private class CountingQuadTree {
        
                private int size = 0;     
        
        private Bounds bounds;
        private CountingQuadTree[] children = null;

        public CountingQuadTree(Bounds bounds) {
            this.bounds = bounds.clone();
        }
        
        public void purge() {
            size = 0;
            if (children != null) {
                for (CountingQuadTree child : children) {
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
                for (CountingQuadTree child : children) {
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
                if (size++ >= MAX_SIZE) {
                    if (children == null) {
                        createChildren();
                        result = false;
                    }
                    for (CountingQuadTree child : children) {
                        result &= child.add(element);
                    }
                }
            }
            return result;
        };
        
        public void createChildren() {
            children = new CountingQuadTree[4];
            float width = bounds.getWidth() / 2;
            float height = bounds.getHeight() / 2;
            for (int i = 0; i < 4; i++) {
                Coordinates topLeft = bounds.getTopLeft().add(height * (i % 2), width * (i / 2));  
                children[i] = new CountingQuadTree(
                        new Bounds(topLeft, topLeft.clone().add(height, width)));
            }
        };
        
    }

}
