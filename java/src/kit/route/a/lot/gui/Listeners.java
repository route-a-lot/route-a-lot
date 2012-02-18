package kit.route.a.lot.gui;

import java.util.ArrayList;
import java.util.List;

import kit.route.a.lot.common.Listener;
import kit.route.a.lot.gui.event.Event;

public class Listeners {
          
    private List<Listener>[] lists;
    
    /**
     * Initializes all listener collections that are used to communicate with the controller.
     */
    @SuppressWarnings("unchecked")
    public Listeners(int numberOfLists) {  
        lists = (List<Listener>[]) new ArrayList[numberOfLists];
        for (int i = 0; i < lists.length; i++) {
            lists[i] = new ArrayList<Listener>();
        }
    }
    
    public void addListener(int eventType, Listener listener) {
        lists[eventType].add(listener);
    }
    
    public void fireEvent(int eventType, Event event) {
        for (Listener listener: lists[eventType]) {
            listener.handleEvent(event);
        }  
    }
    
}
