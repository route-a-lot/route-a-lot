package kit.ral.common.event;

import kit.ral.common.Coordinates;

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
