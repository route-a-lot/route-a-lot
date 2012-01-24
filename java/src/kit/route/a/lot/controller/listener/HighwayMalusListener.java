package kit.route.a.lot.controller.listener;

import kit.route.a.lot.controller.Controller;
import kit.route.a.lot.gui.event.GeneralEvent;
import kit.route.a.lot.gui.event.NumberEvent;


public class HighwayMalusListener implements GeneralListener {

    private Controller ctrl;
    
    public HighwayMalusListener(Controller ctrl) {
        this.ctrl = ctrl;
    }

    @Override
    public void handleEvent(GeneralEvent event) {
        if (event instanceof NumberEvent) {
            ctrl.setHighwayMalus(((NumberEvent) event).getNumber());
        }
    }
}
