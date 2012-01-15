package kit.route.a.lot.map.infosupply;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.controller.State;
import kit.route.a.lot.map.MapElement;
import kit.route.a.lot.map.infosupply.QuadTree;

public class QTLeaf extends QuadTree {

    private ArrayList<MapElement> overlay;
    private ArrayList<MapElement> baseLayer;
    
    private static final int limit = 64;     //elements per Leaf -> performance-tests
        
    public QTLeaf(Coordinates upLeft, Coordinates bottomRight) {
        super(upLeft, bottomRight);
        overlay = new ArrayList<MapElement>();
        baseLayer = new ArrayList<MapElement>();
    }

    /**
     * Operation getBaseLayer
     * 
     * @return Set<MapElement>
     */
    protected Collection<MapElement> getBaseLayer() {
        return baseLayer;
    }

    /**
     * Operation getOverlay
     * 
     * @return Set<MapElement>
     */
    protected Collection<MapElement> getOverlay() {
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
            if (overlay.size() == limit) {
                return false;
            }
            overlay.add(element);
        }
        return true;
    }

    @Override
    protected boolean addToBaseLayer(MapElement element) {
        if (element.isInBounds(getUpLeft(), getBottomRight())) {
            if (baseLayer.size() == limit) {
                return false;
            }
            baseLayer.add(element);
        }
        return true;
    }
    
    @Override
    public String print(int offset, List<Integer> last) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" " + countElements() + "\n");
        return stringBuilder.toString();
    }
    
    @Override
    public int countElements() {
        return baseLayer.size();
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
        stream.writeInt(overlay.size());
        for (MapElement element: overlay) {
            MapElement.saveToStream(stream, element, true);
        }
        // for each base layer element, save type and ID
        stream.writeInt(baseLayer.size());
        for (MapElement element: baseLayer) {
            MapElement.saveToStream(stream, element, true);
        }
    }
}
