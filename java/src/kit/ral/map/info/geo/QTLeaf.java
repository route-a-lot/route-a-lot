
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
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import kit.ral.common.Bounds;
import kit.ral.controller.State;
import kit.ral.map.MapElement;

public class QTLeaf extends QuadTree {
       
    private Set<MapElement> elements = new TreeSet<MapElement>();
      
    
    // CONSTRUCTOR
    
    public QTLeaf(Bounds bounds) {
        super(bounds);
        unload();
    }
    
    
    // GETTERS & SETTERS

    @Override
    public int getSize() {
        return elements.size();
    }
    
    
    // BASIC OPERATIONS
    
    @Override
    public boolean addElement(MapElement element) {
        if (element.isInBounds(bounds)) {
            int size = getSize();
            if (size >= SIZE_LIMIT) {
                return false;
            }
            elements.add(element);
        }
        return true;
    }
    
    @Override
    public void queryElements(Bounds area, Set<MapElement> target, boolean exact) {
        if(isInBounds(area)) {
            if (QTGeographicalOperator.drawFrames) {
                State.getInstance().getActiveRenderer().addFrameToDraw(
                        this.bounds, Color.blue);
            }
            if (!exact) {
                 target.addAll(elements);   
            } else {
                for (MapElement element : elements) {
                    if (element.isInBounds(area)) {
                        target.add(element);
                    }
                }
            }
        }
    }

    
    // I/O OPERATIONS
    
    @Override
    protected void load(DataInput input) throws IOException {
        // load each base layer element via type and ID
        int len = input.readInt();
        elements = new TreeSet<MapElement>();
        for (int i = 0; i < len; i++) {
            elements.add(MapElement.loadFromInput(input));
        }
    }

    @Override
    protected void save(DataOutput output) throws IOException {
        output.writeInt(elements.size());
        for (MapElement element: elements) {
            MapElement.saveToOutput(output, element, true);
        }
    }
    
    @Override
    public void unload() {
        elements.clear();
    }
    
    
    // MISCELLANEOUS
    
    protected QTNode split() {
        QTNode result = new QTNode(bounds);
        for(MapElement element : elements) {
            result.addElement(element);
        }
        return result;
    }
    
    public boolean equals(Object other) {
        if(other == this) {
            return true;
        }
        if(!(other instanceof QTLeaf)) {
            return false;
        }
        return super.equals(other) && elements.equals(((QTLeaf) other).elements);
    }

    @Override
    public String toString(int offset, List<Integer> last) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" " + getSize() + "\n");
        return stringBuilder.toString();
    }   
    
}
