package kit.route.a.lot.gui;

import kit.route.a.lot.common.Coordinates;



public class PositionEvent extends java.util.EventObject {
    
    private Coordinates coordinates;

    public PositionEvent(Object source, Coordinates coordinates) {
        super(source);
        this.coordinates = coordinates;
    }

    
    public Coordinates getCoordinates() {
        return coordinates;
    }
}
