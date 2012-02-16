package kit.route.a.lot.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import kit.route.a.lot.common.Context;
import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.OSMType;
import kit.route.a.lot.common.POIDescription;
import kit.route.a.lot.common.Projection;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.common.Util;
import kit.route.a.lot.controller.listener.AddFavoriteListener;
import kit.route.a.lot.controller.listener.ChangeViewListener;
import kit.route.a.lot.controller.listener.ClickPositionListener;
import kit.route.a.lot.controller.listener.CloseListener;
import kit.route.a.lot.controller.listener.DeleteFavoriteListener;
import kit.route.a.lot.controller.listener.DeleteMapListener;
import kit.route.a.lot.controller.listener.DeleteNavNodeListener;
import kit.route.a.lot.controller.listener.ExportRouteListener;
import kit.route.a.lot.controller.listener.GeneralListener;
import kit.route.a.lot.controller.listener.GetPoiDescriptionListener;
import kit.route.a.lot.controller.listener.HeightMalusListener;
import kit.route.a.lot.controller.listener.HighwayMalusListener;
import kit.route.a.lot.controller.listener.ImportOsmFileListener;
import kit.route.a.lot.controller.listener.LoadMapListener;
import kit.route.a.lot.controller.listener.LoadRouteListener;
import kit.route.a.lot.controller.listener.OrderNavNodesListener;
import kit.route.a.lot.controller.listener.SaveRouteListner;
import kit.route.a.lot.controller.listener.SearchNameListener;
import kit.route.a.lot.controller.listener.SelectNavNodeListener;
import kit.route.a.lot.controller.listener.ShowFavoriteDescriptionListener;
import kit.route.a.lot.controller.listener.SpeedListener;
import kit.route.a.lot.controller.listener.SuggestionListener;
import kit.route.a.lot.gui.GUI;
import kit.route.a.lot.gui.GUIHandler;
import kit.route.a.lot.gui.event.GeneralEvent;
import kit.route.a.lot.io.HeightLoader;
import kit.route.a.lot.io.MapIO;
import kit.route.a.lot.io.OSMLoader;
import kit.route.a.lot.io.RouteIO;
import kit.route.a.lot.io.SRTMLoader;
import kit.route.a.lot.io.StateIO;
import kit.route.a.lot.map.Node;
import kit.route.a.lot.map.infosupply.ComplexInfoSupplier;
import kit.route.a.lot.map.rendering.Renderer;
import kit.route.a.lot.map.rendering.Renderer3D;
import kit.route.a.lot.routing.Precalculator;
import kit.route.a.lot.routing.Router;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Controller {

    private Renderer renderer = new Renderer();
    private GUIHandler guiHandler = new GUIHandler();
    private State state = State.getInstance();
    private static Logger logger = Logger.getLogger(Controller.class);
    
    public static void main(String[] args) {
        PropertyConfigurator.configure("config/log4j.conf");
        new Controller();
    }
    
    private Controller() {
        File defaultMap = new File("./test/resources/karlsruhe_small_current.osm");
        
        Util.startTimer();
        importHeightmap("./srtm/");
        logger.info("### Loaded heightmaps in " + Util.stopTimer() + " ###");
        
        loadState();
        logger.info("### Loaded state in " + Util.stopTimer() + " ###");
        
        if (state.getLoadedMapFile() != null && state.getLoadedMapFile().exists()) {
            loadMap(state.getLoadedMapFile());
            logger.info("### Loaded map in " + Util.stopTimer() + " ###");
        } else if (defaultMap.exists()) {
            logger.info("import default map...");
            importMap(defaultMap);
            setViewToMapCenter();
            logger.info("### Imported default map in " + Util.stopTimer() + " ###");
        } else {
            logger.warn("no map loaded."); //TODO not loading map 
        }                        
        
        guiHandler.addAddNavNodeListener(new SelectNavNodeListener(this));
        guiHandler.addChangedViewListener(new ChangeViewListener(this));
        guiHandler.addImportMapListener(new ImportOsmFileListener(this));  
        guiHandler.addOptimizeRouteListener(new OrderNavNodesListener(this));
        guiHandler.addDeleteNavNodeListener(new DeleteNavNodeListener(this));
        guiHandler.addLoadMapListener(new LoadMapListener(this));
        guiHandler.addAddFavoriteListener(new AddFavoriteListener(this));
        guiHandler.addSaveRouteListener(new SaveRouteListner(this));
        guiHandler.addLoadRouteListener(new LoadRouteListener(this));
        guiHandler.addExportRouteListener(new ExportRouteListener(this));
        guiHandler.addDeleteFavListener(new DeleteFavoriteListener(this));
        guiHandler.addSetSpeedListener(new SpeedListener(this));
        guiHandler.addClickPositionListener(new ClickPositionListener(this));
        guiHandler.addHeightMalusListener(new HeightMalusListener(this));
        guiHandler.addHighwayMalusListener(new HighwayMalusListener(this));
        guiHandler.addCloseListener(new CloseListener(this));
        guiHandler.addGetPoiDescriptionListener(new GetPoiDescriptionListener(this));
        guiHandler.addSwitchMapModeListener(new GeneralListener() {
            @Override
            public void handleEvent(GeneralEvent event) {
                switchMapMode();
            }         
        });
        guiHandler.addAutoCompletionListener(new SuggestionListener(this));
        guiHandler.addGetNavNodeDescriptionListener(new SearchNameListener(this));
        guiHandler.addFavDescriptionListener( new ShowFavoriteDescriptionListener(this));
        guiHandler.addDeleteMapListener(new DeleteMapListener(this));
        guiHandler.setView(state.getCenterCoordinates());
        guiHandler.updateMapList(state.getImportedMaps()); 
        guiHandler.setSpeed(state.getSpeed());
    }
        
    
    private static void loadState() {
        File stateFile = new File("./state.state");
        if (stateFile.exists()) {   
            logger.info("load state file..."); 
            try { 
                StateIO.loadState(stateFile); 
            } catch (IOException e) {
                logger.error("state loading: Read error occurred.");
                e.printStackTrace();
            }
            File directoryOfSral = new File("./sral"); // Directory is just a list of files

            if(directoryOfSral.isDirectory()) { // check to make sure it is a directory
                String filenames[] = directoryOfSral.list();
                for(String filename : filenames) {
                    if (filename.endsWith(".sral")) {
                        State.getInstance().getImportedMaps().add("./sral/" + filename);
                    }
                }
            }
        }
    }
    
    public void importMap(File osmFile) {
        if(!osmFile.exists()) {
            logger.error("osm File doesn't exist");
        } else {
            state.resetMap();
            new OSMLoader().importMap(osmFile);
            Precalculator.precalculate();
            state.getLoadedMapInfo().compactifyDatastructures();
            renderer.resetCache();
            state.setLoadedMapFile(new File("./sral/" + Util.removeExtension(osmFile.getName()) + "_" + state.getHeightMalus() + "_" + state.getHighwayMalus() + ".sral"));
            state.getImportedMaps().add("./sral/" + Util.removeExtension(osmFile.getName()) + "_" + state.getHeightMalus() + "_" + state.getHighwayMalus() + ".sral");
            guiHandler.updateMapList(state.getImportedMaps());
            try {
                MapIO.saveMap(state.getLoadedMapFile());
            } catch (IOException e) {
                logger.error("Could not save imported map to file.");
            }
            setViewToMapCenter();
            guiHandler.setView(state.getCenterCoordinates());
        }    
    }
    
    public void saveMap(String mapPath) {
        File mapFile = new File(mapPath);
        try {
            MapIO.saveMap(mapFile);
        } catch (IOException e) {
            logger.fatal("saveMap: IO Exception in MapIO");
        }
    }
    
    public void loadMap(File file) {
        File mapFile = file;
        if(!mapFile.exists()) {
            logger.error("map File doesn't exist");
        } else {
            state.resetMap();
            try {
                MapIO.loadMap(mapFile);
                state.setLoadedMapFile(mapFile);
            } catch (IOException e) {
                logger.fatal("loadMap: IO Exception in MapIO");
            }
            setViewToMapCenter(); 
            guiHandler.setView(state.getCenterCoordinates());
            renderer.resetCache();
        }
    }

    public void deleteMap(String path){
        File file = new File(path);
        if(file.exists()) {
            file.delete();
        }
        for (int i = 0; i < state.getImportedMaps().size(); i++) {
            if (state.getImportedMaps().get(i).equals(path)) {
                state.getImportedMaps().remove(i);
            }
        }
        guiHandler.updateMapList(state.getImportedMaps());
    }
       
    public void importHeightmap(String directory) {
        File heightFile = new File(directory);
        HeightLoader loader = new SRTMLoader();
        loader.load(heightFile);
    }
    
    
    public void saveRoute(String path) {
        File routeFile = new File(path);
        if (state.getCurrentRoute().size() != 0) {
            try {
                RouteIO.saveCurrentRoute(routeFile);
            } catch (IOException e) {
                logger.error("Could not save route to file '" + path + "'.");
            }
        }
    }

    public void loadRoute(String path) {
        File routeFile = new File(path);
        if (!routeFile.exists()) {
            logger.error("RouteFile existiert nicht");
        } else {
            try {
                RouteIO.loadCurrentRoute(routeFile);
            } catch (IOException e) {
                logger.error("Could not load route from file '" + path + "'.");
            }
        }
        calculateRoute();
    }

    public void exportRoute(String path) {
        String kmlPath = path;
        if (!kmlPath.endsWith(".kml")) {
            kmlPath += ".kml";
        }
        File routeFile = new File(kmlPath);
        if (state.getCurrentRoute().size() != 0) {
            RouteIO.exportCurrentRouteToKML(routeFile);
        }
    }

    public void printRoute() {
    }

      
    public void addNavNode(Coordinates pos, int position) {
        Selection newSel = state.getLoadedMapInfo().select(pos);
        if (newSel != null) {
            if (position == 0 && state.getNavigationNodes().size() > 1) {
                state.getNavigationNodes().remove(0);
                state.getNavigationNodes().add(0, newSel);
            } else if (position == state.getNavigationNodes().size() && state.getNavigationNodes().size() > 1) {
                state.getNavigationNodes().remove(state.getNavigationNodes().remove(state.getNavigationNodes().size() - 1));
                state.getNavigationNodes().add(newSel);
            } else {
                state.getNavigationNodes().add(position, newSel);
            }
        }
        guiHandler.updateNavPointsList(state.getNavigationNodes());
        calculateRoute();
        guiHandler.updateGUI();
//        for (int i = 0; i < state.getNavigationNodes().size(); i++) {
//            guiHandler.showNavNodeDescription(state.getNavigationNodes().get(i).getName(), i);    // TODO error in GUI
//        }
    }
    
    public void addNavNode(String name, int position) {
        Selection newSel = state.getLoadedMapInfo().select(name);
        if (newSel != null) {
            if (position == 0 && state.getNavigationNodes().size() > 1) {
                state.getNavigationNodes().remove(0);
                state.getNavigationNodes().add(0, newSel);
            } else if (position == state.getNavigationNodes().size() && state.getNavigationNodes().size() > 1) {
                state.getNavigationNodes().remove(state.getNavigationNodes().remove(state.getNavigationNodes().size() - 1));
                state.getNavigationNodes().add(newSel);
            } else {
                state.getNavigationNodes().add(position, newSel);
            }
        }
        guiHandler.updateNavPointsList(state.getNavigationNodes());
        calculateRoute();
        guiHandler.updateGUI();
//        for (int i = 0; i < state.getNavigationNodes().size(); i++) {
//            guiHandler.showNavNodeDescription(state.getNavigationNodes().get(i).getName(), i);    // TODO error in GUI
//        }
    }

    public void getNavNodeFromText(String str) {
        Selection sel = state.getLoadedMapInfo().select(str);
        if (sel != null) {
            state.getNavigationNodes().add(state.getNavigationNodes().size() - 1, sel);
            guiHandler.updateNavPointsList(state.getNavigationNodes());
            calculateRoute();
            guiHandler.updateGUI();
        }
    }
    
    public void deleteNavNode(int pos) {
        if (pos < state.getNavigationNodes().size()) {
            state.getNavigationNodes().remove(pos);
            state.setCurrentRoute(new ArrayList<Integer>());
            guiHandler.updateNavPointsList(state.getNavigationNodes());
            calculateRoute();
            guiHandler.updateGUI();
        }
    }
    
    public void deleteNavNode(Coordinates pos) {
        for (int i = 0; i < state.getNavigationNodes().size(); i++) {
            Node node = new Node(state.getNavigationNodes().get(i).getPosition());
            Coordinates topLeft = new Coordinates();
            Coordinates bottomRight = new Coordinates();
            topLeft.setLatitude(pos.getLatitude() - Projection.getZoomFactor(state.getDetailLevel()) * state.getClickRadius());
            topLeft.setLongitude(pos.getLongitude() - Projection.getZoomFactor(state.getDetailLevel()) * state.getClickRadius());
            bottomRight.setLatitude(pos.getLatitude() + Projection.getZoomFactor(state.getDetailLevel()) * state.getClickRadius());
            bottomRight.setLongitude(pos.getLongitude() + Projection.getZoomFactor(state.getDetailLevel()) * state.getClickRadius());
            if (node.isInBounds(topLeft, bottomRight)) {
                state.getNavigationNodes().remove(i);
                state.setCurrentRoute(new ArrayList<Integer>());
                guiHandler.updateNavPointsList(state.getNavigationNodes());
                calculateRoute();
                guiHandler.updateGUI();
            }
        }
    }

    public void switchNavNodes(int index1, int index2) {
        if (index1 < state.getNavigationNodes().size()
            && index2 < state.getNavigationNodes().size()) {
            Collections.swap(state.getNavigationNodes(), index1, index2);
            calculateRoute();
        }
    }

    public void orderNavNodes() {  
        state.setNavigationNodes(Router.optimizeRoute());
        state.setCurrentRoute(Router.calculateRoute());
        guiHandler.updateGUI();
    }

    
    public void addFavorite(Coordinates pos, String name, String description) {  
        state.getLoadedMapInfo().addFavorite(pos, new POIDescription(name, OSMType.FAVOURITE, description));  //TODO category
        guiHandler.updateGUI();
    }

    public void deleteFavorite(Coordinates pos) {
        state.getLoadedMapInfo().deleteFavorite(pos, state.getDetailLevel(), state.getClickRadius());
        guiHandler.updateGUI();
    }


    public void passElementType(Coordinates pos) {
        //TODO: better approximation:
        
        float adaptedRadius = (Projection.getZoomFactor(state.getDetailLevel())) * state.getClickRadius();
        Coordinates topLeft = new Coordinates(pos.getLatitude() - adaptedRadius,
                pos.getLongitude() - adaptedRadius);       
        Coordinates bottomRight = new Coordinates(pos.getLatitude() + adaptedRadius,
                pos.getLongitude() + adaptedRadius);
        for (Selection navNode: state.getNavigationNodes()) {
            Node node = new Node(navNode.getPosition());
            if (node.isInBounds(topLeft, bottomRight)) {
                guiHandler.passElementType(GUI.NAVNODE);
                return;
            }
        }    
        if (state.getLoadedMapInfo().getPOIDescription(pos,
                state.getClickRadius(), state.getDetailLevel()) != null) {
            guiHandler.passElementType(GUI.POI);
            return;
        } 
        if(state.getLoadedMapInfo().getFavoriteDescription(pos,
                state.getDetailLevel(), state.getClickRadius()) != null) {
            guiHandler.passElementType(GUI.FAVORITE);
            return;
        }
        guiHandler.passElementType(GUI.FREEMAPSPACE);
    }
    
    public void passPOIDescription(Coordinates pos) {   
        POIDescription description = state.getLoadedMapInfo().getPOIDescription(pos, 
                state.getClickRadius(), state.getDetailLevel());
        if (description != null) {
            guiHandler.passDescription(description);
        }
    }

    public void passFavDescription(Coordinates pos) {
        POIDescription description = state.getLoadedMapInfo().getFavoriteDescription(pos,
                state.getDetailLevel(), state.getClickRadius());
        if (description != null) {
            guiHandler.passDescription(description);
        }
    }
    
    public void passSearchCompletion(String str) {
        guiHandler.showSearchCompletion(state.getLoadedMapInfo().suggestCompletions(str));
    }
    
    public void passTextRoute() {   //TODO
        if (state.getCurrentRoute().size() != 0) {
            ComplexInfoSupplier.getRouteDescription(state.getCurrentRoute());
        }
    }
  
    
    public void setSpeed(int speed) {
        if(speed >= 0) {
            state.setSpeed(speed);
            guiHandler.showRouteValues(ComplexInfoSupplier.getDuration(state.getCurrentRoute(), speed),
                    ComplexInfoSupplier.getLength(state.getCurrentRoute()));
        }
    }
    
    public void setHeightMalus(int newMalus) {
        if (newMalus >= 0) {
            state.setHeightMalus(newMalus);
        }
    }

    public void setHighwayMalus(int newMalus) {
        if (newMalus >= 0) {
            state.setHighwayMalus(newMalus);
        }
    }
    
    public void setViewToMapCenter() {
        if (state.getLoadedMapInfo() == null) {
            return;
        }
        Coordinates upLeft = new Coordinates();
        Coordinates bottomRight = new Coordinates();
        state.getLoadedMapInfo().getBounds(upLeft, bottomRight);
        Coordinates center = new Coordinates();
        center.setLatitude((upLeft.getLatitude() + bottomRight.getLatitude()) / 2);
        center.setLongitude((upLeft.getLongitude() + bottomRight.getLongitude()) / 2);
        state.setCenterCoordinates(center);
    }
    
    public void calculateRoute() {
        State state = State.getInstance();
        if (state.getNavigationNodes().size() >= 2) {
            try {
                state.setCurrentRoute(Router.calculateRoute());
                int duration = ComplexInfoSupplier.getDuration(state.getCurrentRoute(), state.getSpeed()); 
                int length = ComplexInfoSupplier.getLength(state.getCurrentRoute());
                guiHandler.showRouteValues(duration, length);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public void render(Context context) {
        state.setDetailLevel(context.getZoomlevel());
        renderer.render(context); 
    }
    
    public void switchMapMode() {
        renderer = (renderer instanceof Renderer3D) ? new Renderer() : new Renderer3D();
    }
    
    public void prepareForShutDown() {
        File stateFile = new File("./state.state");
        try {
            StateIO.saveState(stateFile);
        } catch (IOException e) {
            logger.fatal("IO exception in StateIO");
        }
    }   

}
