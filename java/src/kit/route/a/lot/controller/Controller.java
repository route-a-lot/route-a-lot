package kit.route.a.lot.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;


import kit.route.a.lot.common.Context;
import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.POIDescription;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.common.Util;
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
import kit.route.a.lot.map.infosupply.ComplexInfoSupplier;
import kit.route.a.lot.map.rendering.Renderer;
import kit.route.a.lot.map.Node;
import kit.route.a.lot.routing.Router;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Controller {

    private Renderer renderer;
    private GUIHandler guiHandler;
    private State state;

    private static Logger logger = Logger.getLogger(Controller.class);
    
    
    private Controller() {
        renderer = new Renderer();
        guiHandler = new GUIHandler();
        state = State.getInstance();
    }
        
    
    public void setViewToMapCenter() {
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
            } catch (IOException e) {
                logger.fatal("loadMap: IO Exception in MapIO");
            }
            setViewToMapCenter(); // TODO: likely incorrect place
            //guiHandler.setView(state.getCenterCoordinates());
            renderer.resetRenderCache();
        }
    }

    /**
     * Operation importMap
     * 
     * @return
     */
    public void importMap(File osmFile) {
        if(!osmFile.exists()) {
            logger.error("osm File doesn't exist");
        } else {
            state.resetMap();
            new OSMLoader().importMap(osmFile);
            state.getLoadedMapInfo().buildZoomlevels();
            state.getLoadedMapInfo().trimm();
            setViewToMapCenter();
            //guiHandler.setView(state.getCenterCoordinates());
            renderer.resetRenderCache();
            state.setLoadedMapFile(new File(Util.removeExtension(osmFile.getPath()) + ".sral"));
            state.getImportedMaps().add(Util.removeExtension(osmFile.getPath()) + ".sral");
            //guiHandler.updateMapList(state.getImportedMaps());
            try {
                MapIO.saveMap(state.getLoadedMapFile());
            } catch (IOException e) {
                logger.error("Could not save imported map to file.");
                e.printStackTrace();
            }
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
        Selection newSel = state.getLoadedMapInfo().select(pos);
        if (newSel != null) {
            state.getNavigationNodes().add(position,
                    state.getLoadedMapInfo().select(pos));
        }
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
        guiHandler.updateGUI();
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
                guiHandler.deleteNavNodeFromList(i);
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
            Collections.swap(state.getNavigationNodes(), one, two);
            calculateRoute();
        }
    }

    /**
     * Operation orderNavNodes
     * 
     * @return
     */
    public void orderNavNodes() {  
        Collection<Selection> col = Router.optimizeRoute();         
        guiHandler.setNavNodesOrder(new ArrayList<Integer>());   //TODO make this list
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
        guiHandler.updateGUI();
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

//    /**
//     * Operation searchPOI
//     * 
//     * @return
//     */
//    public void searchPOI() {   //TODO needed?
//    }
//
//    /**
//     * Operation searchFavorite
//     * 
//     * @return
//     */
//    public void searchFavorite() {  //TODO needed?
//    }

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
    
    public void whatWasClicked(Coordinates pos) {
        for (int i = 0; i < state.getNavigationNodes().size(); i++) {
            Node node = new Node(state.getNavigationNodes().get(i).getPosition());
            Coordinates topLeft = new Coordinates();
            Coordinates bottomRight = new Coordinates();
            topLeft.setLatitude(pos.getLatitude() - state.getClickRadius());
            topLeft.setLongitude(pos.getLatitude() - state.getClickRadius());
            bottomRight.setLatitude(pos.getLatitude() + state.getClickRadius());
            topLeft.setLongitude(pos.getLatitude() + state.getClickRadius());
            if (node.isInBounds(topLeft, bottomRight)) {
                //TODO tell gui
                return;
            }
        }
        if (state.getLoadedMapInfo().getPOIDescription(pos, state.getClickRadius()) != null) {
            //TODO tell GUI
            return;
        } 
        //TODO tell gui
        
    }

    /**
     * Operation getPOIInfo
     * 
     * @return
     */
    public void getPOIInfo(Coordinates pos) {   
        POIDescription info = state.getLoadedMapInfo().getPOIDescription(pos, state.getClickRadius());
        //TODO tell gui
    }

    /**
     * Operation showTextRoute
     * 
     * @return
     */
    public void showTextRoute() {   //TODO
        if (state.getCurrentRoute().size() != 0) {
            ComplexInfoSupplier cIS = new ComplexInfoSupplier();
            cIS.getRouteDescription(state.getCurrentRoute());
        }
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
            try {
                state.setCurrentRoute(Router.calculateRoute());
                for(Integer inte : state.getCurrentRoute()) {
                    System.out.println(inte);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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
        File defaultMap = new File("./test/resources/karlsruhe_small_current.osm");
        if (stateFile.exists()) {   
            logger.info("Load state file..."); 
            try { 
                StateIO.loadState(stateFile); 
            } catch (IOException e) {
                logger.error("State loading: Read error occurred.");
                e.printStackTrace();
            }
            ctrl.loadMap(ctrl.state.getLoadedMapFile());
        } else {
            if (defaultMap.exists()) {
                logger.info("Import default map...");
                ctrl.importMap(defaultMap);
                try {
                    StateIO.saveState(stateFile); // TODO: move saveState call to program exit
                } catch (IOException e) {
                    logger.error("State saving: Write error occurred.");
                    e.printStackTrace();
                }
            } else {
                logger.warn("No map loaded."); //TODO not loading map 
            }
        }
        
        ctrl.guiHandler.createGUI(ctrl.state.getCenterCoordinates());
        ctrl.guiHandler.addListenerAddNavNode(new TargetSelectedListener(ctrl));
        ctrl.guiHandler.addChangedViewListener(new ViewChangedListener(ctrl));
        ctrl.guiHandler.addListenerImportMap(new ImportOsmFileListener(ctrl));
                
    }
}
