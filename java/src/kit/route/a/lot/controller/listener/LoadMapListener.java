package kit.route.a.lot.controller.listener;

import java.io.File;

import kit.route.a.lot.controller.Controller;
import kit.route.a.lot.gui.event.GeneralEvent;
import kit.route.a.lot.gui.event.TextEvent;


public class LoadMapListener implements GeneralListener {
    
    private Controller ctrl;
    
    public LoadMapListener(Controller ctrl) {
        this.ctrl = ctrl;
    }

    @Override
    public void handleEvent(GeneralEvent event) {
        if (event instanceof TextEvent) {
            System.err.println(((TextEvent) event).getText());
            ctrl.loadMap(new File(((TextEvent) event).getText()));
        }

    }

}
