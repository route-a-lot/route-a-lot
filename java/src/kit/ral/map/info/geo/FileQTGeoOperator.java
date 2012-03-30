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
import kit.ral.map.Node;
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
            Iterator<Node> elements = elementDB.getAllNodes();
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
            FileQuadTree[][] branches = new FileQuadTree[NUM_LEVELS][];
            float[] ranges = new float[NUM_LEVELS];
            for (int detail = 0; detail < NUM_LEVELS; detail++) {
                // get current tree branches (same division for all), prepare arrays
                HashSet<FileQuadTree> branchSet = new HashSet<FileQuadTree>();
                trunks[detail] = divider.buildTrunk(branchSet);
                branches[detail] = branchSet.toArray(new FileQuadTree[branchSet.size()]);
                ranges[detail] = Projection.getZoomFactor(detail) * LAYER_MULTIPLIER;
            }
                
            // pick each tree branch 
            for (int b = 0; b < branches[0].length; b++) {
                Util.startTimer();
                logger.info("QT branch " + (b + 1) + "/" + branches[0].length);
                // add all relevant elements
                Iterator<MapElement> elements = elementDB.getAllMapElements();
                while (elements.hasNext()) {
                    MapElement element = elements.next();
                    int maxZoom = getMaximumZoomlevel(element) + 1;
                    for (int d = 0; d < maxZoom; d++) {
                        MapElement reduced = element.getReduced(d, ranges[d]);
                        if (reduced != null) {
                            branches[d][b].addElement(element);
                        }
                    }
                }
                logger.info("QT branch " + (b+1) + " filling took " + Util.stopTimer());
                Util.startTimer(); 
                // save tree branches and remove them from RAM
                for (int d = 0; d < NUM_LEVELS; d++) {
                    branches[d][b].saveTree(target);
                    branches[d][b].unload();
                }
                logger.info("QT branch " + (b+1) + " saving took " + Util.stopTimer());
                
            }
            
            // save tree trunks (branches will be linked)
            for (int d = 0; d < NUM_LEVELS; d++) {       
                
                trunks[d].saveTree(target); 
                trunks[d].unload();                
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
