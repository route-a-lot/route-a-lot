
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

package kit.ral.map;

import kit.ral.common.Coordinates;
import kit.ral.common.description.Address;
import kit.ral.common.description.POIDescription;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.MappedByteBuffer;

import static kit.ral.common.util.Util.readUTFString;

public class POINode extends Node {

    private POIDescription info = null;
    private Address address;
    
    public POINode(Coordinates position, POIDescription description, Address address){
        super(position);
        this.info = description;
        this.address = address;
    }
    
    public POINode(int id) {
        super(id);
    }
    
    public POINode() {
        super();
    }
    
    
    @Override
    public String getName() {
        return getInfo().getName();
    }
    
    @Override
    public String getFullName() {  
        if ((address == null || address.getCity().length() == 0)) {
            return getName();
        } else {
            return getName() + ", " + address.getCity();
        }
    }   
    
    public POIDescription getInfo() {
        if (this.info == null) {
            this.info = new POIDescription(null, -1, null);
        }
        return this.info;
    }
    
    @Override
    protected void load(DataInput input) throws IOException {
        super.load(input);
        this.info = new POIDescription(input.readUTF(), input.readInt(), input.readUTF());
    }
    
    @Override
    protected void load(MappedByteBuffer mmap) throws IOException {
        super.load(mmap);
        this.info = new POIDescription(readUTFString(mmap), mmap.getInt(), readUTFString(mmap));
    }

    @Override
    protected void save(DataOutput output) throws IOException {
        super.save(output);
        output.writeUTF(getInfo().getName());
        output.writeInt(getInfo().getCategory());       
        output.writeUTF(getInfo().getDescription());
    }
    
    @Override
    public boolean equals(Object other) {
        if(other == this) {
            return true;
        }
        if(!(other instanceof POINode)) {
            return false;
        }
        POINode poinode = (POINode) other;
        return super.equals(other) && info.equals(poinode.info);
    }
}
