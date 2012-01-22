package kit.route.a.lot.controller.listener;

import java.util.EventObject;

import kit.route.a.lot.controller.Controller;


public class OrderNavNodesEvent implements RALListener {

    Controller ctrl;
    
    public OrderNavNodesEvent(Controller ctrl) {
        this.ctrl = ctrl;
    }

    @Override
    public void handleRALEvent(EventObject event) {
        ctrl.orderNavNodes();

    }

}
