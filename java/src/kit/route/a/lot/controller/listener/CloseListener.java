package kit.route.a.lot.controller.listener;

import kit.route.a.lot.controller.Controller;
import kit.route.a.lot.gui.event.GeneralEvent;

public class CloseListener implements GeneralListener {

    private Controller ctrl;
    
    
    public CloseListener(Controller ctrl) {
        this.ctrl = ctrl;
    }

    @Override
    public void handleEvent(GeneralEvent event) {
        ctrl.prepareForShutdown();
    }

}
