package kit.route.a.lot.gui;


public class PathEvent extends java.util.EventObject {

    private static final long serialVersionUID = 1L;
    
    private String path;

    public PathEvent(Object source, String path) {
        super(source);
        this.path = path;
    }

    
    public String getPath() {
        return path;
    }
}
