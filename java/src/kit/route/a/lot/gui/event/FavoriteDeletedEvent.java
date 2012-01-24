package kit.route.a.lot.gui.event;

import java.util.EventObject;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.controller.Controller;
import kit.route.a.lot.controller.listener.RALListener;


public class FavoriteDeletedEvent extends EventObject {

    private static final long serialVersionUID = 1L;
    
    private Coordinates position;
        
    public FavoriteDeletedEvent(Coordinates position) {
        super(position);
        this.position = position;
    }
    
    public Coordinates getPosition() {
        return position;
    }

}
