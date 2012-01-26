package kit.route.a.lot.gui;

import java.util.ArrayList;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.POIDescription;
import kit.route.a.lot.common.RouteDescription;
import kit.route.a.lot.controller.listener.GeneralListener;


public class GUIHandler {

    private GUI gui;
    private Listeners listener = new Listeners();
    
    public GUIHandler() {
        createGUI();
    }

    public void createGUI() {
        gui = new GUI(listener);
        gui.setBounds(0, 25, 600, 600);
        gui.addContents();
    }


    /**
     * Operation updateGUI
     * 
     * @return
     */
    public void updateGUI() {
        gui.updateGUI();
    }


    /**
     * Operation updateMapList
     * 
     * @return
     */
    public void updateMapList(ArrayList<String> maps) {
        gui.updateMapChooser(maps);
    }
    
    public void updateNavPointsList(ArrayList<Coordinates> navPointsList) {
        gui.updateNavNodes(navPointsList);
    }

    /**
     * Operation addListenerImportMap
     * 
     * @return
     */
    public void addListenerImportMap(GeneralListener importOsmFileListener) {
        listener.addImportOsmFileListener(importOsmFileListener);
    }

    /**
     * Operation addListenerAddNavNode
     * 
     * @return
     */
    public void addListenerAddNavNode(GeneralListener targetSelectedListener) {
        listener.addTargetSelectedListener(targetSelectedListener);
    }

    public void addChangedViewListener(GeneralListener viewChangedListener) {
        listener.addViewChangedListener(viewChangedListener);
    }
    
    public void addOptimizeRouteListener(GeneralListener optimizeRouteListener) {
        listener.addOptimizeRouteListener(optimizeRouteListener);
    }
    
    public void addLoadMapListener(GeneralListener loadMapListener) {
        listener.addLoadMapListener(loadMapListener);
    }
    
    public void addDeleteNavNodeListener(GeneralListener deleteNavNodeListener) {
        listener.addDeleteNavNodeListener(deleteNavNodeListener);
    }
    
    public void addSwitchNavNodesListener (GeneralListener switchNavNodesListener) {
        //TODO
    }
    
    public void addAddFavoriteListener(GeneralListener addFavListener) {
        listener.addFavoriteListener(addFavListener);
    }
    
    public void addDeleteFavListener (GeneralListener deleteFavListener) {
        listener.addDeleteFavListener(deleteFavListener);
    }
    
    public void addSaveRouteListener(GeneralListener saveRouteListener) {
        listener.addSaveRouteListener(saveRouteListener);
    }
    
    public void addLoadRouteListener(GeneralListener loadRouteListener) {
        listener.addLoadRouteListener(loadRouteListener);
    }
    
    public void addExportRouteListener(GeneralListener exportRouteListener) {
        listener.addExportRouteListener(exportRouteListener);
    }
    
    public void printRouteListener(GeneralListener printRouteListener) {
        listener.addPrintRouteListener(printRouteListener);
        //TODO
    }
    
    public void addSetSpeedListener(GeneralListener setSpeedListener) {
        listener.addSetSpeedListener(setSpeedListener);
    }
    
    public void addClickPositionListener(GeneralListener clickPositionListener) {
        listener.addClickPositionListener(clickPositionListener);
    }
    
    public void addGetPoiDescriptionListener(GeneralListener poiDescriptionListener) {
        listener.addPoiDescriptionListener(poiDescriptionListener);
    }
    
    public void addHighwayMalusListener(GeneralListener highwayMalusListener) {
        listener.addHighwayMalusListener(highwayMalusListener);
    }
    
    public void addHeightMalusListener(GeneralListener heightMalusListener) {
        listener.addHeightMalusListener(heightMalusListener);
    }
    
    public void addImportHeightMapListener(GeneralListener importHeightMapListener) {
        listener.addImportHeightMapListener(importHeightMapListener);
    }
    
    public void addCloseListener(GeneralListener closeListener) { //doesn't work ... obsolete comment?
        listener.addCloseListener(closeListener);
    }
    
    public void addSwitchMapModeListener(GeneralListener switchMapModeListener) {
        listener.addSwitchMapModeListener(switchMapModeListener);
    }
    
    public void setView(Coordinates center) {
        gui.setView(center);
    }
    
    public void setSpeed(int speed) {   //TODO change ist with duration
        gui.setSpeed(speed);
    }
    
    public void setRouteText(RouteDescription routeDescr) {
        //TODO
    }
    
    public void showPoiDescription(POIDescription descr, Coordinates pos) {
        //TODO
    }
    
    public void thisWasClicked(int element, Coordinates pos){
        gui.popUpTrigger(element, pos);
    }
    
    public void deleteNavNodeFromList(Coordinates coordinates) {
        gui.deleteNavNodesFromList(coordinates);
    }
    
    public void setNavNodesOrder(ArrayList<Coordinates> orderedList) {
        gui.setNavPointsOrdered(orderedList);
    }
}
