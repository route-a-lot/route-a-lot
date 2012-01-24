package kit.route.a.lot.gui.event;

import kit.route.a.lot.common.Coordinates;

public class PositionEvent extends java.util.EventObject {
    
    private static final long serialVersionUID = 1L;
    
    private Coordinates position;

    public PositionEvent(Coordinates position) {
        super(position);
        this.position = position;
    }
    
    public Coordinates getCoordinates() {
        return position;
    }
}
