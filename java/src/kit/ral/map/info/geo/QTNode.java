package kit.ral.map.info.geo;

import java.awt.Color;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import kit.ral.common.Bounds;
import kit.ral.controller.State;
import kit.ral.map.MapElement;
import kit.ral.map.info.geo.QuadTree;

public class QTNode extends QuadTree {
    
    private QuadTree[] children = new QuadTree[4];  

    
    // CONSTRUCTOR
    
    public QTNode(Bounds bounds) {
        super(bounds);
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
        if(element.isInBounds(bounds)) {
            for (int i = 0; i < children.length; i++) {
                if (!children[i].addElement(element)) {
                    children[i] = ((QTLeaf) children[i]).split();
                    children[i].addElement(element);  //we cant't add this directly in QTLeaf (array -> outOfBounds)
                }
            }
        }
        return true;
    }
     
    @Override
    public void queryElements(Bounds area, Set<MapElement> elements, boolean exact) {
        if (isInBounds(area)) {
            if (QTGeographicalOperator.drawFrames) {
                State.getInstance().getActiveRenderer().addFrameToDraw(bounds, Color.black);
            }
            for(QuadTree qt : children) {
                qt.queryElements(area, elements, exact);
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
        float widthHalf = bounds.getWidth() / 2;
        float heightHalf = bounds.getHeight() / 2;
        children[0] = new QTLeaf(bounds.clone().extend(-widthHalf, 0, 0, -heightHalf));
        children[1] = new QTLeaf(bounds.clone().extend(0, -widthHalf, -heightHalf, 0));
        children[2] = new QTLeaf(bounds.clone().extend(0, -widthHalf, 0, -heightHalf));
        children[3] = new QTLeaf(bounds.clone().extend(-widthHalf, 0, -heightHalf, 0)); 
        /*float width = bounds.getWidth() / 2;
        float height = bounds.getHeight() / 2;
        for (int i = 0; i < 4; i++) {
            Coordinates topLeft = bounds.getTopLeft().add(height * (i % 2), width * (i / 2));  
            children[i] = new QTLeaf(new Bounds(topLeft, topLeft.clone().add(height, width)));
        }*/
    }
    
    @Override
    public void compactify() {
        for(QuadTree qt : children) {
            qt.compactify();
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