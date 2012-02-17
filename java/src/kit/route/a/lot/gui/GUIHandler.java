package kit.route.a.lot.gui;

import java.util.ArrayList;
import java.util.List;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.POIDescription;
import kit.route.a.lot.common.RouteDescription;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.controller.listener.GeneralListener;


public class GUIHandler {

    private GUI gui;
    private Listeners listeners = new Listeners();
    
    public GUIHandler() {
        gui = new GUI(listeners);
        gui.setBounds(0, 25, 600, 600);
        gui.addContents();
    }

    public void updateGUI() {
        gui.updateGUI();
    }

    public void updateMapList(ArrayList<String> maps) {
        gui.updateMapChooser(maps);
    }
    
    public void updateNavPointsList(List<Selection> navPointsList) {
        gui.updateNavNodes(navPointsList);
    }
    
    public void passElementType(int element){
        gui.popUpTrigger(element);
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
    
    public void showNavNodeDescription(String navNodeDescription, int navNodeIndex) {
        gui.showNavNodeDescription(navNodeDescription, navNodeIndex);
    }
    
    public void showRouteValues(int duration, int length) {
        gui.showRouteValues(duration, length);
    }
    
    public void showSearchCompletion(List<String> completition) {
        gui.showSearchCompletion(completition);
    }
    
    
    // LISTENER RECEPTION //

    public void addImportMapListener(GeneralListener listener) {
        listeners.addImportOsmFileListener(listener);
    }

    public void addAddNavNodeListener(GeneralListener listener) {
        listeners.addTargetSelectedListener(listener);
    }

    public void addChangedViewListener(GeneralListener listener) {
        listeners.addViewChangedListener(listener);
    }
    
    public void addOptimizeRouteListener(GeneralListener listener) {
        listeners.addOptimizeRouteListener(listener);
    }
    
    public void addLoadMapListener(GeneralListener listener) {
        listeners.addLoadMapListener(listener);
    }
    
    public void addDeleteNavNodeListener(GeneralListener listener) {
        listeners.addDeleteNavNodeListener(listener);
    }
    
    public void addSwitchNavNodesListener (GeneralListener listener) {
        //listeners.addSwitchNavNodesListener(listener); //TODO
    }
    
    public void addAddFavoriteListener(GeneralListener listener) {
        listeners.addFavoriteListener(listener);
    }
    
    public void addDeleteFavListener (GeneralListener listener) {
        listeners.addDeleteFavListener(listener);
    }
    
    public void addSaveRouteListener(GeneralListener listener) {
        listeners.addSaveRouteListener(listener);
    }
    
    public void addLoadRouteListener(GeneralListener listener) {
        listeners.addLoadRouteListener(listener);
    }
    
    public void addExportRouteListener(GeneralListener listener) {
        listeners.addExportRouteListener(listener);
    }
    
    public void addSetSpeedListener(GeneralListener listener) {
        listeners.addSetSpeedListener(listener);
    }
    
    public void addClickPositionListener(GeneralListener listener) {
        listeners.addClickPositionListener(listener);
    }
    
    public void addGetPoiDescriptionListener(GeneralListener listener) {
        listeners.addPoiDescriptionListener(listener);
    }
    
    public void addHighwayMalusListener(GeneralListener listener) {
        listeners.addHighwayMalusListener(listener);
    }
    
    public void addHeightMalusListener(GeneralListener listener) {
        listeners.addHeightMalusListener(listener);
    }
    
    public void addImportHeightMapListener(GeneralListener listener) {
        listeners.addImportHeightMapListener(listener);
    }
    
    public void addCloseListener(GeneralListener listener) {
        listeners.addCloseListener(listener);
    }
    
    public void addSwitchMapModeListener(GeneralListener listener) {
        listeners.addSwitchMapModeListener(listener);
    }
    
    public void addGetNavNodeDescriptionListener(GeneralListener listener) {
        listeners.addGetNavNodeDescriptionListener(listener);
    }
    
    public void addAutoCompletionListener(GeneralListener listener) {
        listeners.addAutoCompletionListener(listener);
    }
    
    public void addDeleteMapListener(GeneralListener listener) {
        listeners.addDeleteMapListener(listener);
    }
    
    public void addFavDescriptionListener(GeneralListener listener) {
        listeners.addFavDescriptionListener(listener);
    }
}
