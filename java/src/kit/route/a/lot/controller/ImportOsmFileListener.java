package kit.route.a.lot.controller;

import java.util.EventObject;

import kit.route.a.lot.gui.ImportOsmFileEvent;


public class ImportOsmFileListener implements RALListener {

    private Controller ctrl;
    
    public ImportOsmFileListener(Controller ctrl) {
        this.ctrl = ctrl;
    }
    
    @Override
    public void handleRALEvent(EventObject event) {
        if (event instanceof ImportOsmFileEvent) {
            ctrl.importMap(((ImportOsmFileEvent) event).getPath());
        }
    }

}
