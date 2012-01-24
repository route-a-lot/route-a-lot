package kit.route.a.lot.controller.listener;

import java.util.EventObject;

import kit.route.a.lot.controller.Controller;
import kit.route.a.lot.gui.event.PathEvent;


public class ExportRouteListener implements RALListener {

    private Controller ctrl;
    
    @Override
    public void handleRALEvent(EventObject event) {
        if (event instanceof PathEvent) {
            ctrl.exportRoute(((PathEvent) event).getPath());
        }

    }

}
