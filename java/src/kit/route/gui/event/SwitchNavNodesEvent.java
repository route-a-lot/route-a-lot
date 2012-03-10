package kit.route.a.lot.gui.event;


public class SwitchNavNodesEvent extends Event {

    private int firstID;
    private int secondID;
    
    public SwitchNavNodesEvent(int firstID, int secondID) {
        this.firstID = firstID;
        this.secondID = secondID;
    }

    
    public int getFirstID() {
        return firstID;
    }

    
    public int getSecondID() {
        return secondID;
    }
    
}
