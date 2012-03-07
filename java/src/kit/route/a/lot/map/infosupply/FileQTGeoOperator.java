package kit.route.a.lot.map.infosupply;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashSet;
import java.util.Iterator;
import kit.route.a.lot.common.Projection;
import kit.route.a.lot.map.MapElement;
import kit.route.a.lot.map.Node;


public class FileQTGeoOperator extends QTGeographicalOperator {
           
    private RandomAccessFile output;

    // GETTERS & SETTERS
    
    public void setOutputFile(RandomAccessFile output) {
        this.output = output;
    }
    
    
    // BASIC OPERATIONS
    
    public void fill(ElementDB elementDB) {
        if (output == null) {
            throw new IllegalStateException();
        }
        if (elementDB == null) {
            throw new IllegalArgumentException();
        }
        
        // << DIVIDE SECTION >>
        // get a subtree division (one is enough as all trees are similar)
        FileQuadTreeDivider divider = new FileQuadTreeDivider(topLeft, bottomRight); 
        // divider cannot store nodes, so we have to add them several times
        do {
            Iterator<Node> nodes = elementDB.getAllNodes();
            while (nodes.hasNext()) {
                divider.addNode(nodes.next());
            }
        } while (divider.isRefillNeeded());
        
        // << FILL AND SAVE SECTION >>
        try {           
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
        } catch (IOException e) {
            // can't throw IO exception as signature does not allow that:
            throw new IllegalArgumentException();
        }
            
        // << PASSING ON SECTION >>
        // relevant: allRoots, output
    }

    
    // UNSUPPORTED OPERATIONS
    
    @Override
    public void loadFromInput(DataInput input) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void saveToOutput(DataOutput output) throws IOException {
        throw new UnsupportedOperationException();
    }   

}
