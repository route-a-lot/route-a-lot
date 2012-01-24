package kit.route.a.lot.controller.listener;

import kit.route.a.lot.controller.Controller;
import kit.route.a.lot.gui.event.GeneralEvent;


public class OrderNavNodesListener implements GeneralListener {

    Controller ctrl;
    
    public OrderNavNodesListener(Controller ctrl) {
        this.ctrl = ctrl;
    }

    @Override
    public void handleEvent(GeneralEvent event) {
        ctrl.orderNavNodes();

    }

}
