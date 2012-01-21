package kit.route.a.lot.gui;

import java.util.EventObject;


public class switchNaveNodesEvent extends EventObject {
    private int first;
    private int second;
    
    public int getFirst() {
        return first;
    }

    
    public int getSecond() {
        return second;
    }

    public switchNaveNodesEvent(Object source, int first, int second) {
        super(source);
        this.first = first;
        this.second = second;
    }
}
