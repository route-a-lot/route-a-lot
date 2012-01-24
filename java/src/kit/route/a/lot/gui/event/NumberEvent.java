package kit.route.a.lot.gui.event;

import java.util.EventObject;


public class NumberEvent extends EventObject {
    
    private static final long serialVersionUID = 1L;
    
    private int number;
    
    public NumberEvent(int number) {
        super(new Integer(number));
        this.number = number;
    }
    
    public int getNumber() {
        return number;
    }
}
