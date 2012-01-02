package kit.route.a.lot.map.infosupply;

import java.util.Collection;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.map.MapElement;
import kit.route.a.lot.map.infosupply.QuadTree;

public class QTNode extends QuadTree {
    
    private QuadTree left;
    private QuadTree midLeft;
    private QuadTree mirRight;
    private QuadTree right;

    public QTNode(Coordinates upLeft, Coordinates bottomRight) {
        super(upLeft, bottomRight);
        Coordinates upMiddle = new Coordinates();
        Coordinates middleLeft = new Coordinates();
        Coordinates middleMiddle = new Coordinates();
        Coordinates middleRight = new Coordinates();
        Coordinates bottomMiddle = new Coordinates();
        
    }

    /** Associations */
    private QuadTree children;

    @Override
    protected Collection<QTLeaf> getLeafs(Coordinates upLeft,
            Coordinates bottomRight) {
        // TODO Auto-generated method stub
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
