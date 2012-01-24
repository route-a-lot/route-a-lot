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
    
    public void setNavNodesOrder(ArrayList<Coordinates> orderedList) {
        gui.setNavPointsOrdered(orderedList);
    }
    
    public void addLoadMapListener(GeneralListener loadMapListener) {
        listener.addLoadMapListener(loadMapListener);
    }
    
    public void addDeleteNavNodeListener(GeneralListener deleteNavNodeListener) {
        listener.addDeleteNavNodeListener(deleteNavNodeListener);
    }
    
    public void deleteNavNodeFromList(Coordinates coordinates) {
        gui.deleteNavNodesFromList(coordinates);
    }
    
    public void addSwitchNavNodesListener (GeneralListener switchNavNodesListener) {
        //TODO
    }
    
    public void addAddFavoriteListener(GeneralListener addFavListener) {
        listener.addFavoriteListener(addFavListener);
    }
    
    public void addDeleteFavListener (GeneralListener deleteFavListener) {
        //TODO
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
    
    public void setSpeed(int speed) {
        gui.setSpeed(speed);
    }
    
    public void addWhatWasClickedListener(GeneralListener whatWasClickedListener) {
        listener.addWhatWasClickedListener(whatWasClickedListener);
    }
    
    public void thisWasClicked(int element, Coordinates pos){
        //TODO tells the gui what was clicked (element will be a constant)
    }
    
    public void addGetPoiDescriptionListener(GeneralListener posDescriptionListener) {
        //TODO
    }
    
    public void showPoiDescription(POIDescription descr, Coordinates pos) {
        //TODO
    }
    
    public void setRouteText(RouteDescription routeDescr) {
        //TODO
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
    
    public void addCloseListener(GeneralListener closeListener) {
        listener.addCloseListener(closeListener);
    }
    
    public void setView(Coordinates topLeft) {
        gui.setView(topLeft);
    }
}
