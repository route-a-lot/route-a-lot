package kit.route.a.lot.controller.listener;

import java.util.EventObject;

import kit.route.a.lot.controller.Controller;
import kit.route.a.lot.gui.PositionEvent;


public class DeleteFavListener implements RALListener {

    private Controller ctrl;
    
    public DeleteFavListener(Controller ctrl) {
        this.ctrl = ctrl;
    }

    @Override
    public void handleRALEvent(EventObject event) {
        if (event instanceof PositionEvent) {
            ctrl.deleteFavorite(((PositionEvent) event).getCoordinates());
        }

    }

}
