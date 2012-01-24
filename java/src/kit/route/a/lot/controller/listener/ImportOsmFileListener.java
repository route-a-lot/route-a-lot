package kit.route.a.lot.controller.listener;

import java.io.File;

import kit.route.a.lot.controller.Controller;
import kit.route.a.lot.gui.event.GeneralEvent;
import kit.route.a.lot.gui.event.TextEvent;


public class ImportOsmFileListener implements GeneralListener {

    private Controller ctrl;
    
    public ImportOsmFileListener(Controller ctrl) {
        this.ctrl = ctrl;
    }
    
    @Override
    public void handleEvent(GeneralEvent event) {
        if (event instanceof TextEvent) {
            ctrl.importMap(new File(((TextEvent) event).getText()));
        }
    }

}
