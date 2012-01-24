package kit.route.a.lot.controller.listener;

import kit.route.a.lot.controller.Controller;
import kit.route.a.lot.gui.event.ChangeViewEvent;
import kit.route.a.lot.gui.event.GeneralEvent;


public class ViewChangedListener implements GeneralListener {

    private Controller ctrl;
    
    public ViewChangedListener(Controller ctrl) {
        this.ctrl = ctrl;
    }
    
    @Override
    public void handleEvent(GeneralEvent event) {
        if(event instanceof ChangeViewEvent) {
            ctrl.render(((ChangeViewEvent) event).getContext(), ((ChangeViewEvent) event).getZoomlevel());
        }
    }

}
