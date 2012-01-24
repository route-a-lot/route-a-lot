package kit.route.a.lot.gui.event;

import java.util.EventObject;


public class NavNodesSwitchedEvent extends EventObject {

    private static final long serialVersionUID = 1L;
    
    private int first;
    private int second;
    
    public int getFirst() {
        return first;
    }

    
    public int getSecond() {
        return second;
    }

    public NavNodesSwitchedEvent(Object source, int first, int second) {
        super(source);
        this.first = first;
        this.second = second;
    }
}
