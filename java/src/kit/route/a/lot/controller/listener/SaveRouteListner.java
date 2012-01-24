package kit.route.a.lot.controller.listener;

import kit.route.a.lot.controller.Controller;
import kit.route.a.lot.gui.event.GeneralEvent;
import kit.route.a.lot.gui.event.TextEvent;


public class SaveRouteListner implements GeneralListener {

    private Controller ctrl;
    
    public SaveRouteListner(Controller ctrl) {
        this.ctrl = ctrl;
    }

    @Override
    public void handleEvent(GeneralEvent event) {
        if(event instanceof TextEvent) {
            ctrl.saveRoute(((TextEvent) event).getText());
        }
    }
}
