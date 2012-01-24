package kit.route.a.lot.gui.event;

import kit.route.a.lot.common.Coordinates;

public class NavNodeSelectedEvent extends java.util.EventObject {
  
    private static final long serialVersionUID = 1L;

    /*
     *describes the Coordinates of the selected point
     */ 
    private Coordinates position;
    
    /*
     * describes the position of the navNode (1 for start, last pos. for end)
     */
    private int index;
    
    public NavNodeSelectedEvent(Coordinates position, int index) {
        super(position);
        this.position = position;
        this.index = index;
    }

    public Coordinates getPosition() {
        return position;
    }
    
    public int getIndex() {
        return index;
    }
    
}
