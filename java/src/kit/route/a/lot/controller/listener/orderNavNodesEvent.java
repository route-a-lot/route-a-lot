package kit.route.a.lot.controller.listener;

import java.util.EventObject;

import kit.route.a.lot.controller.Controller;


public class orderNavNodesEvent implements RALListener {

    Controller ctrl;
    
    public orderNavNodesEvent(Controller ctrl) {
        this.ctrl = ctrl;
    }

    @Override
    public void handleRALEvent(EventObject event) {
        ctrl.orderNavNodes();

    }

}
