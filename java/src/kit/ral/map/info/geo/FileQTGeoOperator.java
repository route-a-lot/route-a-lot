package kit.ral.map.info.geo;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

import kit.ral.common.Bounds;
import kit.ral.common.RandomAccessStream;
import kit.ral.common.projection.Projection;
import kit.ral.map.MapElement;
import kit.ral.map.Node;
import kit.ral.map.info.ElementDB;


public class FileQTGeoOperator extends QTGeographicalOperator {
           
    private RandomAccessStream file = null;
    private ElementDB elementDB = null;
    
    // BASIC OPERATIONS
    
    @Override
    public void fill(ElementDB elementDB) {
        if (elementDB == null) {
            throw new IllegalArgumentException();
        }
        if (file == null) {
            this.elementDB = elementDB; // store to be able to invoke fill() later
            return;
        }     

        
        // << DIVIDE SECTION >>
        // get a subtree division (one is enough as all trees are similar)
        FileQuadTreeDivider divider = new FileQuadTreeDivider(bounds); 
        // divider cannot store nodes, so we have to add them several times
        do {
            Iterator<Node> elements = elementDB.getAllNodes();
            while (elements.hasNext()) {
                divider.add(elements.next());
            }
        } while (divider.isRefillNeeded());
        
        
        // << FILL AND SAVE SECTION >>
        //long start = 0;
        try {     
            file.setRandomAccess(true);
            //start = output.getFilePointer();
            // save map bounds
            bounds.saveToOutput(file);
            // tree location table reservation
            long mark = file.getPosition();
            for (int detail = 0; detail < NUM_LEVELS; detail++) {
                file.writeLong(0);
            }
            // build tree for each detail level
            for (int detail = 0; detail < NUM_LEVELS; detail++) {
                float range = Projection.getZoomFactor(detail) * LAYER_MULTIPLIER; // for reducing
                    
                    // get current tree branches (same division for all), prepare arrays
                    HashSet<FileQuadTree> branchSet = new HashSet<FileQuadTree>();
                    trees[detail] = divider.buildTrunk(branchSet);
                    FileQuadTree[] branchRoots = branchSet.toArray(new FileQuadTree[branchSet.size()]);
                    // pick each tree branch of the current tree
                    for (int i = 0; i < branchRoots.length; i++) {
                        // add all relevant elements
                        Iterator<MapElement> elements = elementDB.getAllMapElements();
                        while (elements.hasNext()) {
                            MapElement element = elements.next().getReduced(detail, range);
                            if (element != null) {
                                branchRoots[i].addElement(element);
                            }
                        }
                        // save tree branch and remove it from RAM
                        branchRoots[i].saveTree(file);
                        branchRoots[i].unload();
                    }
                    // register tree trunk location
                    long pos = file.getPosition();
                    file.setPosition(mark + detail * 8);
                    file.writeLong(pos);
                    file.setPosition(pos);
                    // save tree trunk (branches will be linked)
                    ((FileQuadTree) trees[detail]).saveTree(file);                 
            }
            file.setRandomAccess(false);
        } catch (IOException e) {
            // can't throw IO exception as signature does not allow that:
            throw new IllegalArgumentException(e);
        }
        
                    
        // << PASSING ON SECTION >>
        // relevant: zoomlevels, output, start
    }

    @Override
    public void loadFromInput(DataInput input) throws IOException {
        if (!(input instanceof RandomAccessStream)) {
            throw new IllegalArgumentException();
        }
        file = (RandomAccessStream) input;
        bounds = Bounds.loadFromInput(file);
        for (int detail = 0; detail < NUM_LEVELS; detail++) {
            trees[detail] = new FileQuadTree(bounds, file, file.readLong());
        }
    }

    @Override
    public void saveToOutput(DataOutput output) throws IOException {
        if (!(output instanceof RandomAccessStream)) {
            throw new IllegalArgumentException();
        }
        file = (RandomAccessStream) output;
        fill(this.elementDB);
        compactify();
    }   

}
