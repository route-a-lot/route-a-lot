package kit.route.a.lot.gui;

import kit.route.a.lot.common.Context;
import kit.route.a.lot.common.Coordinates;
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
     * Operation getContext
     * 
     * @return Context
     */
    public Context getContext() {
        return null;
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
     * Operation updateMap
     * 
     * @return
     */
    public void updateMap() {
    }

    /**
     * Operation updateNavNodes
     * 
     * @return
     */
    public void updateNavNodes() {
    }

    /**
     * Operation updateFavorite
     * 
     * @return
     */
    public void updateFavorite() {
    }

    /**
     * Operation updateRoute
     * 
     * @return
     */
    public void updateRoute() {
    }

    /**
     * Operation updateSearch
     * 
     * @return
     */
    public void updateSearch() {
    }

    /**
     * Operation updateDuration
     * 
     * @return
     */
    public void updateDuration() {
    }

    /**
     * Operation updateText
     * 
     * @return
     */
    public void updateText() {
    }

    /**
     * Operation updateMalus
     * 
     * @return
     */
    public void updateMalus() {
    }

    /**
     * Operation updateMapList
     * 
     * @return
     */
    public void updateMapList(String[] maps) {
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
    
    public void setView(Coordinates topLeft) {
        gui.setView(topLeft);
    }
}
