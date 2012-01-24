package kit.route.a.lot.gui.event;

import kit.route.a.lot.common.Coordinates;


public class AddFavoriteEvent extends GeneralEvent {

    private Coordinates position;
    private String name;
    private String description;
    
    public AddFavoriteEvent(Coordinates position, String name, String description) {
        this.position = position;
        this.name = name;
        this.description = description;
    }
    
    public Coordinates getPosition() {
        return position;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
   
}
