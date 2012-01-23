package kit.route.a.lot.gui;

import java.util.EventObject;


public class IntEvent extends EventObject {
    
    private int number;
    
    public int getNumber() {
        return number;
    }

    public IntEvent(Object source, int number) {
        super(source);
        this.number = number;
    }
}
