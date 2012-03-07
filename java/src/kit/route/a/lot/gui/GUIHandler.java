package kit.route.a.lot.gui;

import java.util.List;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.POIDescription;
import kit.route.a.lot.common.RouteDescription;
import kit.route.a.lot.common.Selection;


public class GUIHandler {

    private GUI gui;
    
    public GUIHandler() {
        gui = new GUI();
        gui.setBounds(0, 25, 600, 600);
        gui.addContents();
    }
    
    public void updateGUI() {
        gui.updateGUI();
    }

    public void updateMapList(List<String> maps, int activeMapIndex) {
        gui.setImportedMapsList(maps, activeMapIndex);
    }
    
    public void updateNavNodes(List<Selection> navPointsList) {
        gui.updateNavNodes(navPointsList);
    }
    
    public void passElementType(int element){
        gui.passElementType(element);
    }
    
    public void setView(Coordinates center, int detailLevel) {
        gui.setView(center, detailLevel);
    }
    
    public void setSpeed(int speed) {   
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
    
    public void showSearchCompletion(List<String> completion, int iconNum) {
        gui.showSearchCompletions(completion, iconNum);
    }
    
    public void setMapMode(boolean render3D) {
        gui.setMapMode(render3D);
    }
}
