package kit.route.a.lot.controller.listener;

import kit.route.a.lot.controller.Controller;
import kit.route.a.lot.gui.event.GeneralEvent;
import kit.route.a.lot.gui.event.PositionEvent;


public class GetPoiDescriptionListener implements GeneralListener {

    private Controller ctrl;
        
    public GetPoiDescriptionListener(Controller ctrl) {
        this.ctrl = ctrl;
    }

    @Override
    public void handleEvent(GeneralEvent event) {
        if (event instanceof PositionEvent) {
            ctrl.passPOIDescription(((PositionEvent) event).getCoordinates());
        }
    }

}
