package kit.route.a.lot.controller.listener;

import kit.route.a.lot.controller.Controller;
import kit.route.a.lot.gui.event.GeneralEvent;
import kit.route.a.lot.gui.event.NumberEvent;

public class HeightMalusListener implements GeneralListener {

    private Controller ctrl;
    
    public HeightMalusListener(Controller ctrl) {
        this.ctrl = ctrl;
    }

    @Override
    public void handleEvent(GeneralEvent event) {
        if (event instanceof NumberEvent) {
            ctrl.setHeightMalus(((NumberEvent) event).getNumber());
        }
    }
}