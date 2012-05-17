
/**
Copyright (c) 2012, Matthias Grundmann, Jan Jacob, Daniel Krauß, Yvonne Braun, Josua Stabenow
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

package kit.ral.map;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.MappedByteBuffer;

import kit.ral.common.Bounds;
import kit.ral.common.Coordinates;
import kit.ral.common.Selection;
import kit.ral.controller.State;
import kit.ral.map.info.MapInfo;

public abstract class MapElement {

    protected static final String EMPTY = "";
    
    /** constants used in a stream for announcing certain element data following */
    public static final byte
        DESCRIPTOR_NODE = 1, DESCRIPTOR_STREET = 2,
        DESCRIPTOR_AREA = 3, DESCRIPTOR_POI = 4;  
    
    /** the map element identifier */
    protected int id = -1;
    
    private int usesCount = 0;
    
    /**
     * Returns the name of the map element.
     * @return the name of the map element
     */
    public abstract String getName();

    public abstract String getFullName();
    
    /**
     * Returns the MapElement ID.
     * @return the MapElement ID
     */
    public int getID() {
        return this.id;
    }
    
    public boolean equals(Object other) {
        if(other == this) {
            return true;
        }
        if(!(other instanceof MapElement)) {
            return false;
        }
        MapElement mapElement = (MapElement) other;
        return id == mapElement.id;
        
    }
    
    /**
     * Sets the MapElement ID if no ID was assigned so far.
     * @param id the new MapElement ID
     */
    public void setID(int id) {
        this.id = id;
    }
    
    /**
     * Checks whether the <code>MapElement</code> is (fully or partially) within
     * the given {@link Coordinates} range.
     * 
     * @param topLeft the northwestern corner of the boundary
     * @param bottomRight the southeastern corner of the boundary
     * @return true if the element is in the boundary
     * @throws IllegalArgumentException either argument is <code>null</code>
     */
    // used by QuadTree
    public abstract boolean isInBounds(Bounds bounds);
    
    abstract public MapElement getReduced(int detail, float range);

    /**
     * Calculates an appropriate {@link Selection} from the MapElement
     * and the selected position.
     * @return a Selection
     */
    public abstract Selection getSelection();

    
    /**
     * Loads a {@link MapElement} from the input. Before doing so determines
     * the map element type from the input and creates the map element.
     * 
     * @param input the input
     * @return the loaded {@link MapElement}
     * @throws IllegalArgumentException <b>stream</b> is <code>null</code>
     * @throws UnsupportedOperationException element type could not be determined
     * @throws IOException map element could not be loaded from the input
     */
    public static MapElement loadFromInput(DataInput input) throws IOException {
        if (input == null) {
            throw new IllegalArgumentException("Data source is null.");
        }
        byte descriptor = input.readByte();
        if (descriptor < DESCRIPTOR_NODE || descriptor > DESCRIPTOR_POI) {
            throw new IllegalArgumentException("Tried loading undefined map element.");
        }
        MapElement result;      
        if (input.readBoolean()) {
            MapInfo mapInfo = State.getInstance().getMapInfo();
            int id = input.readInt();
            result = (descriptor == DESCRIPTOR_NODE)
                    ? mapInfo.getNode(id) : mapInfo.getMapElement(id);
        } else {
            switch (descriptor) {
                case DESCRIPTOR_NODE: result = new Node(); break;
                case DESCRIPTOR_POI: result = new POINode(); break;
                case DESCRIPTOR_STREET: result = new Street(); break;
                default: result = new Area();
            }
            result.load(input);
        }
        return result;
    }
    
    public static MapElement loadFromInput(MappedByteBuffer mmap) throws IOException {
        if (mmap == null) {
            throw new IllegalArgumentException("Data source is null.");
        }
        byte descriptor = mmap.get();
        if (descriptor < DESCRIPTOR_NODE || descriptor > DESCRIPTOR_POI) {
            throw new IllegalArgumentException("Tried loading undefined map element.");
        }
        MapElement result;      
        if (mmap.get() != 0) {
            MapInfo mapInfo = State.getInstance().getMapInfo();
            int id = mmap.getInt();
            result = (descriptor == DESCRIPTOR_NODE)
                    ? mapInfo.getNode(id) : mapInfo.getMapElement(id);
        } else {
            switch (descriptor) {
                case DESCRIPTOR_NODE: result = new Node(); break;
                case DESCRIPTOR_POI: result = new POINode(); break;
                case DESCRIPTOR_STREET: result = new Street(); break;
                default: result = new Area();
            }
            result.load(mmap);
        }
        return result;
    }

    /**
     * Saves a map element (or if possible its ID if <code>allowAsID</code> is set) to the output.
     * Before doing so determines the map element type and saves it to the output.
     * 
     * @param output the output
     * @param element the {@link MapElement} that is to be saved
     * @param allowAsID determines whether the element itself or rather its ID is saved
     * @throws IllegalArgumentException either argument is <code>null</code>
     * @throws IOException <b>element</b> could not be saved to the output
     */
    public static void saveToOutput(DataOutput output, MapElement element, boolean allowAsID) throws IOException {     
        if ((output == null) || (element == null)) {
            throw new IllegalArgumentException();
        }
        
        byte descriptor;
        if (element instanceof POINode) {
           descriptor = DESCRIPTOR_POI;  
        } else if (element instanceof Node) {
           descriptor = DESCRIPTOR_NODE; 
        } else if (element instanceof Street) {
           descriptor = DESCRIPTOR_STREET; 
        } else if (element instanceof Area) {
           descriptor = DESCRIPTOR_AREA; 
        } else {
            throw new UnsupportedOperationException(
                    "Cannot save element of type" + element.getClass().getName());
        }
        output.writeByte(descriptor);
        
        boolean asID = allowAsID && (element.getID() >= 0);
        output.writeBoolean(asID);
        if (asID) {
            output.writeInt(element.getID());
        } else {
            element.save(output);
        }
    }

    /**
     * Loads a map element from the input.
     * 
     * @param input the source input
     * @throws NullPointerException <b>input</b> is <code>null</code>
     * @throws IOException element could not be loaded from the input  
     */
    protected abstract void load(DataInput input) throws IOException;
    
    protected abstract void load(MappedByteBuffer mmap) throws IOException;

    /**
     * Saves a map element to the output.
     * 
     * @param output the destination output
     * @throws NullPointerException <b>output</b> is <code>null</code>
     * @throws IOException element could not be saved to the output
     */
    protected abstract void save(DataOutput output) throws IOException;
    
    //public abstract boolean equals(MapElement other);

    //public abstract int compare(MapElement one, MapElement other);
       
    public void registerUse() {
        usesCount++;
    }
   
    public void unregisterUse() {
        usesCount--;
    }
    
    public int getUsesCount() {
        return usesCount;
    }
    
 
}