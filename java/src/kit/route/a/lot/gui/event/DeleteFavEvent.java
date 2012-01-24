package kit.route.a.lot.gui.event;

import java.util.EventObject;

import kit.route.a.lot.controller.Controller;
import kit.route.a.lot.controller.listener.RALListener;


public class DeleteFavEvent implements RALListener {

    private Controller ctrl;
    
    
    public DeleteFavEvent(Controller ctrl) {
        this.ctrl = ctrl;
    }


    @Override
    public void handleRALEvent(EventObject event) {
        if (event instanceof PositionEvent) {
            ctrl.deleteFavorite(((PositionEvent) event).getCoordinates());
        }

    }

}
