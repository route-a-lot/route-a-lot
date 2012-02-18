package kit.route.a.lot.controller.listener;

import kit.route.a.lot.controller.Controller;
import kit.route.a.lot.gui.event.GeneralEvent;
import kit.route.a.lot.gui.event.PositionEvent;


public class ShowPOIDescriptionListener implements GeneralListener {

    private Controller ctrl;
        
    public ShowPOIDescriptionListener(Controller ctrl) {
        this.ctrl = ctrl;
    }

    @Override
    public void handleEvent(GeneralEvent event) {
        ctrl.passDescription(((PositionEvent) event).getCoordinates());
    }

}
