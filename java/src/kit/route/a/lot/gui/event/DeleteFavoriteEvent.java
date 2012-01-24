package kit.route.a.lot.gui.event;

import kit.route.a.lot.common.Coordinates;

public class DeleteFavoriteEvent extends GeneralEvent {

    private Coordinates position;
        
    public DeleteFavoriteEvent(Coordinates position) {
        this.position = position;
    }
    
    public Coordinates getPosition() {
        return position;
    }

}
