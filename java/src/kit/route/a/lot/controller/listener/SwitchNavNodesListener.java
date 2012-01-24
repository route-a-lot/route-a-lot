package kit.route.a.lot.controller.listener;

import java.util.EventObject;

import kit.route.a.lot.controller.Controller;
import kit.route.a.lot.gui.event.NavNodesSwitchedEvent;


public class SwitchNavNodesListener implements RALListener {

    private Controller ctrl;
    
    
    public SwitchNavNodesListener(Controller ctrl) {
        this.ctrl = ctrl;
    }


    @Override
    public void handleRALEvent(EventObject event) {
       if (event instanceof NavNodesSwitchedEvent) {
           ctrl.switchNavNodes(((NavNodesSwitchedEvent) event).getFirst(), ((NavNodesSwitchedEvent) event).getSecond());
       }

    }

}
