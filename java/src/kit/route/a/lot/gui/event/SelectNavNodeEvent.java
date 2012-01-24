package kit.route.a.lot.gui.event;

import kit.route.a.lot.common.Coordinates;

public class SelectNavNodeEvent extends GeneralEvent {

    /*
     *describes the Coordinates of the selected point
     */ 
    private Coordinates position;
    
    /*
     * describes the position of the navNode (1 for start, last pos. for end)
     */
    private int index;
    
    public SelectNavNodeEvent(Coordinates position, int index) {
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
