package kit.route.a.lot.map.infosupply;

import java.util.ArrayList;import java.util.Collection;
import java.util.HashSet;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.map.MapElement;
import kit.route.a.lot.map.infosupply.QuadTree;

public class QTLeaf extends QuadTree {

    private Collection<MapElement> overlay;
    private Collection<MapElement> baseLayer;
    
    private int limit = 64;     //elements per Leaf -> performance-tests
        
    
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
}
