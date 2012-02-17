package kit.route.a.lot.controller.listener;

import kit.route.a.lot.controller.Controller;
import kit.route.a.lot.gui.event.GeneralEvent;


public class OptimizeRouteListener implements GeneralListener {

    Controller ctrl;
    
    public OptimizeRouteListener(Controller ctrl) {
        this.ctrl = ctrl;
    }

    @Override
    public void handleEvent(GeneralEvent event) {
        ctrl.optimizeRoute();
    }

}
