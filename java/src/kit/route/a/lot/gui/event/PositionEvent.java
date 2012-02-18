package kit.route.a.lot.gui.event;

import kit.route.a.lot.common.Coordinates;

public class PositionEvent extends Event {
    
    private Coordinates position;

    public PositionEvent(Coordinates position) {
        this.position = position;
    }
    
    public Coordinates getPosition() {
        return position;
    }
}
