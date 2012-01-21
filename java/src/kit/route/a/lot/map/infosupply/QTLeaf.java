package kit.route.a.lot.map.infosupply;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.map.MapElement;
import kit.route.a.lot.map.infosupply.QuadTree;

public class QTLeaf extends QuadTree {

    private MapElement[] overlay;
    private MapElement[] baseLayer;
    
    private static final int limit = 64;     //elements per Leaf -> performance-tests
        
    public QTLeaf(Coordinates upLeft, Coordinates bottomRight) {
        super(upLeft, bottomRight);
        overlay = new MapElement[1];
        baseLayer = new MapElement[1];
    }

    /**
     * Operation getBaseLayer
     * 
     * @return Set<MapElement>
     */
    protected MapElement[] getBaseLayer() {
        return baseLayer;
    }

    /**
     * Operation getOverlay
     * 
     * @return Set<MapElement>
     */
    protected MapElement[] getOverlay() {
        return overlay;
    }

    @Override
    protected Collection<QTLeaf> getLeafs(Coordinates upLeft,
            Coordinates bottomRight) {
        ArrayList<QTLeaf> ret = new ArrayList<QTLeaf>();
        if(isInBounds(upLeft, bottomRight)) {
            ret.add(this);
        }
        return ret;
    }

    @Override
    protected boolean addToOverlay(MapElement element) {
        if (element.isInBounds(getUpLeft(), getBottomRight())) {
            int size = countArrayElementsSize(overlay);
            if (size >= limit) {
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
            if (size >= limit) {
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
    protected void load(DataInputStream stream) throws IOException {
        // load each overlay element via type and ID
        int len = stream.readInt();
        overlay.ensureCapacity(len);
        for (int i = 0; i < len; i++) {
            overlay.add(MapElement.loadFromStream(stream, true));
        }
        // load each base layer element via type and ID
        len = stream.readInt();
        baseLayer.ensureCapacity(len);
        for (int i = 0; i < len; i++) {
            baseLayer.add(MapElement.loadFromStream(stream, true));
        }
    }

    @Override
    protected void save(DataOutputStream stream) throws IOException {
        // for each overlay element, save type and ID
        stream.writeInt(countArrayElementsSize(overlay));
        for (MapElement element: overlay) {
            MapElement.saveToStream(stream, element, true);
        }
        // for each base layer element, save type and ID
        stream.writeInt(countArrayElementsSize(baseLayer));
        for (MapElement element: baseLayer) {
            MapElement.saveToStream(stream, element, true);
        }
    }

    @Override
    protected void addBaseLayerElementsToCollection(Coordinates upLeft, Coordinates bottomRight,
            Set<MapElement> elememts) {
        if(isInBounds(upLeft, bottomRight)) {
            for (int i = 0; i < countArrayElementsSize(baseLayer); i++) {
                elememts.add(baseLayer[i]);
            }
        }
    }

    @Override
    protected void addOverlayElementsToCollection(Coordinates upLeft, Coordinates bottomRight,
            Set<MapElement> elememts) {
        if(isInBounds(upLeft, bottomRight)) {
            for (int i = 0; i < countArrayElementsSize(overlay); i++) {
                elememts.add(overlay[i]);
            }
        }
        
    }

    @Override
    protected void addBaseLayerAndOverlayElementsToCollection(Coordinates upLeft, Coordinates bottomRight,
            Set<MapElement> baseLayer, Set<MapElement> overlay) {
        if(isInBounds(upLeft, bottomRight)) {
            baseLayer.addAll(baseLayer);
            overlay.addAll(overlay);
        }
    }

    @Override
    protected void trimm() {
        MapElement[] tempOverlay = overlay;
        overlay = new MapElement[countArrayElementsSize(overlay)];
        for (int i = 0; i < overlay.length; i++) {
            overlay[i] = tempOverlay[i];
        }
        
        MapElement[] tempBaseLyer = baseLayer;
        overlay = new MapElement[countArrayElementsSize(baseLayer)];
        for (int i = 0; i < baseLayer.length; i++) {
            baseLayer[i] = tempOverlay[i];
        }
    }
}
