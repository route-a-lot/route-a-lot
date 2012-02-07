package kit.route.a.lot.map.infosupply;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
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
        float widthHalf = (bottomRight.getLongitude() - upLeft.getLongitude()) / 2;
        float heightHalf = (bottomRight.getLatitude() - upLeft.getLatitude()) / 2;
        Coordinates middleMiddle = upLeft.clone().add(heightHalf, widthHalf);
        children[0] = new QTLeaf(upLeft, middleMiddle);
        children[1] = new QTLeaf(upLeft.clone().add(0, widthHalf),
                                 bottomRight.clone().add(-heightHalf, 0));
        children[2] = new QTLeaf(upLeft.clone().add(heightHalf, 0),
                                 bottomRight.clone().add(0, -widthHalf));
        children[3] = new QTLeaf(middleMiddle, bottomRight);        
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
        if(element.isInBounds(upLeft, bottomRight)) {
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
    protected void queryBaseLayer(Coordinates upLeft, Coordinates bottomRight,
            Set<MapElement> elements) {
        if (isInBounds(upLeft, bottomRight)) {
            for(QuadTree qt : children) {
                qt.queryBaseLayer(upLeft, bottomRight, elements);
          
            }    
        }
    }

    @Override
    protected void queryOverlay(Coordinates upLeft, Coordinates bottomRight,
            Set<MapElement> elememts) {
        if (isInBounds(upLeft, bottomRight)) {
            for(QuadTree qt : children) {
                qt.queryOverlay(upLeft, bottomRight, elememts);
          
            }    
        }
        
    }


    @Override
    protected void compactifyDataStructures() {
        for(QuadTree qt : children) {
            qt.compactifyDataStructures();
        }
    }

}