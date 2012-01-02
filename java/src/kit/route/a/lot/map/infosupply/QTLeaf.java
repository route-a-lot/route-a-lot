package kit.route.a.lot.map.infosupply;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.map.MapElement;
import kit.route.a.lot.map.infosupply.QuadTree;

public class QTLeaf extends QuadTree {

    private Set<MapElement> overlay;
    private Set<MapElement> baseLayer;
        
    
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
    protected Set<MapElement> getBaseLayer() {
        return baseLayer;
    }

    /**
     * Operation getOverlay
     * 
     * @return Set<MapElement>
     */
    protected Set<MapElement> getOverlay() {
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
    protected void addToOverlay(MapElement element) {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void addToBaseLayer(MapElement element) {
        // TODO Auto-generated method stub
        
    }
}
