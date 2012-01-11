package kit.route.a.lot.common;


public class Selection {

    /** Attributes */
    /**
     * 
     */
    public int node1;
    /**
     * 
     */
    public int node2;
    /**
     * 
     */
    public float ratio;
    
    public Selection(int node1, int node2, float ratio, Coordinates coor) {
        
    }
    
    public int getFrom() {
        return node1;
    }
    
    public int getTo() {
        return node2;
    }
    
    public float getRatio() {
        return ratio;
    }
    
    /**
     * 
     */
    public Coordinates position;
    /**
     * 
     */
    public String name;
}
