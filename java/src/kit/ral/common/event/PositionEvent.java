package kit.ral.common.event;

import kit.ral.common.Coordinates;

public class PositionEvent extends Event {
    
    private Coordinates position;

    public PositionEvent(Coordinates position) {
        this.position = position;
    }
    
    public Coordinates getPosition() {
        return position;
    }
}
