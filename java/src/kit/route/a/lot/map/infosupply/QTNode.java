package kit.route.a.lot.map.infosupply;

import java.util.ArrayList;
import java.util.Collection;

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
        middleLeft.setLongitude(upMiddle.getLongitude());
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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected boolean addToOverlay(MapElement element) {
        if(element.isInBounds(getUpLeft(), getBottomRight())) {
            for (int i = 0; i < children.length; i++) {
                if (!children[i].addToOverlay(element)) {  //full leaf
                    ArrayList<MapElement> temp = (ArrayList<MapElement>) ((QTLeaf)children[i]).getOverlay();
                    children[i] = new QTNode(getUpLeft(), getBottomRight());
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
                    ArrayList<MapElement> temp = (ArrayList<MapElement>) ((QTLeaf)children[i]).getBaseLayer();
                    children[i] = new QTNode(getUpLeft(), getBottomRight());
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