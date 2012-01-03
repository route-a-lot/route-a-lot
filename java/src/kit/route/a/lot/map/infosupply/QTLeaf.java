package kit.route.a.lot.map.infosupply;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.map.MapElement;
import kit.route.a.lot.map.infosupply.QuadTree;

public class QTLeaf extends QuadTree {

    private Collection<MapElement> overlay;
    private Collection<MapElement> baseLayer;
    
    private int limit = 100;     //elements per Leaf -> performance-tests
        
    
    public QTLeaf(Coordinates upLeft, Coordinates bottomRight) {
        super(upLeft, bottomRight);
        overlay = new HashSet<MapElement>();
        baseLayer = new HashSet<MapElement>();
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
        if(isInBounds(upLeft, bottomRight)) {
            HashSet<QTLeaf> ret = new HashSet<QTLeaf>();
            ret.add(this);
        }
        return null;
    }

    @Override
    protected boolean addToOverlay(MapElement element) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected boolean addToBaseLayer(MapElement element) {
        // TODO Auto-generated method stub
        return false;
    }
}
