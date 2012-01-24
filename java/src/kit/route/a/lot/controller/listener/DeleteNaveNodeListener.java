package kit.route.a.lot.controller.listener;

import kit.route.a.lot.controller.Controller;
import kit.route.a.lot.gui.event.GeneralEvent;
import kit.route.a.lot.gui.event.NumberEvent;
import kit.route.a.lot.gui.event.PositionEvent;

public class DeleteNaveNodeListener implements GeneralListener {

    private Controller ctrl;
        
    public DeleteNaveNodeListener(Controller ctrl) {
        this.ctrl = ctrl;
    }

    @Override
    public void handleEvent(GeneralEvent event) {
        if (event instanceof PositionEvent) {
            ctrl.deleteNavNode(((PositionEvent) event).getCoordinates());
        } else if (event instanceof NumberEvent) {
            ctrl.deleteNavNode(((NumberEvent) event).getNumber());
        }
    }

}
