package kit.route.a.lot.gui;

import java.util.EventObject;

import kit.route.a.lot.common.Coordinates;


public class AddFavEvent extends EventObject {
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


    public AddFavEvent(Object source, Coordinates position, String name, String description) {
        super(source);
        this.position = position;
        this.name = name;
        this.description = description;
    }
    
}
