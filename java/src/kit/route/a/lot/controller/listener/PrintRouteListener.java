package kit.route.a.lot.controller.listener;

import kit.route.a.lot.controller.Controller;
import kit.route.a.lot.gui.event.GeneralEvent;


public class PrintRouteListener implements GeneralListener {

    private Controller ctrl;
    
    public PrintRouteListener(Controller ctrl) {
        this.ctrl = ctrl;
    }

    @Override
    public void handleEvent(GeneralEvent event) {
       ctrl.printRoute();
    }
}
