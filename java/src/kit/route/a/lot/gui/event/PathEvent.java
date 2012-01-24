package kit.route.a.lot.gui.event;


public class PathEvent extends java.util.EventObject {

    private static final long serialVersionUID = 1L;
    
    private String path;

    public PathEvent(String path) {
        super(null);
        this.path = path;
    }

    
    public String getPath() {
        return path;
    }
}
