package kit.route.a.lot.controller;

import kit.route.a.lot.gui.event.GeneralEvent;

/**
 * Interface for our own Listener
 * @author krauss
 *
 */
public interface Listener {
    
    public void handleEvent(GeneralEvent event);
    
}
