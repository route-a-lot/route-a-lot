package kit.ral.map.info.geo;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import org.apache.log4j.Logger;

import kit.ral.common.Bounds;
import kit.ral.common.RandomReadStream;
import kit.ral.common.RandomWriteStream;
import kit.ral.common.projection.Projection;
import kit.ral.common.util.Util;
import kit.ral.map.MapElement;
import kit.ral.map.info.ElementDB;


public class FileQTGeoOperator extends QTGeographicalOperator {

    private static Logger logger = Logger.getLogger(FileQTGeoOperator.class);
    
    private RandomWriteStream target = null;
    private ElementDB elementDB = null;
    
    // BASIC OPERATIONS

    @Override
    public void fill(ElementDB elementDB) {
        if (elementDB == null) {
            throw new IllegalArgumentException();
        }
        if (target == null) {
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
        } while (divider.startRefill());
        
        // << FILL AND SAVE SECTION >>
        try {
            long startOffset = target.getPosition();
            
            // save map bounds
            bounds.saveToOutput(target);
            
            // tree location table reservation
            long treeTableOffset = target.getPosition();
            for (int detail = 0; detail < NUM_LEVELS; detail++) {
                target.writeLong(0);
            }
            
            // build tree for each detail level
            FileQuadTree[] trunks = new FileQuadTree[NUM_LEVELS];
            for (int detail = 0; detail < NUM_LEVELS; detail++) {
                Util.startTimer();
                float range = Projection.getZoomFactor(detail) * LAYER_MULTIPLIER; // for reducing
                
                // get current tree branches (same division for all), prepare arrays
                HashSet<FileQuadTree> branchSet = new HashSet<FileQuadTree>();
                trunks[detail] = divider.buildTrunk(branchSet);
                FileQuadTree[] branchRoots = branchSet.toArray(new FileQuadTree[branchSet.size()]);
                
                // pick each tree branch of the current tree      
                for (int i = 0; i < branchRoots.length; i++) {
                    logger.info("QT " + (detail + 1) + "/" + NUM_LEVELS
                            + ": branch " + (i + 1) + "/" + branchRoots.length);
                    // add all relevant elements
                    Iterator<MapElement> elements = elementDB.getAllMapElements();
                    while (elements.hasNext()) {
                        MapElement element = elements.next();
                        if (getMaximumZoomlevel(element) >= detail) {
                            element = element.getReduced(detail, range);
                            if (element != null) {
                                branchRoots[i].addElement(element);
                            }
                        }
                    }
                    // save tree branch and remove it from RAM
                    branchRoots[i].saveTree(target);
                    branchRoots[i].unload();
                }
                logger.info("QT " + (detail+1) + " filling took " + Util.stopTimer());
                Util.startTimer();
                // save tree trunk (branches will be linked)
                trunks[detail].saveTree(target); 
                trunks[detail].unload();
                logger.info("QT " + (detail+1) + " saving took " + Util.stopTimer());
            }
            
            
            // fill tree location table
            for (int detail = 0; detail < NUM_LEVELS; detail++) {
                target.writeLongToPosition(trunks[detail].getFileOffset(),
                        treeTableOffset + detail * 8);
            }
                
            // start reading mode
            RandomReadStream source = target.openForReading();
            source.setPosition(startOffset);
            loadFromInput(source);           
            
        } catch (IOException e) {
            // can't throw IO exception as signature does not allow that:
            throw new IllegalArgumentException(e);
        }


        // << PASSING ON SECTION >>
        // relevant: zoomlevels, output, start
    }

    @Override
    public void loadFromInput(DataInput input) throws IOException {
        logger.info("load FileQT");
        if (!(input instanceof RandomReadStream)) {
            throw new IllegalArgumentException();
        }
        RandomReadStream stream = ((RandomReadStream) input).openForReading();
        stream.setPosition(((RandomReadStream) input).getPosition());
        
        bounds = Bounds.loadFromInput(stream);
        for (int detail = 0; detail < NUM_LEVELS; detail++) {
            trees[detail] = new FileQuadTree(bounds, stream, stream.readLong());
        }
    }

    @Override
    public void saveToOutput(DataOutput output) throws IOException {
        logger.info("save FileQT");
        if (!(output instanceof RandomWriteStream)) {
            throw new IllegalArgumentException();
        }
        target = (RandomWriteStream) output;
        fill(this.elementDB);  
        compactify();
    }

}
