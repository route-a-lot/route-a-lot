package kit.route.a.lot.gui.event;

import kit.route.a.lot.common.Coordinates;

public class AddNavNodeEvent extends PositionEvent {

    private int index;
    
    public AddNavNodeEvent(Coordinates position, int index) {
        super(position);
        this.index = index;
    }
    
    public int getIndex() {
        return index;
    }
    
}
