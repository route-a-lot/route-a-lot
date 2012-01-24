package kit.route.a.lot.gui.event;

import kit.route.a.lot.common.Coordinates;

public class PositionEvent extends GeneralEvent {
    
    private Coordinates position;

    public PositionEvent(Coordinates position) {
        this.position = position;
    }
    
    public Coordinates getCoordinates() {
        return position;
    }
}
