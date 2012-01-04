package kit.route.a.lot.controller;

import java.util.EventObject;

import kit.route.a.lot.gui.ViewChangedEvent;
import kit.route.a.lot.common.Context;


public class ViewChangedListener implements RALListener {

    private Controller ctrl;
    
    public ViewChangedListener(Controller ctrl) {
        this.ctrl = ctrl;
    }
    
    @Override
    public void handleRALEvent(EventObject event) {
        if(event instanceof ViewChangedEvent) {
            ctrl.render(((ViewChangedEvent) event).getContext());
        }

    }

}