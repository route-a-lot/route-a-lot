package kit.route.a.lot.map.infosupply;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashSet;
import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Projection;
import kit.route.a.lot.map.MapElement;
import kit.route.a.lot.map.Node;


public class FileQTGeoOperator {
    
    private static final float LAYER_MULTIPLIER = 3;
              
    // brainstorming method ;)
    public void doItAll(Coordinates topLeft, Coordinates bottomRight, Node[] nodes,
                        MapElement[][] layers, RandomAccessFile output) throws IOException {
        // << DIVIDE SECTION >>
        // get a subtree division (one is enough as all trees are similar, same for only adding Nodes)
        FileQuadTreeDivider divider = new FileQuadTreeDivider(topLeft, bottomRight); 
        // divider cannot store nodes, so we have to add them several times
        do {
            for (Node node : nodes) {
                divider.addNode(node);
            }
        } while (divider.refillNeeded);
        
        // << FILL AND SAVE SECTION >>
        FileQuadTree[][] allRoots = new FileQuadTree[10][2]; // [detail][layer]   
        // build tree for each detail level and layer
        for (int detail = 0; detail < 10; detail++) {
            float range = Projection.getZoomFactor(detail) * LAYER_MULTIPLIER; // for reducing
            for (int layer = 0; layer < 2; layer++) {
                
                // get current root tree division (same for all), prepare arrays
                HashSet<FileQuadTree> subtreeSet = new HashSet<FileQuadTree>();
                allRoots[detail][layer] = divider.buildDividedQuadTree(subtreeSet);
                FileQuadTree[] subtrees = subtreeSet.toArray(new FileQuadTree[subtreeSet.size()]);
                
                // pich each subtree to the current root tree
                for (int i = 0; i < subtrees.length; i++) {
                    // add all relevant elements
                    for (MapElement ele : layers[layer]) {
                        subtrees[i].addElement(ele.getReduced(detail, range));
                    }
                    // save subtree and remove it from RAM
                    subtrees[i].save(output);
                    subtrees[i].unload();
                }
                // save complete tree (already saved subtrees will be linked)
                allRoots[detail][layer].save(output);
            }
        }
            
        // << PASSING ON SECTION >>
        // relevant: allRoots, output
    }   

}
