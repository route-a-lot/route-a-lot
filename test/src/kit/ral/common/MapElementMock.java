
/**
Copyright (c) 2012, Matthias Grundmann, Yvonne Braun, Josua Stabenow
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

package kit.ral.common;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.MappedByteBuffer;

import kit.ral.map.MapElement;


public class MapElementMock extends MapElement {
    
    private String name;
    private int id = -1; 
    
    public MapElementMock(String name){
        this.name = name;
    }
    
    @Override
    public String getName(){
        return name;
    }
    
    @Override
    public void setID(int id){
        this.id = id;
    }

    @Override
    public int getID(){
        return id;
    }

    @Override
    public boolean isInBounds(Bounds bounds) {
        return false;
    }

    @Override
    public MapElement getReduced(int detail, float range) {
        return null;
    }

    @Override
    public Selection getSelection() {
        return null;
    }

    @Override
    protected void load(DataInput input) throws IOException {
    }

    @Override
    protected void save(DataOutput output) throws IOException {
    }

    @Override
    protected void load(MappedByteBuffer mmap) throws IOException {
    }

    @Override
    public String getFullName() {
        return null;
    }

}
