package kit.ral.map.info.geo;

import java.awt.Color;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import kit.ral.common.Bounds;
import kit.ral.controller.State;
import kit.ral.map.MapElement;

public class QTLeaf extends QuadTree {
       
    private Set<MapElement> elements = new TreeSet<MapElement>();
      
    
    // CONSTRUCTOR
    
    public QTLeaf(Bounds bounds) {
        super(bounds);
        unload();
    }
    
    
    // GETTERS & SETTERS

    @Override
    public int getSize() {
        return elements.size();
    }
    
    
    // BASIC OPERATIONS
    
    @Override
    public boolean addElement(MapElement element) {
        if (element.isInBounds(bounds)) {
            int size = getSize();
            if (size >= SIZE_LIMIT) {
                return false;
            }
            elements.add(element);
        }
        return true;
    }
    
    @Override
    public void queryElements(Bounds area, Set<MapElement> target, boolean exact) {
        if(isInBounds(area)) {
            if (QTGeographicalOperator.drawFrames) {
                State.getInstance().getActiveRenderer().addFrameToDraw(
                        this.bounds, Color.blue);
            }
            if (!exact) {
                 target.addAll(elements);   
            } else {
                for (MapElement element : elements) {
                    if (element.isInBounds(area)) {
                        target.add(element);
                    }
                }
            }
        }
    }

    
    // I/O OPERATIONS
    
    @Override
    protected void load(DataInput input) throws IOException {
        // load each base layer element via type and ID
        int len = input.readInt();
        elements = new TreeSet<MapElement>();
        for (int i = 0; i < len; i++) {
            elements.add(MapElement.loadFromInput(input));
        }
    }

    @Override
    protected void save(DataOutput output) throws IOException {
        output.writeInt(elements.size());
        for (MapElement element: elements) {
            MapElement.saveToOutput(output, element, true);
        }
    }
    
    @Override
    public void unload() {
        elements.clear();
    }
    
    
    // MISCELLANEOUS
    
    protected QTNode split() {
        QTNode result = new QTNode(bounds);
        for(MapElement element : elements) {
            result.addElement(element);
        }
        return result;
    }
    
    public boolean equals(Object other) {
        if(other == this) {
            return true;
        }
        if(!(other instanceof QTLeaf)) {
            return false;
        }
        return super.equals(other) && elements.equals(((QTLeaf) other).elements);
    }

    @Override
    public String toString(int offset, List<Integer> last) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" " + getSize() + "\n");
        return stringBuilder.toString();
    }   
    
}
