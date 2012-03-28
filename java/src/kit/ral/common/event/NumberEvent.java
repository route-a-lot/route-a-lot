package kit.ral.common.event;

public class NumberEvent extends Event {
       
    private int number;
    
    public NumberEvent(int number) {
        this.number = number;
    }
    
    public int getNumber() {
        return number;
    }
}