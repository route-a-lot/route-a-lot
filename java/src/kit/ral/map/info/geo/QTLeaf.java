package kit.ral.map.info.geo;

import java.awt.Color;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import kit.ral.common.Bounds;
import kit.ral.common.util.Util;
import kit.ral.controller.State;
import kit.ral.map.MapElement;

public class QTLeaf extends QuadTree {
       
    private MapElement[] elements;
      
    
    // CONSTRUCTOR
    
    public QTLeaf(Bounds bounds) {
        super(bounds);
        unload();
    }
    
    
    // GETTERS & SETTERS

    @Override
    public int getSize() {
        return Util.countNonNullElements(elements);
    }
    
    
    // BASIC OPERATIONS
    
    @Override
    public boolean addElement(MapElement element) {
        if (element.isInBounds(bounds)) {
            int size = getSize();
            if (size >= MAX_SIZE) {
                return false;
            }
            // if needed double elements array length
            if(size == elements.length) {        
                MapElement[] newElements = new MapElement[(elements.length == 0) ? 2 : elements.length * 2];
                for(int i = 0; i < elements.length; i++) {
                    newElements[i] = elements[i];
                }
                elements = newElements;
            }
            // add element
            elements[size] = element;
        }
        return true;
    }
    
    @Override
    public void queryElements(Bounds area, Set<MapElement> elememts, boolean exact) {
        if(isInBounds(area)) {
            if (QTGeographicalOperator.drawFrames) {
                State.getInstance().getActiveRenderer().addFrameToDraw(
                        this.bounds, Color.blue);
            }
            for (int i = 0; i < Util.countNonNullElements(elements); i++) {
                if (!exact || elements[i].isInBounds(area)) {   //TODO test what's faster
                    elememts.add(elements[i]);
                }
            }
        }
    }

    
    // I/O OPERATIONS
    
    @Override
    protected void load(DataInput input) throws IOException {
        // load each base layer element via type and ID
        int len = input.readInt();
        elements = new MapElement[len];
        for (int i = 0; i < len; i++) {
            elements[i] = MapElement.loadFromInput(input);
        }
    }

    @Override
    protected void save(DataOutput output) throws IOException {
        // for each base layer element, save type and ID
        output.writeInt(getSize());
        for (MapElement element: elements) {
            if (element != null) {
                MapElement.saveToOutput(output, element, true);
            }
        }
    }
    
    @Override
    public void unload() {
        elements = new MapElement[1];
    }
    
    @Override
    public void compactify() {
        elements = Arrays.copyOf(elements, getSize());
    }
    
    
    // MISCELLANEOUS
    
    protected QTNode split() {
        QTNode result = new QTNode(bounds);
        for(int i = 0; i < Util.countNonNullElements(elements); i++) {
            result.addElement(elements[i]);
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
        QTLeaf comparee = (QTLeaf) other;
        return super.equals(other) 
                && java.util.Arrays.equals(elements, comparee.elements);
    }

    @Override
    public String toString(int offset, List<Integer> last) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" " + getSize() + "\n");
        return stringBuilder.toString();
    }   
    
}
