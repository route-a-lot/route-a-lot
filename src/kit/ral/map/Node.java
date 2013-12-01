
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

import kit.ral.common.Bounds;
import kit.ral.common.Coordinates;
import kit.ral.common.Selection;
import kit.ral.controller.State;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.MappedByteBuffer;

public class Node extends MapElement {

    private float lat;
    private float lon;

    public boolean equals(Object other) {
        if(other == this) {
            return true;
        }
        if(!(other instanceof Node)) {
            return false;
        }
        Node node = (Node) other;
        return lat == node.lat && lon == node.lon;
    }
    
    public Node(Coordinates pos, int id) {
        this.id = id;
        lat = pos.getLatitude();
        lon = pos.getLongitude();
    }
    
    public Node(Coordinates pos) {
        lat = pos.getLatitude();
        lon = pos.getLongitude();
    }

    public Node(int id) {
        this.id = id;
        lat = 0;
        lon = 0;
    }
    
    public Node() {
        lat = 0;
        lon = 0;
    }

    public Coordinates getPos() {
        return new Coordinates(lat, lon);
    }
    
    float getLatitude() {
        return lat;
    }

    float getLongitude() {
        return lon;
    }

    @Override
    public String getName() {
        return null;
    }
    
    @Override
    public String getFullName() {
        return getName();
    }

    @Override
    public boolean isInBounds(Bounds bounds) {
        return (bounds.getTop() < lat) && (bounds.getBottom() > lat) 
            && (bounds.getLeft() < lon) && (bounds.getRight() > lon);
    }

    @Override
    public Selection getSelection() {
        Selection sel = State.getInstance().getMapInfo().select(getPos());
        sel.setName(getFullName());
        return sel;
    }


    @Override
    public MapElement getReduced(int detail, float range) {
        return (detail > 2) ? null : this;
    }

    @Override
    protected void load(DataInput input) throws IOException {
        this.id = input.readInt();
        this.lat = input.readFloat();
        this.lon = input.readFloat();
    }
    
    @Override
    protected void load(MappedByteBuffer mmap) throws IOException {
        this.id = mmap.getInt();
        this.lat = mmap.getFloat();
        this.lon = mmap.getFloat();
    }

    @Override
    protected void save(DataOutput output) throws IOException {
        output.writeInt(id);
        output.writeFloat(this.lat);
        output.writeFloat(this.lon);
    }
    
    @Override
    public String toString() {
        return "Node " + id + " at " + lat + " , " + lon;
    }

}
