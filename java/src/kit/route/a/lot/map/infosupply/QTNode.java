package kit.route.a.lot.map.infosupply;

import java.util.ArrayList;import java.util.Collection;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.map.MapElement;
import kit.route.a.lot.map.infosupply.QuadTree;

public class QTNode extends QuadTree {
    
    
    /*
     * 0: "upperLeft" child
     * 1: "upperRight" child
     * 2: "bottomLeft" child
     * 3: "bottomRight" child
     */
    private QuadTree[] children = new QuadTree[4];  
    
   

    public QTNode(Coordinates upLeft, Coordinates bottomRight) {
        super(upLeft, bottomRight);
        Coordinates upMiddle = new Coordinates();
        Coordinates middleLeft = new Coordinates();
        Coordinates middleMiddle = new Coordinates();
        Coordinates middleRight = new Coordinates();
        Coordinates bottomMiddle = new Coordinates();
        upMiddle.setLatitude(upLeft.getLatitude());
        upMiddle.setLongitude((upLeft.getLongitude() + bottomRight.getLongitude()) / 2);
        middleLeft.setLatitude((upLeft.getLatitude() + bottomRight.getLatitude()) / 2);
        middleLeft.setLongitude(upLeft.getLongitude());
        middleMiddle.setLatitude(middleLeft.getLatitude());
        middleMiddle.setLongitude(upMiddle.getLongitude());
        middleRight.setLatitude(middleLeft.getLatitude());
        middleRight.setLongitude(bottomRight.getLongitude());
        bottomMiddle.setLatitude(bottomRight.getLatitude());
        bottomMiddle.setLongitude(upMiddle.getLongitude());
        
        children[0] = new QTLeaf(upLeft, middleMiddle);
        children[1] = new QTLeaf(upMiddle, middleRight);
        children[2] = new QTLeaf(middleLeft, bottomMiddle);
        children[3] = new QTLeaf(middleMiddle, bottomRight);
        
    }

    @Override
    protected Collection<QTLeaf> getLeafs(Coordinates upLeft,
            Coordinates bottomRight) {
        ArrayList<QTLeaf> leafList = new ArrayList<QTLeaf>();
        if (isInBounds(upLeft, bottomRight)) {    
            for(QuadTree qt : children) {
                leafList.addAll(qt.getLeafs(upLeft, bottomRight));
            }
        }
        return leafList;
    }

    @Override
    protected boolean addToOverlay(MapElement element) {
        if(element.isInBounds(getUpLeft(), getBottomRight())) {
            for (int i = 0; i < children.length; i++) {
                if (!children[i].addToOverlay(element)) {  //full leaf
                    Collection<MapElement> temp = ((QTLeaf)children[i]).getOverlay();
                    Coordinates childUL = children[i].getUpLeft();
                    Coordinates childBR = children[i].getBottomRight();
                    children[i] = new QTNode(childUL, childBR);
                    for(MapElement ele: temp) {
                        children[i].addToOverlay(ele);
                    }
                    children[i].addToOverlay(element);
                }
            }
        }
        return true;
    }

    @Override
    protected boolean addToBaseLayer(MapElement element) {
        if(element.isInBounds(getUpLeft(), getBottomRight())) {
            for (int i = 0; i < children.length; i++) {
                if (!children[i].addToBaseLayer(element)) {  //full leaf
                    Collection<MapElement> temp = ((QTLeaf)children[i]).getBaseLayer();
                    Coordinates childUL = children[i].getUpLeft();
                    Coordinates childBR = children[i].getBottomRight();
                    children[i] = new QTNode(childUL, childBR);
                    for(MapElement ele: temp) {
                        children[i].addToBaseLayer(ele);
                    }
                    children[i].addToBaseLayer(element);
                }
            }
        }
        return true;
    }
}