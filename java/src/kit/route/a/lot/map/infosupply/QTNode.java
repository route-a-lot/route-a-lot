package kit.route.a.lot.map.infosupply;

import java.awt.Color;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.controller.State;
import kit.route.a.lot.map.MapElement;
import kit.route.a.lot.map.infosupply.QuadTree;

public class QTNode extends QuadTree {
    
    private QuadTree[] children = new QuadTree[4];  

    
    // CONSTRUCTOR
    
    public QTNode(Coordinates upLeft, Coordinates bottomRight) {
        super(upLeft, bottomRight);
        unload();            
    }

    
    // GETTERS & SETTERS
    
    @Override
    public int getSize() {
        int countElements = 0;
        for (QuadTree child: children) {
            countElements += child.getSize();
        }
        return countElements;
    }
    
    
    // BASIC OPERATIONS

    @Override
    public boolean addElement(MapElement element) {
        if(element.isInBounds(topLeft, bottomRight)) {
            for (int i = 0; i < children.length; i++) {
                if (!children[i].addElement(element)) {
                    children[i] = ((QTLeaf) children[i]).splitLeaf();
                    children[i].addElement(element);  //we cant't add this directly in QTLeaf (array -> outOfBounds)
                }
            }
        }
        return true;
    }
     
    @Override
    public void queryElements(Coordinates upLeft, Coordinates bottomRight,
            Set<MapElement> elements, boolean exact) {
        if (isInBounds(upLeft, bottomRight)) {
            if (QTGeographicalOperator.DRAW_FRAMES) {
                State.getInstance().getActiveRenderer().addFrameToDraw(this.topLeft, this.bottomRight, Color.black);
            }
            for(QuadTree qt : children) {
                qt.queryElements(upLeft, bottomRight, elements, exact);
          
            }    
        }
    }


    // I/O OPERATIONS
    
    @Override
    protected void load(DataInput input) throws IOException {
        for (int i = 0; i < 4; i++) {
            children[i] = QuadTree.loadFromInput(input);
        }
    }

    @Override
    protected void save(DataOutput output) throws IOException {
        for (QuadTree child: children) {
            QuadTree.saveToOutput(output, child);
        }
    }
    
    @Override
    public void unload() {
        float widthHalf = (bottomRight.getLongitude() - topLeft.getLongitude()) / 2;
        float heightHalf = (bottomRight.getLatitude() - topLeft.getLatitude()) / 2;
        Coordinates middleMiddle = topLeft.clone().add(heightHalf, widthHalf);
        children[0] = new QTLeaf(topLeft, middleMiddle);
        children[1] = new QTLeaf(topLeft.clone().add(0, widthHalf),
                            bottomRight.clone().add(-heightHalf, 0));
        children[2] = new QTLeaf(topLeft.clone().add(heightHalf, 0),
                            bottomRight.clone().add(0, -widthHalf));
        children[3] = new QTLeaf(middleMiddle, bottomRight); 
    }
    
    @Override
    public void compactifyDataStructures() {
        for(QuadTree qt : children) {
            qt.compactifyDataStructures();
        }
    }
    
    
    // MISCELLANEOUS
    
    public boolean equals(Object other) {
        if(other == this) {
            return true;
        }
        if(!(other instanceof QTNode)) {
            return false;
        }
        QTNode comparee = (QTNode) other;
        return super.equals(other) && java.util.Arrays.equals(children, comparee.children);
    }
    
    @Override
    public String toString(int offset, List<Integer> last) {
        if (offset > 50) {
            return "this seems like a good point to stop printing...\n";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("'" + getSize() + "'\n");
        
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

}