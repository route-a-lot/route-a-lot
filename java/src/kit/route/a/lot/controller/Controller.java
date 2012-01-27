package kit.route.a.lot.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import kit.route.a.lot.common.Context;
import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.POIDescription;
import kit.route.a.lot.common.Projection;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.common.Util;

import kit.route.a.lot.controller.listener.AddFavoriteListener;
import kit.route.a.lot.controller.listener.ClickPositionListener;
import kit.route.a.lot.controller.listener.CloseListener;
import kit.route.a.lot.controller.listener.DeleteNavNodeListener;
import kit.route.a.lot.controller.listener.ExportRouteListener;
import kit.route.a.lot.controller.listener.GeneralListener;
import kit.route.a.lot.controller.listener.GetPoiDescriptionListener;
import kit.route.a.lot.controller.listener.HeightMalusListener;
import kit.route.a.lot.controller.listener.HighwayMalusListener;
import kit.route.a.lot.controller.listener.ImportOsmFileListener;
import kit.route.a.lot.controller.listener.LoadMapListener;
import kit.route.a.lot.controller.listener.OrderNavNodesListener;
import kit.route.a.lot.controller.listener.SearchNameListener;
import kit.route.a.lot.controller.listener.SelectNavNodeListener;
import kit.route.a.lot.controller.listener.LoadRouteListener;
import kit.route.a.lot.controller.listener.SpeedListener;
import kit.route.a.lot.controller.listener.SaveRouteListner;
import kit.route.a.lot.controller.listener.ChangeViewListener;
import kit.route.a.lot.controller.listener.SuggestionListener;
import kit.route.a.lot.gui.GUI;
import kit.route.a.lot.gui.GUIHandler;
import kit.route.a.lot.gui.event.GeneralEvent;
import kit.route.a.lot.io.HeightLoader;
import kit.route.a.lot.io.MapIO;
import kit.route.a.lot.io.OSMLoader;
import kit.route.a.lot.io.Printer;
import kit.route.a.lot.io.RouteIO;
import kit.route.a.lot.io.SRTMLoader;
import kit.route.a.lot.io.StateIO;
import kit.route.a.lot.map.infosupply.ComplexInfoSupplier;
import kit.route.a.lot.map.rendering.Renderer;
import kit.route.a.lot.map.rendering.Renderer3D;
import kit.route.a.lot.map.Node;
import kit.route.a.lot.routing.Precalculator;
import kit.route.a.lot.routing.Router;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Controller {

    private Renderer renderer = new Renderer3D();
    private GUIHandler guiHandler = new GUIHandler();
    private State state = State.getInstance();

    private static Logger logger = Logger.getLogger(Controller.class);
    
    /**
     * Operation main
     * 
     * @param args :-)
     */
    public static void main(String[] args) {
        PropertyConfigurator.configure("config/log4j.conf");
        new Controller();
    }
    
    private Controller() {
        File defaultMap = new File("./test/resources/karlsruhe_small.osm");
        
        importHeightMap("./srtm/");

        loadState();
        if (state.getLoadedMapFile() != null && state.getLoadedMapFile().exists()) {
              loadMap(state.getLoadedMapFile());
        } else if (defaultMap.exists()) {
              logger.info("import default map...");
              importMap(defaultMap);
              setViewToMapCenter();
        } else {
              logger.warn("no map loaded."); //TODO not loading map 
        }                        
        
        guiHandler.addListenerAddNavNode(new SelectNavNodeListener(this));
        guiHandler.addChangedViewListener(new ChangeViewListener(this));
        guiHandler.addListenerImportMap(new ImportOsmFileListener(this));  
        guiHandler.addOptimizeRouteListener(new OrderNavNodesListener(this));
        guiHandler.addDeleteNavNodeListener(new DeleteNavNodeListener(this));
        guiHandler.addLoadMapListener(new LoadMapListener(this));
        guiHandler.addAddFavoriteListener(new AddFavoriteListener(this));
        guiHandler.addSaveRouteListener(new SaveRouteListner(this));
        guiHandler.addLoadRouteListener(new LoadRouteListener(this));
        guiHandler.addExportRouteListener(new ExportRouteListener(this));
        guiHandler.addDeleteFavListener(new DeleteNavNodeListener(this));
        guiHandler.addSetSpeedListener(new SpeedListener(this));
        guiHandler.addClickPositionListener(new ClickPositionListener(this));
        guiHandler.addHeightMalusListener(new HeightMalusListener(this));
        guiHandler.addHighwayMalusListener(new HighwayMalusListener(this));
        guiHandler.addCloseListener(new CloseListener(this));
        guiHandler.addGetPoiDescriptionListener(new GetPoiDescriptionListener(this));
        guiHandler.addSwitchMapModeListener(new GeneralListener() {
            @Override
            public void handleEvent(GeneralEvent event) {
                Renderer oldRenderer = renderer;
                renderer = (renderer instanceof Renderer3D) ? new Renderer() : new Renderer3D();
                renderer.inheritCache(oldRenderer);
            }         
        });
        guiHandler.addAutoCompletitionListener(new SuggestionListener(this));
        guiHandler.addGetNavNodeDescriptionListener(new SearchNameListener(this));
        guiHandler.setView(state.getCenterCoordinates());
        guiHandler.updateMapList(state.getImportedMaps()); 
        guiHandler.setSpeed(state.getSpeed());
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

//    /**
//     * Operation toggle3D
//     */
//    public void toggle3D() {
//    }

    
    public void saveMap(String mapPath) {
        File mapFile = new File(mapPath);
        try {
            MapIO.saveMap(mapFile);
        } catch (IOException e) {
            logger.fatal("saveMap: IO Exception in MapIO");
        }
    }
    
    /**
     * Operation loadMap
     * 
     * @return
     */
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
            renderer.resetRenderCache();
        }
    }

    /**
     * Operation importMap
     */
    public void importMap(File osmFile) {
        if(!osmFile.exists()) {
            logger.error("osm File doesn't exist");
        } else {
            state.resetMap();
            new OSMLoader().importMap(osmFile);
            Precalculator.precalculate();
            state.getLoadedMapInfo().trimm();
            renderer.resetRenderCache();
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

    /**
     * Operation addNavNode
     * 
     * @return
     */
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
    
    /**
     * Same as above with names
     * 
     */
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

    /**
     * Operation deleteNavNode
     */
    public void deleteNavNode(int pos) {
        if (pos < state.getNavigationNodes().size()) {
            state.getNavigationNodes().remove(pos);
            state.setCurrentRoute(new ArrayList<Integer>());
            guiHandler.updateNavPointsList(state.getNavigationNodes());
            calculateRoute();
            guiHandler.updateGUI();
        }
    }
    
    /**
     * Operation deleteNavNode 
     */
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

    /**
     * Operation switchNavNodes
     */
    public void switchNavNodes(int one, int two) {
        if (one < state.getNavigationNodes().size() && two < state.getNavigationNodes().size()) {
            Collections.swap(state.getNavigationNodes(), one, two);
            calculateRoute();
        }
    }

    /**
     * Operation orderNavNodes
     */
    public void orderNavNodes() {  
        List<Selection> col = Router.optimizeRoute();  
        state.setNavigationNodes(col);
        state.setCurrentRoute(Router.calculateRoute());
        guiHandler.updateGUI();
    }

    /**
     * Operation addFavorite
     */
    public void addFavorite(Coordinates pos, String name, String description) {  
        state.getLoadedMapInfo().addFavorite(pos, new POIDescription("Favorit", 0, description));  //TODO category
        guiHandler.updateGUI();
    }

    /**
     * Operation deleteFavorite
     */
    public void deleteFavorite(Coordinates pos) {
        state.getLoadedMapInfo().deleteFavorite(pos);
        guiHandler.updateGUI();
    }

    /**
     * Operation saveRoute
     */
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

    /**
     * Operation loadRoute
     */
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

    /**
     * Operation exportRoute
     */
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

    /**
     * Operation printRoute
     */
    public void printRoute() {
        if (state.getCurrentRoute().size() != 0) {
            Printer.printRouteDescription();
        }
    }

    /**
     * Operation typeAddress
     */
    public void typeAddress() {  //TODO
    }

    /**
     * Operation searchAddress
     */
    public void searchAddress() {   //TODO
        
    }

    public void getCompletition(String str) {
        guiHandler.showCompletition(state.getLoadedMapInfo().suggestCompletions(str));
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

    /**
     * Operation setSpeed
     */
    public void setSpeed(int speed) {
        if(speed >= 0) {
            state.setSpeed(speed);
            guiHandler.showRouteValues(ComplexInfoSupplier.getDuration(state.getCurrentRoute(), speed),
                    ComplexInfoSupplier.getLength(state.getCurrentRoute()));
        }
    }
    
    public void whatWasClicked(Coordinates pos) {
        for (int i = 0; i < state.getNavigationNodes().size(); i++) {
            Node node = new Node(state.getNavigationNodes().get(i).getPosition());
            Coordinates topLeft = new Coordinates();
            Coordinates bottomRight = new Coordinates();
            topLeft.setLatitude(pos.getLatitude() - (state.getDetailLevel() + 1) * 2 * state.getClickRadius());
            topLeft.setLongitude(pos.getLongitude() -(state.getDetailLevel() + 1) * 2 * state.getClickRadius());
            bottomRight.setLatitude(pos.getLatitude() + (state.getDetailLevel() + 1) * 2 * state.getClickRadius());
            bottomRight.setLongitude(pos.getLongitude() + (state.getDetailLevel() + 1) * 2 * state.getClickRadius());
            if (node.isInBounds(topLeft, bottomRight)) {
                guiHandler.thisWasClicked(GUI.NAVNODE, pos);
                return;
            }
        }    
        if (state.getLoadedMapInfo().getPOIDescription(pos, state.getClickRadius(), state.getDetailLevel()) != null) {
            guiHandler.thisWasClicked(GUI.POI, pos);
            return;
        } 
        if(state.getLoadedMapInfo().isFavorite(pos, state.getDetailLevel(), state.getClickRadius())) {
            guiHandler.thisWasClicked(GUI.FAVORITE, pos);
            return;
        }
        guiHandler.thisWasClicked(GUI.FREEMAPSPACE, pos);
    }

    /**
     * Operation getPOIInfo
     */
    public void getPOIInfo(Coordinates pos) {   
        POIDescription info = state.getLoadedMapInfo().getPOIDescription(pos, state.getClickRadius(), state.getDetailLevel());
        if (info != null) {
            guiHandler.showPoiDescription(info, pos);
        }
    }

    /**
     * Operation showTextRoute
     */
    public void showTextRoute() {   //TODO
        if (state.getCurrentRoute().size() != 0) {
            ComplexInfoSupplier.getRouteDescription(state.getCurrentRoute());
        }
    }

    /**
     * Operation setHeightMalus
     */
    public void setHeightMalus(int newMalus) {
        if (newMalus >= 0) {
            state.setHeightMalus(newMalus);
        }
    }

    /**
     * Operation setHighwayMalus
     */
    public void setHighwayMalus(int newMalus) {
        if (newMalus >= 0) {
            state.setHighwayMalus(newMalus);
        }
    }

    /**
     * Operation importHeightMap
     */
    public void importHeightMap(String path) {
        File heightFile = new File(path);
        HeightLoader loader = new SRTMLoader();
        loader.load(heightFile);
    }

    /**
     * Operation render
     */
    public void render(Context context, int zoomLevel) {
        state.setDetailLevel(zoomLevel);
        renderer.render(context, zoomLevel); 
    }

    /**
     * Operation calculateRoute
     */
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
    
    public void prepareForShutDown() {
        File stateFile = new File("./state.state");
        try {
            StateIO.saveState(stateFile);
        } catch (IOException e) {
            logger.fatal("IO exception in StateIO");
        }
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
            File directoryOfSrl = new File("./sral"); // Directory is just a list of files

            if(directoryOfSrl.isDirectory()) { // check to make sure it is a directory
                String filenames[] = directoryOfSrl.list();
                for(String filename : filenames) {
                    if (filename.endsWith(".sral")) {
                        State.getInstance().getImportedMaps().add("./sral/" + filename);
                    }
                }
            }
        }
    }
   
    public Renderer getRender() {
        return renderer;
    }
}
