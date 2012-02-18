package kit.route.a.lot.gui.event;

public class SwitchIndicesEvent extends NumberEvent {
    
    private int index2;
    
    public SwitchIndicesEvent(int index1, int index2) {
        super(index1);
        this.index2 = index2;
    } 
    
    public int getIndex2() {
        return index2;
    }
}
