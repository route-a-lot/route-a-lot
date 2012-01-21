package kit.route.a.lot.controller;

import java.io.File;import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;


import kit.route.a.lot.common.Context;
import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.POIDescription;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.controller.listener.ImportOsmFileListener;
import kit.route.a.lot.controller.listener.TargetSelectedListener;
import kit.route.a.lot.controller.listener.ViewChangedListener;
import kit.route.a.lot.gui.GUIHandler;
import kit.route.a.lot.io.HeightLoader;
import kit.route.a.lot.io.MapIO;
import kit.route.a.lot.io.OSMLoader;
import kit.route.a.lot.io.Printer;
import kit.route.a.lot.io.RouteIO;
import kit.route.a.lot.io.SRTMLoader;
import kit.route.a.lot.io.StateIO;
import kit.route.a.lot.map.infosupply.MapInfo;
import kit.route.a.lot.map.rendering.Renderer;
import kit.route.a.lot.map.Node;
import kit.route.a.lot.routing.Router;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Controller {

    /** Attributes */
    /**
     * 
     */
    private Renderer renderer;
    /**
     * 
     */
    private GUIHandler guiHandler;
    
    private State state;

    private static Logger logger = Logger.getLogger(Controller.class);
    
    private Controller() {
        renderer = new Renderer();
        guiHandler = new GUIHandler();
        state = State.getInstance();
    }
    
    
    
    /**
     * Operation setView
     * 
     * @return
     */
    public void setView() {
        guiHandler.updateMap(); // TODO needed?
    }
    
    public void setViewToMapCenter() {
        Coordinates upLeft = new Coordinates();
        Coordinates bottomRight = new Coordinates();
        state.getLoadedMapInfo().getBounds(upLeft, bottomRight);
        Coordinates center = new Coordinates();
        center.setLatitude((upLeft.getLatitude() + bottomRight.getLatitude()) / 2);
        center.setLongitude((upLeft.getLongitude() + bottomRight.getLongitude()) / 2);
        state.setCenterCoordinate(center);
    }

    /**
     * Operation setZoomLevel
     * 
     * @return
     */
    public void setZoomLevel() {    //TODO needed?
    }

    /**
     * Operation toggle3D
     * 
     * @return
     */
    public void toggle3D() {
    }

    
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
    public void loadMap(String mapPath) {
        File mapFile = new File(mapPath);
        if(!mapFile.exists()) {
            logger.error("map File doesn't exist");
        } else {
            state.resetMap();
            try {
                MapIO.loadMap(mapFile);
            } catch (IOException e) {
                logger.fatal("loadMap: IO Exception in MapIO");
            }
            state.getLoadedMapInfo().buildZoomlevels();
            setViewToMapCenter();
            guiHandler.setView(state.getCenterCoordinate());
            renderer.resetRenderCache();
        }
    }

    /**
     * Operation importMap
     * 
     * @return
     */
    public void importMap(String osmPath) {
        File osmFile = new File(osmPath);
        if(!osmFile.exists()) {
            logger.error("osm File doesn't exist");
        } else {
            state.resetMap();
            new OSMLoader().importMap(osmFile);
            state.getLoadedMapInfo().buildZoomlevels();
            setViewToMapCenter();
            guiHandler.setView(state.getCenterCoordinate());
            renderer.resetRenderCache();
            //TODO saveMap
        }
       
    }

    /**
     * Operation addNavNode
     * 
     * @return
     */
    public void addNavNode(Coordinates pos, int position, Context context) {
        //if (position < state.getNavigationNodes().size()) {   TODO ass soon as gui functionate 
        //    state.getNavigationNodes().remove(position);
        //}
        state.getNavigationNodes().add(position,
                state.getLoadedMapInfo().select(pos));
        calculateRoute();
        render(context, state.getDetailLevel());
    }

    /**
     * Operation deleteNavNode
     * 
     * @return
     */
    public void deleteNavNode(int pos) {
        if (pos < state.getNavigationNodes().size()) {
            state.getNavigationNodes().remove(pos);
        }
    }
    
    public void deleteNavNode(Coordinates pos) {
        for (int i = 0; i < state.getNavigationNodes().size(); i++) {
            Node node = new Node(state.getNavigationNodes().get(i).getPosition());
            Coordinates topLeft = new Coordinates();
            Coordinates bottomRight = new Coordinates();
            topLeft.setLatitude(pos.getLatitude() - state.getClickRadius());
            topLeft.setLongitude(pos.getLatitude() - state.getClickRadius());
            bottomRight.setLatitude(pos.getLatitude() + state.getClickRadius());
            topLeft.setLongitude(pos.getLatitude() + state.getClickRadius());
            if (node.isInBounds(topLeft, bottomRight)) {
                state.getNavigationNodes().remove(i);
            }
        }
    }

    /**
     * Operation switchNavNodes
     * 
     * @return
     */
    public void switchNavNodes(int one, int two) {
        if (one < state.getNavigationNodes().size() && two < state.getNavigationNodes().size()) {
            Selection tempOne = state.getNavigationNodes().get(one);    //I know could be shorter . . .
            Selection tempTwo = state.getNavigationNodes().get(two);
            state.getNavigationNodes().remove(one);
            state.getNavigationNodes().add(one, tempTwo);
            state.getNavigationNodes().remove(two);
            state.getNavigationNodes().add(two, tempOne);
        }
    }

    /**
     * Operation orderNavNodes
     * 
     * @return
     */
    public void orderNavNodes() {  
        Collection<Selection> col = Router.optimizeRoute();         //TODO better
        state.setNavigationNodes(new ArrayList<Selection>());
        state.getNavigationNodes().addAll(col);
    }

    /**
     * Operation addFavorite
     * 
     * @return
     */
    public void addFavorite(Coordinates pos, String name, String description) {  
        state.getLoadedMapInfo().addFavorite(pos, new POIDescription(name, 0, description));  //TODO category
    }

    /**
     * Operation deleteFavorite
     * 
     * @return
     */
    public void deleteFavorite(Coordinates pos) {
        state.getLoadedMapInfo().deleteFavorite(pos);
    }

    /**
     * Operation saveRoute
     * 
     * @return
     */
    public void saveRoute(String path) {
        File routeFile = new File(path);
        if (state.getCurrentRoute().size() != 0) {
            RouteIO.saveCurrentRoute(routeFile);
        }
    }

    /**
     * Operation loadRoute
     * 
     * @return
     */
    public void loadRoute(String path) {
        File routeFile = new File(path);
        if (!routeFile.exists()) {
            logger.error("RouteFile existiert nicht");
        } else {
            RouteIO.loadCurrentRoute(routeFile);
        }
    }

    /**
     * Operation exportRoute
     * 
     * @return
     */
    public void exportRoute(String path) {
        File routeFile = new File(path);
        if (state.getCurrentRoute().size() != 0) {
            RouteIO.exportCurrentRouteToKML(routeFile);
        }
    }

    /**
     * Operation printRoute
     * 
     * @return
     */
    public void printRoute() {
        if (state.getCurrentRoute().size() != 0) {
            Printer.printRouteDescription();
        }
    }

    /**
     * Operation typeAddress
     * 
     * @return
     */
    public void typeAddress() {  //TODO
    }

    /**
     * Operation searchAddress
     * 
     * @return
     */
    public void searchAddress() {   //TODO
        
    }

    /**
     * Operation searchPOI
     * 
     * @return
     */
    public void searchPOI() {   //TODO
    }

    /**
     * Operation searchFavorite
     * 
     * @return
     */
    public void searchFavorite() {  //TODO
    }

    /**
     * Operation setSpeed
     * 
     * @return
     */
    public void setSpeed(int speed) {
        if(speed >= 0) {
            state.setSpeed(speed);
        }
    }

    /**
     * Operation getPOIInfo
     * 
     * @return
     */
    public void getPOIInfo(Coordinates pos) {   //TODO 
        
    }

    /**
     * Operation showTextRoute
     * 
     * @return
     */
    public void showTextRoute() {   //TODO
        
    }

    /**
     * Operation setHeightMalus
     * 
     * @return
     */
    public void setHeightMalus(int newMalus) {
        if (newMalus >= 0) {
            state.setHeightMalus(newMalus);
        }
    }

    /**
     * Operation setHighwayMalus
     * 
     * @return
     */
    public void setHighwayMalus(int newMalus) {
        if (newMalus >= 0) {
            state.setHighwayMalus(newMalus);
        }
    }

    /**
     * Operation importHeightMap
     * 
     * @return
     */
    public void importHeightMap(String path) {
        File heightFile = new File(path);
        HeightLoader loader = new SRTMLoader();
        loader.load(heightFile);
    }

    /**
     * Operation render
     * 
     * @return
     */
    public void render(Context context, int zoomLevel) {
        state.setDetailLevel(zoomLevel);
        renderer.render(context, zoomLevel); 
    }

    /**
     * Operation calculateRoute
     * 
     * @return
     */
    public void calculateRoute() {
        State state = State.getInstance();
        if (state.getNavigationNodes().size() >= 2) {
            state.setCurrentRoute(Router.calculateRoute());
        }
    }

    /**
     * Operation main
     * 
     * @param args
     *            -
     * @return
     */
    public static void main(String[] args) {
        PropertyConfigurator.configure("config/log4j.conf");
        Controller ctrl = new Controller();
        File stateFile = new File("./state.state");
        if (stateFile.exists()) {    
            try {
                StateIO.loadState(stateFile);
            } catch (IOException e) {
                logger.error("Read error occurred when loading state. Aborting...");
                return;
            }
        } else {
            logger.warn("No state file found. Go on with loading map of Karlsruhe");
            File karlsruheMap = new File("test/resources/karlsruhe_small_current.osm");
            if(karlsruheMap.exists()) {
                logger.info("file exists");
                OSMLoader osmLoader = new OSMLoader();
                osmLoader.importMap(karlsruheMap);
                ctrl.state.getLoadedMapInfo().buildZoomlevels();
                ctrl.setViewToMapCenter();
                ctrl.guiHandler.createGUI(ctrl.state.getCenterCoordinate());
            } else {
                logger.warn("Not even KarlsruheMap found. Going on without loading map."); //TODO not loading map 
            }
        }
        ctrl.guiHandler.addListenerAddNavNode(new TargetSelectedListener(ctrl));
        ctrl.guiHandler.addChangedViewListener(new ViewChangedListener(ctrl));
        ctrl.guiHandler.addListenerImportMap(new ImportOsmFileListener(ctrl));
    }
}
