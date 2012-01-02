package kit.route.a.lot.controller;

import java.util.EventObject;


public class TargetSelectedListener implements RALListener{
    private Controller ctrl;
    
    public TargetSelectedListener(Controller ctrl) {
        this.ctrl = ctrl;
    }

    @Override
    public void handleRALEvent(EventObject event) {
        if (event instanceof kit.route.a.lot.gui.NavNodeSelectedEvent) {
            //TODO we first have to decide who's getting the coordinates from clicks . . . 
        }
        
    }
    
    
}
