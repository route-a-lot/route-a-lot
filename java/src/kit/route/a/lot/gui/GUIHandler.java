package kit.route.a.lot.gui;

import kit.route.a.lot.common.Context;
import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.controller.RALListener;

public class GUIHandler {

    private GUI gui;
    
    public GUIHandler() {
        createGUI();
    }
    public GUIHandler(Coordinates topLeft, Coordinates bottomRight) {
        createGUI(topLeft, bottomRight);
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
    public void createGUI(Coordinates topLeft, Coordinates bottomRight) {
        gui = new GUI(topLeft, bottomRight);
        gui.setBounds(0, 25, 500, 500);
        System.out.println(gui.getWidth());
        System.out.println(gui.getHeight());
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
    public void updateMapList() {
    }

    /**
     * Operation addListenerSetView
     * 
     * @return
     */
    public void addListenerSetView() {
    }

    /**
     * Operation addListenerSetZoomlevel
     * 
     * @return
     */
    public void addListenerSetZoomlevel() {
    }

    /**
     * Operation addListenerToggle3D
     * 
     * @return
     */
    public void addListenerToggle3D() {
    }

    /**
     * Operation addListenerLoadMap
     * 
     * @return
     */
    public void addListenerLoadMap() {
    }

    /**
     * Operation addListenerImportMap
     * 
     * @return
     */
    public void addListenerImportMap() {
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
    
    /**
     * Operation addListenerDeleteNavNode
     * 
     * @return
     */
    public void addListenerDeleteNavNode() {
    }

    /**
     * Operation addListenerSwitchNavNodes
     * 
     * @return
     */
    public void addListenerSwitchNavNodes() {
    }

    /**
     * Operation addListenerOrderNavNodes
     * 
     * @return
     */
    public void addListenerOrderNavNodes() {
    }

    /**
     * Operation addListenerAddFavorite
     * 
     * @return
     */
    public void addListenerAddFavorite() {
    }

    /**
     * Operation addListenerDeleteFavorite
     * 
     * @return
     */
    public void addListenerDeleteFavorite() {
    }

    /**
     * Operation addListenerSaveRoute
     * 
     * @return
     */
    public void addListenerSaveRoute() {
    }

    /**
     * Operation addListenerLoadRoute
     * 
     * @return
     */
    public void addListenerLoadRoute() {
    }

    /**
     * Operation addListenerExportRoute
     * 
     * @return
     */
    public void addListenerExportRoute() {
    }

    /**
     * Operation addListenerPrintRoute
     * 
     * @return
     */
    public void addListenerPrintRoute() {
    }

    /**
     * Operation addListenerTypeAddress
     * 
     * @return
     */
    public void addListenerTypeAddress() {
    }

    /**
     * Operation addListenerSearchAddress
     * 
     * @return
     */
    public void addListenerSearchAddress() {
    }

    /**
     * Operation addListenerSearchPOI
     * 
     * @return
     */
    public void addListenerSearchPOI() {
    }

    /**
     * Operation addListenerSearchFavorite
     * 
     * @return
     */
    public void addListenerSearchFavorite() {
    }

    /**
     * Operation addListenerSetSpeed
     * 
     * @return
     */
    public void addListenerSetSpeed() {
    }

    /**
     * Operation addListenerGetPOIInfo
     * 
     * @return
     */
    public void addListenerGetPOIInfo() {
    }

    /**
     * Operation addListenerShowTextRoute
     * 
     * @return
     */
    public void addListenerShowTextRoute() {
    }

    /**
     * Operation addListenerSetHeightMalus
     * 
     * @return
     */
    public void addListenerSetHeightMalus() {
    }

    /**
     * Operation addListenerSetHighwayMalus
     * 
     * @return
     */
    public void addListenerSetHighwayMalus() {
    }

    /**
     * Operation addListenerImportHeightMap
     * 
     * @return
     */
    public void addListenerImportHeightMap() {
    }
}
