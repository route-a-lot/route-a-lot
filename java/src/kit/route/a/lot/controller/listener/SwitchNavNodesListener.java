package kit.route.a.lot.controller.listener;

import kit.route.a.lot.controller.Controller;
import kit.route.a.lot.gui.event.GeneralEvent;
import kit.route.a.lot.gui.event.SwitchNavNodesEvent;


public class SwitchNavNodesListener implements GeneralListener {

    private Controller ctrl;
    
    
    public SwitchNavNodesListener(Controller ctrl) {
        this.ctrl = ctrl;
    }


    @Override
    public void handleEvent(GeneralEvent event) {
       if (event instanceof SwitchNavNodesEvent) {
           ctrl.switchNavNodes(((SwitchNavNodesEvent) event).getFirst(), ((SwitchNavNodesEvent) event).getSecond());
       }

    }

}
