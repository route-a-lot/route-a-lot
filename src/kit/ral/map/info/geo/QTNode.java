
/**
Copyright (c) 2012, Matthias Grundmann, Jan Jacob, Daniel Krauß, Josua Stabenow
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
        * Redistributions of source code must retain the above copyright
          notice, this list of conditions and the following disclaimer.
        * Redistributions in binary form must reproduce the above copyright
          notice, this list of conditions and the following disclaimer in the
          documentation and/or other materials provided with the distribution.
        * The names of the contributors may not be used to endorse or promote products
          derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
**/

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
    public void queryElements(Bounds area, Set<MapElement> target, boolean exact) {
        if (isInBounds(area)) {
            if (QTGeographicalOperator.drawFrames) {
                State.getInstance().getActiveRenderer().addFrameToDraw(bounds, Color.black);
            }
            for(QuadTree qt : children) {
                qt.queryElements(area, target, exact);
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