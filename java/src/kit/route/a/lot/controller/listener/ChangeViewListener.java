package kit.route.a.lot.controller.listener;

import kit.route.a.lot.controller.Controller;
import kit.route.a.lot.gui.event.ChangeViewEvent;
import kit.route.a.lot.gui.event.GeneralEvent;


public class ChangeViewListener implements GeneralListener {

    private Controller ctrl;
    
    public ChangeViewListener(Controller ctrl) {
        this.ctrl = ctrl;
    }
    
    @Override
    public void handleEvent(GeneralEvent event) {
        if(event instanceof ChangeViewEvent) {
            ctrl.render(((ChangeViewEvent) event).getContext());
        }
    }

}
