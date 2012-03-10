package kit.route.a.lot.gui.event;

import kit.route.a.lot.common.Coordinates;


public class AddFavoriteEvent extends PositionEvent {

    private String name;
    private String description;
    
    public AddFavoriteEvent(Coordinates position, String name, String description) {
        super(position);
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
   
}
