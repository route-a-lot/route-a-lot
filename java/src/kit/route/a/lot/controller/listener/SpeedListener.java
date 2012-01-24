package kit.route.a.lot.controller.listener;

import kit.route.a.lot.controller.Controller;
import kit.route.a.lot.gui.event.GeneralEvent;
import kit.route.a.lot.gui.event.NumberEvent;

public class SpeedListener implements GeneralListener {

    public SpeedListener(Controller ctrl) {
        this.ctrl = ctrl;
    }

    private Controller ctrl;
    
    @Override
    public void handleEvent(GeneralEvent event) {
        if (event instanceof NumberEvent) {
            ctrl.setSpeed(((NumberEvent) event).getNumber());
        }
    }
}
