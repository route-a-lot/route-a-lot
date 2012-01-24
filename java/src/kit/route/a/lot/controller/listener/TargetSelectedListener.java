package kit.route.a.lot.controller.listener;

import java.util.EventObject;

import kit.route.a.lot.controller.Controller;
import kit.route.a.lot.gui.event.NavNodeSelectedEvent;


public class TargetSelectedListener implements RALListener{
    private Controller ctrl;
    
    public TargetSelectedListener(Controller ctrl) {
        this.ctrl = ctrl;
    }

    @Override
    public void handleRALEvent(EventObject event) {
        if (event instanceof NavNodeSelectedEvent) {
            ctrl.addNavNode(((NavNodeSelectedEvent) event).getCoordinates(), ((NavNodeSelectedEvent) event).getposition(),
                    ((NavNodeSelectedEvent) event).getContext());
        }
    }
}
