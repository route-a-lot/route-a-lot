package kit.route.a.lot.gui.event;

import java.util.EventObject;

import kit.route.a.lot.common.Coordinates;


public class FavoriteAddedEvent extends EventObject {

    private static final long serialVersionUID = 1L;
    
    private Coordinates position;
    private String name;
    private String description;
    
    public Coordinates getPosition() {
        return position;
    }
    
    
    public String getName() {
        return name;
    }
    
    
    public String getDescription() {
        return description;
    }


    public FavoriteAddedEvent(Coordinates position, String name, String description) {
        super(null);
        this.position = position;
        this.name = name;
        this.description = description;
    }
    
}
