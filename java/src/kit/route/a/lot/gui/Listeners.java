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
    public List<GeneralListener> clickPosition;
    public List<GeneralListener> addFav;
    public List<GeneralListener> loadRoute;
    public List<GeneralListener> saveRoute;
    public List<GeneralListener> speed;
    public List<GeneralListener> close;
    public List<GeneralListener> heightMalus;
    public List<GeneralListener> highwayMalus;
    public List<GeneralListener> importHeightMap;
    public List<GeneralListener> printRoute;
    public List<GeneralListener> addTextualNavPoint;
    public List<GeneralListener> deleteNavPoint;
    public List<GeneralListener> deleteFavPoint;
    public List<GeneralListener> loadMap;
    public List<GeneralListener> switchMapMode;
    public List<GeneralListener> poiDescription;
    public List<GeneralListener> getNavNodeDescription;
    public List<GeneralListener> autoCompletition;
    
    /**
     * Initializes all listener collections that are used to communicate with the controller.
     */
    public Listeners() {
        targetSelected = new ArrayList<GeneralListener>();
        viewChanged = new ArrayList<GeneralListener>();
        importOsmFile = new ArrayList<GeneralListener>();
        clickPosition = new ArrayList<GeneralListener>();
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
        addTextualNavPoint = new ArrayList<GeneralListener>();
        deleteNavPoint = new ArrayList<GeneralListener>();
        deleteFavPoint = new ArrayList<GeneralListener>();
        loadMap = new ArrayList<GeneralListener>();
        optimizeRoute = new ArrayList<GeneralListener>();
        switchMapMode = new ArrayList<GeneralListener>();
        poiDescription = new ArrayList<GeneralListener>();
        getNavNodeDescription = new ArrayList<GeneralListener>();
        autoCompletition = new ArrayList<GeneralListener>();
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
    public void addClickPositionListener(GeneralListener clickPositionListener) {
        clickPosition.add(clickPositionListener);
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
    }
    public void addDeleteNavNodeListener(GeneralListener deleteNavNodeListener) {
        deleteNavPoint.add(deleteNavNodeListener);
    }
    public void addDeleteFavListener(GeneralListener deleteFavListener) {
        deleteFavPoint.add(deleteFavListener);
    }
    public void addTextualNavPointListener(GeneralListener textualNavPointListener) {
        addTextualNavPoint.add(textualNavPointListener);
    }
    public void addLoadMapListener(GeneralListener loadMapListener) {
        loadMap.add(loadMapListener);
    }
    public void addSwitchMapModeListener(GeneralListener switchMapModeListener) {
        switchMapMode.add(switchMapModeListener);
    }
    public void addPoiDescriptionListener(GeneralListener poiDescriptionListener) {
        poiDescription.add(poiDescriptionListener);
    }
    public void addGetNavNodeDescriptionListener(GeneralListener getNavNodeDescriptionListener) {
        getNavNodeDescription.add(getNavNodeDescriptionListener);
    }
    public void addAutoCompletitionListener(GeneralListener autoCompletitionListener) {
        autoCompletition.add(autoCompletitionListener);
    }
    
    public static void fireEvent(List<GeneralListener> listener, GeneralEvent event) {
        for (GeneralListener lst: listener) {
            lst.handleEvent(event);
        }  
    }
    
}
