
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

package kit.ral.common;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


public class Selection {


    	private int fromID;
    	private int toID;
    	private float ratio;
    	private Coordinates position;
    	private String name;
    	
    	public Selection(Coordinates position, int fromID, int toID, float ratio, String name) {
       		this.position = position;
       		this.fromID = fromID;
        	this.toID = toID;
        	this.ratio = ratio; 
        	this.name = name;
    	}
    	
        public boolean equals(Object other) {
            if(other == this) {
                return true;
            }
            if(!(other instanceof Selection)) {
                return false;
            }
            Selection comparee = (Selection) other;
            return fromID == comparee.fromID
                    && toID == comparee.toID
                    && ratio == comparee.ratio
                    && position == comparee.position
                    && name == comparee.name;
        }
        
        public boolean isOnSameEdge(Selection other) {
            boolean oneDirection = fromID == other.fromID && toID == other.toID;
            return oneDirection || (fromID == other.toID && toID == other.fromID);
        }
    
    	public String getName() {
    	    return name;
    	}
    	
    	public void setName(String name) {
    	    this.name = name;
    	}
    	
    	public int getFrom() {
        	return fromID;
    	}
    
    	public int getTo() {
       		return this.toID;
    	}
    
    	public float getRatio() {
        	return ratio;
    	}
	
    	public Coordinates getPosition(){
    	    return this.position;	
    	}
    	
    	public String toString() {
    	    return "Selection from " + fromID + " to " + toID;
    	}
    	
        public static Selection loadFromInput(DataInput input) throws IOException {
            return new Selection(Coordinates.loadFromInput(input),
                    input.readInt(), input.readInt(),
                    input.readFloat(), input.readUTF());
        }
        
        public void saveToOutput(DataOutput output) throws IOException {
            position.saveToOutput(output);
            output.writeInt(fromID);
            output.writeInt(toID);
            output.writeFloat(ratio);
            output.writeUTF(name); 
        }
}
