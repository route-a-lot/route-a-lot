package kit.route.a.lot.gui.event;

public class NumberEvent extends GeneralEvent {
    
    private static final long serialVersionUID = 1L;
    
    private int number;
    
    public NumberEvent(int number) {
        this.number = number;
    }
    
    public int getNumber() {
        return number;
    }
}
