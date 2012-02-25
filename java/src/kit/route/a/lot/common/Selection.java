package kit.route.a.lot.common;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import kit.route.a.lot.map.Node;


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
    	
        public static Selection loadFromStream(DataInputStream stream) throws IOException {
            return new Selection(Coordinates.loadFromStream(stream),
                    stream.readInt(), stream.readInt(),
                    stream.readFloat(), stream.readUTF());
        }
        
        public void saveToStream(DataOutputStream stream) throws IOException {
            position.saveToStream(stream);
            stream.writeInt(fromID);
            stream.writeInt(toID);
            stream.writeFloat(ratio);
            stream.writeUTF(name); 
        }
}
