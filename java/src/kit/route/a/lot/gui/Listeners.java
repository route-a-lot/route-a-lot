package kit.route.a.lot.gui;

import java.util.ArrayList;
import java.util.List;

import kit.route.a.lot.controller.listener.GeneralListener;
import kit.route.a.lot.gui.event.GeneralEvent;

public class Listeners {

    public List<GeneralListener> targetSelected;
    public List<GeneralListener> viewChanged;
    public List<GeneralListener> importOsmFile;
    public List<GeneralListener> optimizeRoute;
    public List<GeneralListener> exportRoute;
    public List<GeneralListener> whatWasClicked;
    public List<GeneralListener> addFav;
    public List<GeneralListener> loadRoute;
    public List<GeneralListener> saveRoute;
    public List<GeneralListener> speed;
    public List<GeneralListener> close;
    public List<GeneralListener> heightMalus;
    public List<GeneralListener> highwayMalus;
    public List<GeneralListener> importHeightMap;
    public List<GeneralListener> printRoute;
    public List<GeneralListener> addTextuelNavPointList;
    public List<GeneralListener> deleteNavPointList;
    public List<GeneralListener> loadMapList;
    
    /**
     * Initializes all listener collections that are used to communicate with the controller.
     */
    public Listeners() {
        targetSelected = new ArrayList<GeneralListener>();
        viewChanged = new ArrayList<GeneralListener>();
        importOsmFile = new ArrayList<GeneralListener>();
        whatWasClicked = new ArrayList<GeneralListener>();
        addFav = new ArrayList<GeneralListener>();
        loadRoute = new ArrayList<GeneralListener>();
        saveRoute = new ArrayList<GeneralListener>();
        exportRoute = new ArrayList<GeneralListener>();
        speed = new ArrayList<GeneralListener>();
        close = new ArrayList<GeneralListener>();
        heightMalus = new ArrayList<GeneralListener>();
        highwayMalus = new ArrayList<GeneralListener>();
        importHeightMap = new ArrayList<GeneralListener>();
        printRoute = new ArrayList<GeneralListener>();
        addTextuelNavPointList = new ArrayList<GeneralListener>();
        deleteNavPointList = new ArrayList<GeneralListener>();
        loadMapList = new ArrayList<GeneralListener>();
        optimizeRoute = new ArrayList<GeneralListener>();
    }
    
    public void addViewChangedListener(GeneralListener viewChangedListener) {
        viewChanged.add(viewChangedListener);
    }
    public void addTargetSelectedListener(GeneralListener targetSelectedListener) {
        targetSelected.add(targetSelectedListener);
    }
    public void addImportOsmFileListener(GeneralListener importOsmFileListener) {
        importOsmFile.add(importOsmFileListener);
    }    
    public void addLoadRouteListener(GeneralListener loadRouteListener) {
        loadRoute.add(loadRouteListener);
    }  
    public void addSaveRouteListener(GeneralListener saveRouteListener) {
        saveRoute.add(saveRouteListener);
    }    
    public void addExportRouteListener(GeneralListener exportRouteListener) {
        exportRoute.add(exportRouteListener);
    }    
    public void addOptimizeRouteListener(GeneralListener optimizeRouteListener) {
        optimizeRoute.add(optimizeRouteListener);
    }    
    public void addWhatWasClickedListener(GeneralListener whatWasClickedListener) {
        whatWasClicked.add(whatWasClickedListener);
    }   
    public void addFavoriteListener(GeneralListener addFavListener) {
        addFav.add(addFavListener);
    }    
    public void addSetSpeedListener(GeneralListener setSpeedListener) {
        speed.add(setSpeedListener);
    }    
    public void addCloseListener(GeneralListener closeListener) {
        close.add(closeListener);
    }    
    public void addHighwayMalusListener(GeneralListener highwayMalusListener) {
        highwayMalus.add(highwayMalusListener);
    }    
    public void addHeightMalusListener(GeneralListener heightMalusListener) {
        heightMalus.add(heightMalusListener);
    }    
    public void addImportHeightMapListener(GeneralListener importHeightMapListener) {
        importHeightMap.add(importHeightMapListener);
    }    
    public void addPrintRouteListener(GeneralListener printRouteListener) {
        printRoute.add(printRouteListener);
        //TODO
    }
    public void addDeleteNavNodeListener(GeneralListener deleteNavNodeListener) {
        deleteNavPointList.add(deleteNavNodeListener);
    }
    public void addTextuelNavPointListener(GeneralListener textuelNavPointListener) {
        addTextuelNavPointList.add(textuelNavPointListener);
    }
    public void addLoadMapListener(GeneralListener loadMapListener) {
        loadMapList.add(loadMapListener);
    }
    
    public static void fireEvent(List<GeneralListener> listener, GeneralEvent event) {
        for (GeneralListener lst: listener) {
            lst.handleEvent(event);
        }  
    }
    
}
