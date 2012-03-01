package kit.route.a.lot.map.infosupply;

import java.util.HashSet;
import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.map.Node;

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

    CountingQuadTree root = null;
    boolean refillNeeded = false;
    
    public FileQuadTreeDivider(Coordinates topLeft, Coordinates bottomRight) {
        root = new CountingQuadTree(topLeft, bottomRight);
    }

    public void getBounds(Coordinates topLeft, Coordinates bottomRight) {
        if (topLeft != null) {
            topLeft.setLatitude(root.getTopLeft().getLatitude());
            topLeft.setLongitude(root.getTopLeft().getLongitude());
        }
        if (bottomRight != null) {
            bottomRight.setLatitude(root.getBottomRight().getLatitude());
            bottomRight.setLongitude(root.getBottomRight().getLongitude());
        }
    }

    public void addNode(Node node) {
        if (!root.add(node)) {
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
        
        private static final int MAX_SIZE = 16384;
        private int size = 0;     
        
        private Coordinates topLeft, bottomRight;
        private HashSet<CountingQuadTree> children = null;

        public CountingQuadTree(Coordinates topLeft, Coordinates bottomRight) {
            this.topLeft = topLeft;
            this.bottomRight = bottomRight;
        }
        
        public FileQuadTree buildDividedQuadTree(HashSet<FileQuadTree> leaves) {
            FileQuadTree result = new FileQuadTree(topLeft, bottomRight);
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
        public boolean add(Node node) {
            boolean result = true;
            if (node.isInBounds(topLeft, bottomRight)) {
                if (size++ >= MAX_SIZE) {
                    if (children == null) {
                        createChildren();
                        return false;
                    }
                    for (CountingQuadTree child : children) {
                        result &= child.add(node);
                    }
                }
            }
            return result;
        };

        /*public int getSize() {
            return size;
        }*/
        
        public Coordinates getTopLeft() {
            return topLeft;
        }

        public Coordinates getBottomRight() {
            return bottomRight;
        }

        public HashSet<CountingQuadTree> createChildren() {
            HashSet<CountingQuadTree> result = new HashSet<CountingQuadTree>(4);
            Coordinates dim = bottomRight.clone().subtract(topLeft).scale(0.5f);
            for (int i = 0; i < 4; i++) {
                Coordinates origin = topLeft.clone().add(
                        dim.getLatitude() * (i % 2), dim.getLongitude() * (i / 2));
                result.add(new CountingQuadTree(origin, origin.clone().add(dim)));
            }
            children = new HashSet<CountingQuadTree>(result);
            return result;
        };
        
    }

}
