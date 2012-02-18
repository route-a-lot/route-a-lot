package kit.route.a.lot.gui;

import java.util.List;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Listener;
import kit.route.a.lot.common.POIDescription;
import kit.route.a.lot.common.RouteDescription;
import kit.route.a.lot.common.Selection;


public class GUIHandler {

    private GUI gui;
    private Listeners listeners = new Listeners(Listener.TYPE_COUNT);
    
    public GUIHandler() {
        gui = new GUI(listeners);
        gui.setBounds(0, 25, 600, 600);
        gui.addContents();
    }

    public void setActive(boolean active) {
        gui.setActive(true);
    }
    
    public void updateGUI() {
        gui.updateGUI();
    }

    public void updateMapList(List<String> maps) {
        gui.updateMapChooser(maps);
    }
    
    public void updateNavPointsList(List<Selection> navPointsList) {
        gui.updateNavNodes(navPointsList);
    }
    
    public void passElementType(int element){
        gui.passElementType(element);
    }
    
    
    public void setView(Coordinates center) {
        gui.setView(center);
    }
    
    public void setSpeed(int speed) {   //TODO change ist with duration
        gui.setSpeed(speed);
    }
    
    public void setRouteDecription(RouteDescription description) {
        //TODO
    }
    
    public void passDescription(POIDescription description) {
        gui.passDescription(description);
    }
      
    public void showRouteValues(int length, int duration) {
        gui.showRouteValues(length, duration);
    }
    
    public void showSearchCompletion(List<String> completion) {
        gui.showSearchCompletions(completion);
    }
    
    public void addListener(int eventType, Listener listener) {
        listeners.addListener(eventType, listener);
    }
}
