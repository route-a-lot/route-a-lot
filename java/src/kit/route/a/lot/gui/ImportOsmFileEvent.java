package kit.route.a.lot.gui;


public class ImportOsmFileEvent extends java.util.EventObject {

    private static final long serialVersionUID = 1L;
    
    private String path;

    public ImportOsmFileEvent(Object source, String path) {
        super(source);
        this.path = path;
    }

    
    public String getPath() {
        return path;
    }
}
