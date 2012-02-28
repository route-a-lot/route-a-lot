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

    private MapElement[] overlay;
    private MapElement[] baseLayer;
    
    private static final int MAX_SIZE = 64;     //elements per Leaf -> performance-tests
        
    public QTLeaf(Coordinates upLeft, Coordinates bottomRight) {
        super(upLeft, bottomRight);
        clear();
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
                && java.util.Arrays.equals(overlay, comparee.overlay)
                && java.util.Arrays.equals(baseLayer, comparee.overlay);
    }

    @Override
    protected boolean addToOverlay(MapElement element) {
        if (element.isInBounds(getUpLeft(), getBottomRight())) {
            int size = countArrayElementsSize(overlay);
            if (size >= MAX_SIZE) {
                return false;
            }      
            if(size == overlay.length) {
                overlay = doubleSpace(overlay);
            }
            overlay[size] = element;
            
        }
        return true;
    }

    @Override
    protected boolean addToBaseLayer(MapElement element) {
        if (element.isInBounds(getUpLeft(), getBottomRight())) {
            int size = countArrayElementsSize(baseLayer);
            if (size >= MAX_SIZE) {
                return false;
            }
            if(size == baseLayer.length) {
                baseLayer = doubleSpace(baseLayer);
            }
            baseLayer[size] = element;
        }
        return true;
    }
    
    protected QTNode splitLeaf() {
        QTNode result = new QTNode(getUpLeft(), getBottomRight());
        for(int i = 0; i < countArrayElementsSize(baseLayer); i++) {
            result.addToBaseLayer(baseLayer[i]);
        }
        for(int i = 0; i < countArrayElementsSize(overlay); i++) {
            result.addToOverlay(overlay[i]);
        }
        return result;
    }
    
    @Override
    public String toString(int offset, List<Integer> last) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" " + countElements() + "\n");
        return stringBuilder.toString();
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
    
    private MapElement[] doubleSpace(MapElement[] elements) {
        MapElement[] returnArray = new MapElement[elements.length * 2];
        for(int i = 0; i < elements.length; i++) {
            returnArray[i] = elements[i];
        }
        return returnArray;
    }
    
    @Override
    public int countElements() {
        return countArrayElementsSize(baseLayer);
    }
    
    

    @Override
    protected void load(DataInput input) throws IOException {
        // load each overlay element via type and ID
        int len = input.readInt();
        overlay = new MapElement[len];
        for (int i = 0; i < len; i++) {
            overlay[i] = MapElement.loadFromInput(input, input.readBoolean());
        }
        // load each base layer element via type and ID
        len = input.readInt();
        baseLayer = new MapElement[len];
        for (int i = 0; i < len; i++) {
            baseLayer[i] = MapElement.loadFromInput(input, input.readBoolean());
        }
    }

    @Override
    protected void save(DataOutput output) throws IOException {
        // for each overlay element, save type and ID
        output.writeInt(countArrayElementsSize(overlay));
        for (MapElement element: overlay) {
            if (element != null) {
                output.writeBoolean(element.getID() >= 0);
                MapElement.saveToOutput(output, element, element.getID() >= 0);
            }
        }
        // for each base layer element, save type and ID
        output.writeInt(countArrayElementsSize(baseLayer));
        for (MapElement element: baseLayer) {
            if (element != null) {
                output.writeBoolean(element.getID() >= 0);
                MapElement.saveToOutput(output, element, element.getID() >= 0);
            }
        }
    }

    @Override
    protected void queryBaseLayer(Coordinates upLeft, Coordinates bottomRight,
        Set<MapElement> elememts, boolean exact) {
        if(isInBounds(upLeft, bottomRight)) {
            if (QTGeographicalOperator.DRAW_FRAMES) {
                State.getInstance().getActiveRenderer().addFrameToDraw(this.upLeft, this.bottomRight, Color.blue);
            }
            for (int i = 0; i < countArrayElementsSize(baseLayer); i++) {
                if (!exact || baseLayer[i].isInBounds(upLeft, bottomRight)) {   //TODO test what's faster
                    elememts.add(baseLayer[i]);
                }
            }
        }
    }

    @Override
    protected void queryOverlay(Coordinates upLeft, Coordinates bottomRight,
            Set<MapElement> elememts, boolean exact) {
        if(isInBounds(upLeft, bottomRight)) {
            for (int i = 0; i < countArrayElementsSize(overlay); i++) {
                if (!exact || overlay[i].isInBounds(upLeft, bottomRight)) {
                    elememts.add(overlay[i]);
                }
            }
        }
        
    }

    @Override
    protected void compactifyDataStructures() {
        overlay = Arrays.copyOf(overlay, countArrayElementsSize(overlay));
        baseLayer = Arrays.copyOf(baseLayer, countArrayElementsSize(baseLayer));
    }

    @Override
    public void clear() {
        overlay = new MapElement[1];
        baseLayer = new MapElement[1];
    }
}
