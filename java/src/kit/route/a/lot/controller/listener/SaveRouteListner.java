package kit.route.a.lot.controller.listener;

import java.util.EventObject;

import kit.route.a.lot.controller.Controller;
import kit.route.a.lot.gui.event.PathEvent;


public class SaveRouteListner implements RALListener {

    private Controller ctrl;
    
    public SaveRouteListner(Controller ctrl) {
        this.ctrl = ctrl;
    }

    @Override
    public void handleRALEvent(EventObject event) {
        if(event instanceof PathEvent) {
            ctrl.saveRoute(((PathEvent) event).getPath());
        }
    }
}
