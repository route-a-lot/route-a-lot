package kit.route.a.lot.controller.listener;

import java.util.EventObject;

import kit.route.a.lot.controller.Controller;
import kit.route.a.lot.gui.IntEvent;


public class SetHeightMalusListener implements RALListener {

    private Controller ctrl;
    
    public SetHeightMalusListener(Controller ctrl) {
        this.ctrl = ctrl;
    }

    @Override
    public void handleRALEvent(EventObject event) {
        if (event instanceof IntEvent) {
            ctrl.setHeightMalus(((IntEvent) event).getNumber());
        }
    }
}
