package kit.route.a.lot.gui.event;


public class NavNodeNameEvent extends Event {

    private String name;
    private int index;

    public NavNodeNameEvent(String name, int index) {
        this.name = name;
        this.index = index;
    }
    
    public String getName() {
        return name;
    }
    
    public int getIndex() {
        return index;
    }
}