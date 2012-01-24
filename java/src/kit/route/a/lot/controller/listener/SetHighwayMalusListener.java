package kit.route.a.lot.controller.listener;

import java.util.EventObject;

import kit.route.a.lot.controller.Controller;
import kit.route.a.lot.gui.event.NumberEvent;


public class SetHighwayMalusListener implements RALListener {

    private Controller ctrl;
    
    public SetHighwayMalusListener(Controller ctrl) {
        this.ctrl = ctrl;
    }

    @Override
    public void handleRALEvent(EventObject event) {
        if (event instanceof NumberEvent) {
            ctrl.setHighwayMalus(((NumberEvent) event).getNumber());
        }
    }
}
