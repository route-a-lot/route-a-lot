package kit.route.a.lot.gui.event;

import java.util.EventObject;


public class NavNodesSwitchedEvent extends EventObject {

    private static final long serialVersionUID = 1L;
    
    private int first;
    private int second;
    
    public NavNodesSwitchedEvent(int first, int second) {
        super(new Integer(first));
        this.first = first;
        this.second = second;
    }
    
    public int getFirst() {
        return first;
    }
    
    public int getSecond() {
        return second;
    }
}
