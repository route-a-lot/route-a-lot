package kit.route.a.lot.controller.listener;

import kit.route.a.lot.controller.Controller;
import kit.route.a.lot.gui.event.GeneralEvent;
import kit.route.a.lot.gui.event.TextEvent;


public class SuggestionListener implements GeneralListener {

    private Controller ctrl;
    
    
    
    public SuggestionListener(Controller ctrl) {
        this.ctrl = ctrl;
    }



    @Override
    public void handleEvent(GeneralEvent event) {
        if (event instanceof TextEvent) {
            ctrl.getCompletition(((TextEvent) event).getText());
        }
    }

}
