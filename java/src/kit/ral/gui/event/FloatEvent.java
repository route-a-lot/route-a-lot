package kit.route.a.lot.gui.event;


public class FloatEvent extends Event {

    private float number;
    
    public FloatEvent(float number) {
        this.number = number;
    }
    
    public float getNumber() {
        return number;
    }
    
}
