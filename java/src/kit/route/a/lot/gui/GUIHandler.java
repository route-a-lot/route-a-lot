package kit.route.a.lot.gui;

import java.util.ArrayList;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.POIDescription;
import kit.route.a.lot.common.RouteDescription;
import kit.route.a.lot.controller.listener.RALListener;


public class GUIHandler {

    private GUI gui;
    private ListenerLists listener = new ListenerLists();
    
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

    /**
     * Operation addListenerImportMap
     * 
     * @return
     */
    public void addListenerImportMap(RALListener importOsmFileListener) {
        listener.addImportOsmFileListener(importOsmFileListener);
    }

    /**
     * Operation addListenerAddNavNode
     * 
     * @return
     */
    public void addListenerAddNavNode(RALListener targetSelectedListener) {
        listener.addTargetSelectedListener(targetSelectedListener);
    }

    public void addChangedViewListener(RALListener viewChangedListener) {
        listener.addViewChangedListener(viewChangedListener);
    }
    
    public void addOptimizeRouteListener(RALListener optimizeRouteListener) {
        listener.addOptimizeRouteListener(optimizeRouteListener);
    }
    
    public void setNavNodesOrder(ArrayList<Coordinates> orderedList) {
        gui.setNavPointsOrdered(orderedList);
    }
    
    public void addLoadMapListener(RALListener loadMapListener) {
        listener.addLoadMapListener(loadMapListener);
    }
    
    public void addDeleteNavNodeListener(RALListener deleteNavNodeListener) {
        listener.addDeleteNavNodeListener(deleteNavNodeListener);
    }
    
    public void deleteNavNodeFromList(Coordinates coordinates) {
        gui.deleteNavNodesFromList(coordinates);
    }
    
    public void addSwitchNavNodesListener (RALListener switchNavNodesListener) {
        //TODO
    }
    
    public void addAddFavoriteListener(RALListener addFavListener) {
        listener.addFavoriteListener(addFavListener);
    }
    
    public void addDeleteFavListener (RALListener deleteFavListener) {
        //TODO
    }
    
    public void addSaveRouteListener(RALListener saveRouteListener) {
        listener.addSaveRouteListener(saveRouteListener);
    }
    
    public void addLoadRouteListener(RALListener loadRouteListener) {
        listener.addLoadRouteListener(loadRouteListener);
    }
    
    public void addExportRouteListener(RALListener exportRouteListener) {
        listener.addExportRouteListener(exportRouteListener);
    }
    
    public void printRouteListener(RALListener printRouteListener) {
        listener.addPrintRouteListener(printRouteListener);
        //TODO
    }
    
    public void addSetSpeedListener(RALListener setSpeedListener) {
        listener.addSetSpeedListener(setSpeedListener);
    }
    
    public void setSpeed(int speed) {
        gui.setSpeed(speed);
    }
    
    public void addWhatWasClickedListener(RALListener whatWasClickedListener) {
        listener.addWhatWasClickedListener(whatWasClickedListener);
    }
    
    public void thisWasClicked(int element, Coordinates pos){
        //TODO tells the gui what was clicked (element will be a constant)
    }
    
    public void addGetPoiDescriptionListener(RALListener posDescriptionListener) {
        //TODO
    }
    
    public void showPoiDescription(POIDescription descr, Coordinates pos) {
        //TODO
    }
    
    public void setRouteText(RouteDescription routeDescr) {
        //TODO
    }
    
    public void addHighwayMalusListener(RALListener highwayMalusListener) {
        listener.addHighwayMalusListener(highwayMalusListener);
    }
    
    public void addHeightMalusListener(RALListener heightMalusListener) {
        listener.addHeightMalusListener(heightMalusListener);
    }
    
    public void addImportHeightMapListener(RALListener importHeightMapListener) {
        listener.addImportHeightMapListener(importHeightMapListener);
    }
    
    public void addCloseListener(RALListener closeListener) {
        listener.addCloseListener(closeListener);
    }
    
    public void setView(Coordinates topLeft) {
        gui.setView(topLeft);
    }
}
