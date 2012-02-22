package kit.route.a.lot.gui.event;

import kit.route.a.lot.common.Coordinates;

public class PositionNumberEvent extends PositionEvent {

    private int number;
    
    public PositionNumberEvent(Coordinates position, int number) {
        super(position);
        this.number = number;
    }
    
    public int getNumber() {
        return number;
    }
    
}
