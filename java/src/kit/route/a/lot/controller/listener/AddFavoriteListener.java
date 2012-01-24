package kit.route.a.lot.controller.listener;

import kit.route.a.lot.controller.Controller;
import kit.route.a.lot.gui.event.AddFavoriteEvent;
import kit.route.a.lot.gui.event.GeneralEvent;


public class AddFavoriteListener implements GeneralListener {
        
    private Controller ctrl;
     
    public AddFavoriteListener(Controller ctrl) {
        this.ctrl = ctrl;
    }

    public void handleEvent(GeneralEvent event) {
        if (event instanceof AddFavoriteEvent) {
            ctrl.addFavorite(((AddFavoriteEvent) event).getPosition(),
                                ((AddFavoriteEvent) event).getName(),
                                ((AddFavoriteEvent) event).getDescription());
        }

    }

}
