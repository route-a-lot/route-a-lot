package kit.route.a.lot.controller.listener;

import kit.route.a.lot.controller.Controller;
import kit.route.a.lot.gui.event.GeneralEvent;
import kit.route.a.lot.gui.event.SelectNavNodeEvent;


public class SelectNavNodeListener implements GeneralListener{
    private Controller ctrl;
    
    public SelectNavNodeListener(Controller ctrl) {
        this.ctrl = ctrl;
    }

    @Override
    public void handleEvent(GeneralEvent event) {
        if (event instanceof SelectNavNodeEvent) {
            ctrl.addNavNode(((SelectNavNodeEvent) event).getPosition(), ((SelectNavNodeEvent) event).getIndex());
        }
    }
}
