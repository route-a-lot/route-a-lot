package kit.route.a.lot.map.infosupply;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Collection;
import java.util.HashSet;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.POIDescription;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.map.MapElement;
import kit.route.a.lot.map.Node;


public class FileQTGeoOperator implements GeographicalOperator {
    
    public static final int MODE_DIVIDE = 0, MODE_FILL = 1;
    private int mode;
    private FileQuadTreeDivider divider;
    //private HashSet<QuadTree> trees;
          
    
    public void doItAll(Coordinates topLeft, Coordinates bottomRight, Node[] nodes,
                        MapElement[] base, MapElement[] overlay, RandomAccessFile output, DataOutput positions) {
        mode = MODE_DIVIDE;
        FileQuadTreeDivider divider = new FileQuadTreeDivider(topLeft, bottomRight); 
        do {
            for (Node node : nodes) {
                divider.addNode(node);
            }
        } while (divider.refillNeeded);
        HashSet<FileQuadTree> subtrees = new HashSet<FileQuadTree>();
        FileQuadTree root = divider.buildDividedQuadTree(subtrees);
        
        mode = MODE_FILL;
        for (FileQuadTree subtree : subtrees) {
            for (MapElement ele : base) {
                subtree.addElement(ele);
            }
            for (MapElement ele : overlay) {
                subtree.addElement(ele);
            }
            try {
                positions.writeLong(output.getFilePointer());
                subtree.saveToOutput(output);
            } catch (IOException e) {
                e.printStackTrace();
            }
            subtree.unload();
        }
        
        
    }
    
    @Override
    public void setBounds(Coordinates topLeft, Coordinates bottomRight) {
        mode = MODE_DIVIDE;
        divider = new FileQuadTreeDivider(topLeft, bottomRight); 
    }

    @Override
    public void getBounds(Coordinates topLeft, Coordinates bottomRight) {
        switch (mode) {
            case MODE_DIVIDE:
            case MODE_FILL:
                divider.getBounds(topLeft, bottomRight);
        }
    }

    @Override
    public void addToBaseLayer(MapElement element) {
        switch (mode) {
            case MODE_DIVIDE:
                if (element instanceof Node) {
                    divider.addNode((Node) element);
                }
                break;
        }
    }

    @Override
    public void addToOverlay(MapElement element) {
        switch (mode) {
            case MODE_DIVIDE:
                if (element instanceof Node) {
                    divider.addNode((Node) element);
                }
                break;
        }
    }

    @Override
    public Collection<MapElement> getBaseLayer(int zoomlevel, Coordinates topLeft,
                                                Coordinates bottomRight, boolean exact) {
        return null;
    }

    @Override
    public Collection<MapElement> getOverlay(int zoomlevel, Coordinates topLeft,
                                                Coordinates bottomRight, boolean exact) {
        return null;
    }

    @Override
    public Collection<MapElement> getBaseLayer(Coordinates pos, float radius, boolean exact) {
        return null;
    }

    @Override
    public Selection select(Coordinates pos) {
        return null;
    }

    @Override
    public POIDescription getPOIDescription(Coordinates pos, float radius, int detailLevel) {
        return null;
    }

    @Override
    public void loadFromInput(DataInput input) throws IOException {
        
    }

    @Override
    public void saveToOutput(DataOutput output) throws IOException {
        
    }

    @Override
    public void compactifyDatastructures() {
        // TODO Auto-generated method stub
        
    }

}
