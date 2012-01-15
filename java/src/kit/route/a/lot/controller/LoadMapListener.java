package kit.route.a.lot.controller;

import java.util.EventObject;

import kit.route.a.lot.gui.PathEvent;


public class LoadMapListener implements RALListener {
    
    private Controller ctrl;
    
    public LoadMapListener(Controller ctrl) {
        this.ctrl = ctrl;
    }

    @Override
    public void handleRALEvent(EventObject event) {
        if (event instanceof PathEvent) {
            ctrl.loadMap(((PathEvent) event).getPath());
        }

    }

}
