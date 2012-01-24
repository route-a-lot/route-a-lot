package kit.route.a.lot.gui.event;

import java.util.EventObject;


public class IntEvent extends EventObject {
    
    private static final long serialVersionUID = 1L;
    
    private int number;
    
    public int getNumber() {
        return number;
    }

    public IntEvent(Object source, int number) {
        super(source);
        this.number = number;
    }
}
