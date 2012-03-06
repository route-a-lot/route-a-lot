package kit.route.a.lot.map.infosupply;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashSet;
import java.util.Iterator;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Projection;
import kit.route.a.lot.controller.State;
import kit.route.a.lot.map.MapElement;
import kit.route.a.lot.map.Node;


public class FileQTGeoOperator {
    
    public final static int NUM_LEVELS = 10;
    public static final float LAYER_MULTIPLIER = 3;
              
    // brainstorming method ;)
    public void doItAll(Coordinates topLeft, Coordinates bottomRight, RandomAccessFile output) throws IOException {
        ElementDB elementDB = State.getInstance().getMapInfo().getElementDB();
        // << DIVIDE SECTION >>
        // get a subtree division (one is enough as all trees are similar, same for only adding Nodes)
        FileQuadTreeDivider divider = new FileQuadTreeDivider(topLeft, bottomRight); 
        // divider cannot store nodes, so we have to add them several times
        do {
            Iterator<Node> nodes = elementDB.getAllNodes();
            while (nodes.hasNext()) {
                divider.addNode(nodes.next());
            }
        } while (divider.refillNeeded);
        
        // << FILL AND SAVE SECTION >>
        FileQuadTree[] allRoots = new FileQuadTree[10]; // [detail] 
        // build tree for each detail level
        for (int detail = 0; detail < 10; detail++) {
            float range = Projection.getZoomFactor(detail) * LAYER_MULTIPLIER; // for reducing
                
                // get current root tree division (same for all), prepare arrays
                HashSet<FileQuadTree> subtreeSet = new HashSet<FileQuadTree>();
                allRoots[detail] = divider.buildDividedQuadTree(subtreeSet);
                FileQuadTree[] subtrees = subtreeSet.toArray(new FileQuadTree[subtreeSet.size()]);
                
                // pick each subtree of the current root tree
                for (int i = 0; i < subtrees.length; i++) {
                    // add all relevant elements
                    Iterator<MapElement> elements = elementDB.getAllMapElements();
                    while (elements.hasNext()) {
                        subtrees[i].addElement(elements.next().getReduced(detail, range));
                    }
                    // save subtree and remove it from RAM
                    subtrees[i].save(output);
                    subtrees[i].unload();
                }
                // save complete tree (already saved subtrees will be linked)
                allRoots[detail].save(output);
        }
            
        // << PASSING ON SECTION >>
        // relevant: allRoots, output
    }   

}
