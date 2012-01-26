package kit.route.a.lot.common;


public class Selection {


    	private int fromID;
    	private int toID;
    	private float ratio;
    	private Coordinates position;
    	private String name;
    	
    	public Selection(int fromID, int toID, float ratio, Coordinates position) {
       		this.fromID = fromID;
        	this.toID = toID;
        	this.ratio = ratio;
        	this.position = position;  
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
}
