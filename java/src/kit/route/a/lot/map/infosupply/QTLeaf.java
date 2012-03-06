package kit.route.a.lot.map.infosupply;

import java.awt.Color;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.controller.State;
import kit.route.a.lot.map.MapElement;

public class QTLeaf extends QuadTree {
       
    private MapElement[] elements;
      
    
    public QTLeaf(Coordinates upLeft, Coordinates bottomRight) {
        super(upLeft, bottomRight);
        clear();
    }

    @Override
    protected boolean addElement(MapElement element) {
        if (element.isInBounds(getUpLeft(), getBottomRight())) {
            int size = countArrayElementsSize(elements);
            if (size >= MAX_SIZE) {
                return false;
            }
            if(size == elements.length) {
                elements = doubleSpace(elements);
            }
            elements[size] = element;
        }
        return true;
    }
    
    @Override
    protected void queryElements(Coordinates upLeft, Coordinates bottomRight,
        Set<MapElement> elememts, boolean exact) {
        if(isInBounds(upLeft, bottomRight)) {
            if (QTGeographicalOperator.DRAW_FRAMES) {
                State.getInstance().getActiveRenderer().addFrameToDraw(this.topLeft, this.bottomRight, Color.blue);
            }
            for (int i = 0; i < countArrayElementsSize(elements); i++) {
                if (!exact || elements[i].isInBounds(upLeft, bottomRight)) {   //TODO test what's faster
                    elememts.add(elements[i]);
                }
            }
        }
    }

    
    protected QTNode splitLeaf() {
        QTNode result = new QTNode(getUpLeft(), getBottomRight());
        for(int i = 0; i < countArrayElementsSize(elements); i++) {
            result.addElement(elements[i]);
        }
        return result;
    }
    
    @Override
    public int countElements() {
        return countArrayElementsSize(elements);
    }
    
    /**
     * Returns a new array with the same elements as the given array but twice the size.
     * If the given array is empty a new array with size of 2 is returned.
     * @param elements
     * @return
     */
    private MapElement[] doubleSpace(MapElement[] elements) {
        if (elements.length == 0) {
            return new MapElement[2];
        }
        MapElement[] returnArray = new MapElement[elements.length * 2];
        for(int i = 0; i < elements.length; i++) {
            returnArray[i] = elements[i];
        }
        return returnArray;
    }
    
    private int countArrayElementsSize(MapElement[] elements) {
        int size = 0;
        for(int i = 0; i < elements.length; i++) {
            if (elements[i] != null) {
                size++;
            }
        }
        return size;
    }
    
    @Override
    public void clear() {
        elements = new MapElement[1];
    }
    
    @Override
    protected void compactifyDataStructures() {
        elements = Arrays.copyOf(elements, countArrayElementsSize(elements));
    }


    @Override
    protected void load(DataInput input) throws IOException {
        // load each base layer element via type and ID
        int len = input.readInt();
        elements = new MapElement[len];
        for (int i = 0; i < len; i++) {
            elements[i] = MapElement.loadFromInput(input, input.readBoolean());
        }
    }

    @Override
    protected void save(DataOutput output) throws IOException {
        // for each base layer element, save type and ID
        output.writeInt(countArrayElementsSize(elements));
        for (MapElement element: elements) {
            if (element != null) {
                output.writeBoolean(element.getID() >= 0);
                MapElement.saveToOutput(output, element, element.getID() >= 0);
            }
        }
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
        stringBuilder.append(" " + countElements() + "\n");
        return stringBuilder.toString();
    }
    
}
