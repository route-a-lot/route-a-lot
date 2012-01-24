package kit.route.a.lot.controller.listener;

import java.util.EventObject;

import kit.route.a.lot.controller.Controller;
import kit.route.a.lot.gui.event.FavoriteAddedEvent;


public class AddFavListener implements RALListener {
        
    private Controller ctrl;
    
    
    
    public AddFavListener(Controller ctrl) {
        this.ctrl = ctrl;
    }



    @Override
    public void handleRALEvent(EventObject event) {
        if (event instanceof FavoriteAddedEvent) {
            ctrl.addFavorite(((FavoriteAddedEvent) event).getPosition(),
                                ((FavoriteAddedEvent) event).getName(),
                                ((FavoriteAddedEvent) event).getDescription());
        }

    }

}
