package kit.route.a.lot.common;

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
