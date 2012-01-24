package kit.route.a.lot.controller.listener;

import java.util.EventObject;

import kit.route.a.lot.controller.Controller;
import kit.route.a.lot.gui.event.NumberEvent;
import kit.route.a.lot.gui.event.PositionEvent;


public class deleteNaveNodeListener implements RALListener {

    private Controller ctrl;
     
    
    
    public deleteNaveNodeListener(Controller ctrl) {
        this.ctrl = ctrl;
    }



    @Override
    public void handleRALEvent(EventObject event) {
        if (event instanceof PositionEvent) {
            ctrl.deleteNavNode(((PositionEvent) event).getCoordinates());
        } else if (event instanceof NumberEvent) {
            ctrl.deleteNavNode(((NumberEvent) event).getNumber());
        }
    }

}
