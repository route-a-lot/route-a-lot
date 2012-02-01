package kit.route.a.lot.controller.listener;

import kit.route.a.lot.controller.Controller;
import kit.route.a.lot.gui.event.DeleteFavoriteEvent;
import kit.route.a.lot.gui.event.GeneralEvent;

public class DeleteFavoriteListener implements GeneralListener {

    private Controller ctrl;
    
    public DeleteFavoriteListener(Controller ctrl) {
        this.ctrl = ctrl;
    }

    @Override
    public void handleEvent(GeneralEvent event) {
        if (event instanceof DeleteFavoriteEvent) {
            ctrl.deleteFavorite(((DeleteFavoriteEvent) event).getPosition());
        }

    }

}
