package kit.route.a.lot.gui.event;

public class SwitchNavNodesEvent extends GeneralEvent {
    
    private int first;
    private int second;
    
    public SwitchNavNodesEvent(int first, int second) {
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
