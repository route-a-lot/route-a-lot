package kit.route.a.lot.gui;

import java.util.ArrayList;
import java.util.EventObject;

import kit.route.a.lot.controller.listener.RALListener;

public class ListenerLists {

    public ArrayList<RALListener> targetSelected;
    public ArrayList<RALListener> viewChanged;
    public ArrayList<RALListener> importOsmFile;
    public ArrayList<RALListener> optimizeRoute;
    public ArrayList<RALListener> exportRoute;
    public ArrayList<RALListener> whatWasClicked;
    public ArrayList<RALListener> addFav;
    public ArrayList<RALListener> loadRoute;
    public ArrayList<RALListener> saveRoute;
    public ArrayList<RALListener> speed;
    public ArrayList<RALListener> close;
    public ArrayList<RALListener> heightMalus;
    public ArrayList<RALListener> highwayMalus;
    public ArrayList<RALListener> importHeightMap;
    public ArrayList<RALListener> printRoute;
    public ArrayList<RALListener> addTextuelNavPointList;
    public ArrayList<RALListener> deleteNavPointList;
    public ArrayList<RALListener> loadMapList;
    
    /**
     * Initializes all listener collections that are used to communicate with the controller.
     */
    public ListenerLists() {
        targetSelected = new ArrayList<RALListener>();
        viewChanged = new ArrayList<RALListener>();
        importOsmFile = new ArrayList<RALListener>();
        whatWasClicked = new ArrayList<RALListener>();
        addFav = new ArrayList<RALListener>();
        loadRoute = new ArrayList<RALListener>();
        saveRoute = new ArrayList<RALListener>();
        exportRoute = new ArrayList<RALListener>();
        speed = new ArrayList<RALListener>();
        close = new ArrayList<RALListener>();
        heightMalus = new ArrayList<RALListener>();
        highwayMalus = new ArrayList<RALListener>();
        importHeightMap = new ArrayList<RALListener>();
        printRoute = new ArrayList<RALListener>();
        addTextuelNavPointList = new ArrayList<RALListener>();
        deleteNavPointList = new ArrayList<RALListener>();
        loadMapList = new ArrayList<RALListener>();
        optimizeRoute = new ArrayList<RALListener>();
    }
    
    public void addViewChangedListener(RALListener viewChangedListener) {
        viewChanged.add(viewChangedListener);
    }
    public void addTargetSelectedListener(RALListener targetSelectedListener) {
        targetSelected.add(targetSelectedListener);
    }
    public void addImportOsmFileListener(RALListener importOsmFileListener) {
        importOsmFile.add(importOsmFileListener);
    }    
    public void addLoadRouteListener(RALListener loadRouteListener) {
        loadRoute.add(loadRouteListener);
    }  
    public void addSaveRouteListener(RALListener saveRouteListener) {
        saveRoute.add(saveRouteListener);
    }    
    public void addExportRouteListener(RALListener exportRouteListener) {
        exportRoute.add(exportRouteListener);
    }    
    public void addOptimizeRouteListener(RALListener optimizeRouteListener) {
        optimizeRoute.add(optimizeRouteListener);
    }    
    public void addWhatWasClickedListener(RALListener whatWasClickedListener) {
        whatWasClicked.add(whatWasClickedListener);
    }   
    public void addFavoriteListener(RALListener addFavListener) {
        addFav.add(addFavListener);
    }    
    public void addSetSpeedListener(RALListener setSpeedListener) {
        speed.add(setSpeedListener);
    }    
    public void addCloseListener(RALListener closeListener) {
        close.add(closeListener);
    }    
    public void addHighwayMalusListener(RALListener highwayMalusListener) {
        highwayMalus.add(highwayMalusListener);
    }    
    public void addHeightMalusListener(RALListener heightMalusListener) {
        heightMalus.add(heightMalusListener);
    }    
    public void addImportHeightMapListener(RALListener importHeightMapListener) {
        importHeightMap.add(importHeightMapListener);
    }    
    public void addPrintRouteListener(RALListener printRouteListener) {
        printRoute.add(printRouteListener);
        //TODO
    }
    public void addDeleteNavNodeListener(RALListener deleteNavNodeListener) {
        deleteNavPointList.add(deleteNavNodeListener);
    }
    public void addTextuelNavPointListener(RALListener textuelNavPointListener) {
        addTextuelNavPointList.add(textuelNavPointListener);
    }
    public void addLoadMapListener(RALListener loadMapListener) {
        loadMapList.add(loadMapListener);
    }
    
    public static void fireEvent(ArrayList<RALListener> listener, EventObject event) {
        for (RALListener lst: listener) {
            lst.handleRALEvent(event);
        }  
    }
    
}
