package kit.ral.common.event;

import kit.ral.common.Coordinates;


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
