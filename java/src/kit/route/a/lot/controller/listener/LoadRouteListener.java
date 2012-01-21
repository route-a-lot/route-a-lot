package kit.route.a.lot.controller.listener;

import java.util.EventObject;

import kit.route.a.lot.controller.Controller;
import kit.route.a.lot.gui.PathEvent;


public class LoadRouteListener implements RALListener {

    private Controller ctrl;
    
    public LoadRouteListener(Controller ctrl) {
        this.ctrl = ctrl;
    }

    @Override
    public void handleRALEvent(EventObject event) {
        if(event instanceof PathEvent) {
            ctrl.loadRoute(((PathEvent) event).getPath());
        }
    }
}
