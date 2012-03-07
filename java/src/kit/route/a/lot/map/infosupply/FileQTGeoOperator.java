package kit.route.a.lot.map.infosupply;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashSet;
import java.util.Iterator;
import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Projection;
import kit.route.a.lot.controller.State;
import kit.route.a.lot.map.MapElement;
import kit.route.a.lot.map.Node;


public class FileQTGeoOperator extends QTGeographicalOperator {
    
    private Coordinates topLeft, bottomRight;
              
    // brainstorming method ;)
    public void doItAll(RandomAccessFile output) throws IOException {
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
        // build tree for each detail level
        for (int detail = 0; detail < NUM_LEVELS; detail++) {
            float range = Projection.getZoomFactor(detail) * LAYER_MULTIPLIER; // for reducing
                
                // get current root tree division (same for all), prepare arrays
                HashSet<FileQuadTree> subtreeSet = new HashSet<FileQuadTree>();
                zoomlevels[detail] = divider.buildDividedQuadTree(subtreeSet);
                FileQuadTree[] subtrees = subtreeSet.toArray(new FileQuadTree[subtreeSet.size()]);
                
                // pick each subtree of the current root tree
                for (int i = 0; i < subtrees.length; i++) {
                    // add all relevant elements
                    Iterator<MapElement> elements = elementDB.getAllMapElements();
                    while (elements.hasNext()) {
                        subtrees[i].addElement(elements.next().getReduced(detail, range));
                    }
                    // save subtree and remove it from RAM
                    subtrees[i].saveTree(output);
                    subtrees[i].unload();
                }
                // save complete tree (already saved subtrees will be linked)
                zoomlevels[detail].save(output);
        }
            
        // << PASSING ON SECTION >>
        // relevant: allRoots, output
    }

    @Override
    public void setBounds(Coordinates topLeft, Coordinates bottomRight) {
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
        this.zoomlevels = new FileQuadTree[NUM_LEVELS];
    }

    @Override
    public void getBounds(Coordinates topLeft, Coordinates bottomRight) {
        if (topLeft != null) {
            topLeft.setLatitude(topLeft.getLatitude());
            topLeft.setLongitude(topLeft.getLongitude());
        }
        if (bottomRight != null) {
            bottomRight.setLatitude(bottomRight.getLatitude());
            bottomRight.setLongitude(bottomRight.getLongitude());
        }
    }

    @Override
    public void addElement(MapElement element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void loadFromInput(DataInput input) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void saveToOutput(DataOutput output) throws IOException {
        throw new UnsupportedOperationException();
    }   

}
