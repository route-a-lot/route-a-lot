package kit.route.a.lot.controller.listener;

import java.util.EventObject;

import kit.route.a.lot.controller.Controller;
import kit.route.a.lot.gui.event.PositionEvent;


public class GetPoiDescriptionListener implements RALListener {

    private Controller ctrl;
    
    
    
    public GetPoiDescriptionListener(Controller ctrl) {
        this.ctrl = ctrl;
    }



    @Override
    public void handleRALEvent(EventObject event) {
        if (event instanceof PositionEvent) {
            ctrl.getPOIInfo(((PositionEvent) event).getCoordinates());
        }
    }

}
