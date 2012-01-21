package kit.route.a.lot.controller.listener;

import java.util.EventObject;

import kit.route.a.lot.controller.Controller;
import kit.route.a.lot.gui.IntEvent;


public class SetSpeedListener implements RALListener {

    public SetSpeedListener(Controller ctrl) {
        this.ctrl = ctrl;
    }

    private Controller ctrl;
    
    @Override
    public void handleRALEvent(EventObject event) {
        if (event instanceof IntEvent) {
            ctrl.setSpeed(((IntEvent) event).getNumber());
        }
    }
}
