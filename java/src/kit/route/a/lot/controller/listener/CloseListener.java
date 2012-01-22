package kit.route.a.lot.controller.listener;

import java.util.EventObject;

import kit.route.a.lot.controller.Controller;


public class CloseListener implements RALListener {

    private Controller ctrl;
    
    
    public CloseListener(Controller ctrl) {
        this.ctrl = ctrl;
    }


    @Override
    public void handleRALEvent(EventObject event) {
        ctrl.prepareForShutDown();
    }

}
