package kit.route.a.lot.controller.listener;

import java.util.EventObject;

import kit.route.a.lot.controller.Controller;
import kit.route.a.lot.gui.event.PositionEvent;


public class WhatWasClickedListener implements RALListener {

    private Controller ctrl;
    
    
    public WhatWasClickedListener(Controller ctrl) {
        this.ctrl = ctrl;
    }


    @Override
    public void handleRALEvent(EventObject event) {
        if (event instanceof PositionEvent) {
            ctrl.whatWasClicked(((PositionEvent) event).getCoordinates());
        }
    }

}
