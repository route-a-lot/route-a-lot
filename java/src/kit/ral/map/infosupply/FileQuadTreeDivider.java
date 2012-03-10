package kit.ral.map.infosupply;

import java.util.HashSet;

import kit.ral.common.Bounds;
import kit.ral.map.MapElement;
import kit.ral.map.Node;

/**
 * Creates a QuadTree with maximum leaf size 16384. Since elements are not saved,
 * the filling of the QuadTree must be repeatedly fully done until
 * <code>isRefillNeeded()</code> returns <code>false</code>.<br><br>
 * 
 * The QuadTree's leaves can be converted to new separate QuadTrees using
 * <code>determineQuadTrees()</code>. Those QuadTrees then can be filled
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

    public Bounds getBounds() {
        return root.getBounds();
    }

    public void add(Node node) {
        if (!root.add(node)) {
            refillNeeded = true;
        }
    }
    
    public void add(MapElement element) {
        if (!root.add(element)) {
            refillNeeded = true;
        }
    }
    
    public boolean isRefillNeeded() {
        boolean result = refillNeeded;
        refillNeeded = false;
        return result;
    } 
    
    public FileQuadTree buildDividedQuadTree(HashSet<FileQuadTree> divisions) {
        return root.buildDividedQuadTree(divisions);
    }
    
    private class CountingQuadTree {
        
        
        private int size = 0;     
        
        private Bounds bounds;
        private HashSet<CountingQuadTree> children = null;

        public CountingQuadTree(Bounds bounds) {
            this.bounds = bounds.clone();
        }
        
        public Bounds getBounds() {
            return bounds;
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
                        return false;
                    }
                    for (CountingQuadTree child : children) {
                        result &= child.add(element);
                    }
                }
            }
            return result;
        };

        /*public int getSize() {
            return size;
        }*/
        
        public HashSet<CountingQuadTree> createChildren() {
            HashSet<CountingQuadTree> result = new HashSet<CountingQuadTree>(4);
            float xStep = bounds.getWidth() / 2;
            float yStep = bounds.getHeight() / 2;
            for (int i = 0; i < 4; i++) {
                Bounds newBounds = new Bounds(
                        bounds.getLeft() + xStep * (i / 2),
                        bounds.getLeft() + xStep * (i / 2 + 1),
                        bounds.getTop() + yStep * (i % 2),
                        bounds.getTop() + yStep * (i % 2 + 1)
                );
                result.add(new CountingQuadTree(newBounds));
                
            }
            children = new HashSet<CountingQuadTree>(result);
            return result;
        };
        
    }

}
