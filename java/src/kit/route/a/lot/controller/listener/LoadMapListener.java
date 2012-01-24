package kit.route.a.lot.controller.listener;

import java.io.File;
import java.util.EventObject;

import kit.route.a.lot.controller.Controller;
import kit.route.a.lot.gui.event.PathEvent;


public class LoadMapListener implements RALListener {
    
    private Controller ctrl;
    
    public LoadMapListener(Controller ctrl) {
        this.ctrl = ctrl;
    }

    @Override
    public void handleRALEvent(EventObject event) {
        if (event instanceof PathEvent) {
            System.err.println(((PathEvent) event).getPath());
            ctrl.loadMap(new File(((PathEvent) event).getPath()));
        }

    }

}
