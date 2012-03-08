package kit.route.a.lot.map.infosupply;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashSet;
import java.util.Iterator;

import kit.route.a.lot.common.Bounds;
import kit.route.a.lot.common.Projection;
import kit.route.a.lot.map.MapElement;


public class FileQTGeoOperator extends QTGeographicalOperator {
           
    private RandomAccessFile file = null;
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
            Iterator<MapElement> elements = elementDB.getAllMapElements();
            while (elements.hasNext()) {
                divider.add(elements.next());
            }
        } while (divider.isRefillNeeded());
            
        // << FILL AND SAVE SECTION >>
        //long start = 0;
        try {           
            //start = output.getFilePointer();
            // save map bounds
            bounds.saveToOutput(file);
            // tree location table reservation
            long mark = file.getFilePointer();
            for (int detail = 0; detail < NUM_LEVELS; detail++) {
                file.writeLong(0);
            }
            
            // build tree for each detail level
            for (int detail = 0; detail < NUM_LEVELS; detail++) {
                float range = Projection.getZoomFactor(detail) * LAYER_MULTIPLIER; // for reducing
                    
                    // get current tree branches (same division for all), prepare arrays
                    HashSet<FileQuadTree> branchSet = new HashSet<FileQuadTree>();
                    trees[detail] = divider.buildDividedQuadTree(branchSet);
                    FileQuadTree[] branches = branchSet.toArray(new FileQuadTree[branchSet.size()]);
                    // pick each tree branch of the current tree
                    for (int i = 0; i < branches.length; i++) {
                        // add all relevant elements
                        Iterator<MapElement> elements = elementDB.getAllMapElements();
                        while (elements.hasNext()) {
                            MapElement element = elements.next().getReduced(detail, range);
                            if (element != null) {
                                branches[i].addElement(element);
                            }
                        }
                        // save tree branch and remove it from RAM
                        branches[i].saveTree(file);
                        branches[i].unload();
                    }
                    // register tree trunk location
                    long pos = file.getFilePointer();
                    file.seek(mark + detail * 8);
                    file.writeLong(pos);
                    file.seek(pos);
                    // save tree trunk (branches will be linked)
                    ((FileQuadTree) trees[detail]).saveTree(file);                 
            } 
        } catch (IOException e) {
            // can't throw IO exception as signature does not allow that:
            throw new IllegalArgumentException(e);
        }
            
        // << PASSING ON SECTION >>
        // relevant: zoomlevels, output, start
    }

    @Override
    public void loadFromInput(DataInput input) throws IOException {
        if (!(input instanceof RandomAccessFile)) {
            throw new IllegalArgumentException();
        }
        file = (RandomAccessFile) input;
        bounds = Bounds.loadFromInput(file);
        for (int detail = 0; detail < NUM_LEVELS; detail++) {
            trees[detail] = new FileQuadTree(bounds, file, file.readLong());
        }
    }

    @Override
    public void saveToOutput(DataOutput output) throws IOException {
        if (!(output instanceof RandomAccessFile)) {
            throw new IllegalArgumentException();
        }
        file = (RandomAccessFile) output;
        fill(this.elementDB);
        compactify();
    }   

}
