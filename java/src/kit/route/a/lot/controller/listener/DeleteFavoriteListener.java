package kit.route.a.lot.controller.listener;

import kit.route.a.lot.controller.Controller;
import kit.route.a.lot.gui.event.GeneralEvent;
import kit.route.a.lot.gui.event.PositionEvent;

public class DeleteFavoriteListener implements GeneralListener {

    private Controller ctrl;
    
    public DeleteFavoriteListener(Controller ctrl) {
        this.ctrl = ctrl;
    }

    @Override
    public void handleEvent(GeneralEvent event) {
        if (event instanceof PositionEvent) {
            ctrl.deleteFavorite(((PositionEvent) event).getCoordinates());
        }

    }

}
