package kit.route.a.lot.gui;

import java.util.ArrayList;

import kit.route.a.lot.common.Context;
import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.POIDescription;
import kit.route.a.lot.common.RouteDescription;
import kit.route.a.lot.controller.listener.RALListener;


public class GUIHandler {

    private GUI gui;
    
    public GUIHandler() {
    }
    public GUIHandler(Coordinates middle) {
        createGUI(middle);
    }

    public void createGUI() {
        gui = new GUI();
        gui.setBounds(0, 25, 500, 500);
        gui.setVisible(true);
        gui.addContents();
    }
    
    /**
     * Operation createGUI
     * 
     * @return
     */
    public void createGUI(Coordinates middle) {
        gui = new GUI(middle);
        gui.setBounds(0, 25, 500, 500);
        gui.setVisible(true);
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
        gui.addImportOsmFileListener(importOsmFileListener);
    }

    /**
     * Operation addListenerAddNavNode
     * 
     * @return
     */
    public void addListenerAddNavNode(RALListener targetSelectedListener) {
        gui.addTargetSelectedListener(targetSelectedListener);
    }

    public void addChangedViewListener(RALListener viewChangedListener) {
        gui.addViewChangedListener(viewChangedListener);
    }
    
    public void addOptimizeRouteListener(RALListener optimizeRouteListener) {
        gui.addOptimizeRouteListener(optimizeRouteListener);
    }
    
    public void setNavNodesOrder(ArrayList<Coordinates> orderedList) {
        gui.setNavPointsOrdered(orderedList);
    }
    
    public void addLoadMapListener(RALListener loadMapListener) {
        //TODO
    }
    
    public void addDeleteNavNodeListener(RALListener deleteNavNodeListener) {
        //TODO
    }
    
    public void deleteNavNodeFromList(Coordinates coordinates) {
        gui.deleteNavNodesFromList(coordinates);
    }
    
    public void addSwitchNavNodesListener (RALListener switchNavNodesListener) {
        //TODO
    }
    
    public void addAddFavoriteListener(RALListener addFavListener) {
        gui.addFavoriteListener(addFavListener);
    }
    
    public void addDeleteFavListener (RALListener deleteFavListener) {
        //TODO
    }
    
    public void addSaveRouteListener(RALListener saveRouteListener) {
        gui.addSaveRouteListener(saveRouteListener);
    }
    
    public void addLoadRouteListener(RALListener loadRouteListener) {
        gui.addLoadRouteListener(loadRouteListener);
    }
    
    public void addExportRouteListener(RALListener exportRouteListener) {
        gui.addExportRouteListener(exportRouteListener);
    }
    
    public void printRouteListener(RALListener printRouteListener) {
        gui.addPrintRouteListener(printRouteListener);
        //TODO
    }
    
    public void addSetSpeedListener(RALListener setSpeedListener) {
        gui.addSetSpeedListener(setSpeedListener);
    }
    
    public void setSpeed(int speed) {
        gui.setSpeed(speed);
    }
    
    public void addWhatWasClickedListener(RALListener whatWasClickedListener) {
        gui.addWhatWasClickedListener(whatWasClickedListener);
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
        gui.addHighwayMalusListener(highwayMalusListener);
    }
    
    public void addHeightMalusListener(RALListener heightMalusListener) {
        gui.addHeightMalusListener(heightMalusListener);
    }
    
    public void addImportHeightMapListener(RALListener importHeightMapListener) {
        gui.addImportHeightMapListener(importHeightMapListener);
    }
    
    public void addCloseListener(RALListener closeListener) {
        gui.addCloseListener(closeListener);
    }
    
    public void setView(Coordinates topLeft) {
        gui.setView(topLeft);
    }
}
