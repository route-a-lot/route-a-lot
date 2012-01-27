package kit.route.a.lot.controller.listener;

import kit.route.a.lot.controller.Controller;
import kit.route.a.lot.gui.event.GeneralEvent;
import kit.route.a.lot.gui.event.TextEvent;


public class SearchNameListener implements GeneralListener {

    private Controller ctrl;
    
    
    
    public SearchNameListener(Controller ctrl) {
        super();
        this.ctrl = ctrl;
    }



    @Override
    public void handleEvent(GeneralEvent event) {
        if (event instanceof TextEvent) {
            ctrl.getNavNodeFromText(((TextEvent) event).getText());
        }

    }

}
