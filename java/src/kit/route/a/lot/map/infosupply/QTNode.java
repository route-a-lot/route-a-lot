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
        if (isInBounds(upLeft, bottomRight)) {      // TODO nicht immer alle adden
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
                if (!children[i].addToOverlay(element)) {
                    children[i] = ((QTLeaf) children[i]).splitLeaf();
                    children[i].addToOverlay(element); //l.o.
                }
            }
        }
        return true;
    }

    @Override
    protected boolean addToBaseLayer(MapElement element) {
        if(element.isInBounds(getUpLeft(), getBottomRight())) {
            for (int i = 0; i < children.length; i++) {
                if (!children[i].addToBaseLayer(element)) {
                    children[i] = ((QTLeaf) children[i]).splitLeaf();
                    children[i].addToBaseLayer(element);  //we cant't add this directly in QTLeaf (array -> outOfBounds)
                }
            }
        }
        return true;
    }
    
    @Override
    public String toString(int offset, List<Integer> last) {
        if (offset > 50) {
            return "this seems like a good point to stop printing...\n";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("'" + countElements() + "'\n");
        
        printOffset(offset, last, stringBuilder);
        stringBuilder.append("├──");
        stringBuilder.append(children[0].toString(offset + 1, last));
        
        printOffset(offset, last, stringBuilder);
        stringBuilder.append("├──");
        stringBuilder.append(children[1].toString(offset + 1, last));
        
        printOffset(offset, last, stringBuilder);
        stringBuilder.append("├──");
        stringBuilder.append(children[2].toString(offset + 1, last));
        
        printOffset(offset, last, stringBuilder);
        stringBuilder.append("└──");
        List<Integer> newLast = new ArrayList<Integer>(last);
        newLast.add(offset);
        stringBuilder.append(children[3].toString(offset + 1, newLast));
        
        return stringBuilder.toString();
    }
    
    private void printOffset(int offset, List<Integer> last, StringBuilder stringBuilder) {
        for (int i = 0; i < offset; i++) {
            if (last.contains(i)) {
                stringBuilder.append("   ");
            } else {
                stringBuilder.append("│  ");
            }
        }
    }
    
    @Override
    public int countElements() {
        int countElements = 0;
        for (QuadTree child: children) {
            countElements += child.countElements();
        }
        return countElements;
    }

    @Override
    protected void load(DataInputStream stream) throws IOException {
        for (int i = 0; i < 4; i++) {
            children[i] = QuadTree.loadFromStream(stream);
        }
    }

    @Override
    protected void save(DataOutputStream stream) throws IOException {
        for (QuadTree child: children) {
            QuadTree.saveToStream(stream, child);
        }
    }

    @Override
    protected void addBaseLayerElementsToCollection(Coordinates upLeft, Coordinates bottomRight,
            Set<MapElement> elememts) {
        if (isInBounds(upLeft, bottomRight)) {
            for(QuadTree qt : children) {
                qt.addBaseLayerElementsToCollection(upLeft, bottomRight, elememts);
          
            }    
        }
    }

    @Override
    protected void addOverlayElementsToCollection(Coordinates upLeft, Coordinates bottomRight,
            Set<MapElement> elememts) {
        if (isInBounds(upLeft, bottomRight)) {
            for(QuadTree qt : children) {
                qt.addOverlayElementsToCollection(upLeft, bottomRight, elememts);
          
            }    
        }
        
    }

    @Override
    protected void addBaseLayerAndOverlayElementsToCollection(Coordinates upLeft, Coordinates bottomRight,
            Set<MapElement> baseLayer, Set<MapElement> overlay) {
        if (isInBounds(upLeft, bottomRight)) {
            for(QuadTree qt : children) {
                qt.addBaseLayerAndOverlayElementsToCollection(upLeft, bottomRight, baseLayer, overlay);
          
            }  
        }
    }

    @Override
    protected void trimm() {
        for(QuadTree qt : children) {
            qt.trimm();
        }
    }

}