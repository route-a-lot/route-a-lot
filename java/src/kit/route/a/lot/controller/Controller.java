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

import kit.route.a.lot.controller.listener.AddFavoriteListener;
import kit.route.a.lot.controller.listener.ClickPositionListener;
import kit.route.a.lot.controller.listener.CloseListener;
import kit.route.a.lot.controller.listener.DeleteNavNodeListener;
import kit.route.a.lot.controller.listener.ExportRouteListener;
import kit.route.a.lot.controller.listener.HeightMalusListener;
import kit.route.a.lot.controller.listener.HighwayMalusListener;
import kit.route.a.lot.controller.listener.ImportOsmFileListener;
import kit.route.a.lot.controller.listener.LoadMapListener;
import kit.route.a.lot.controller.listener.OrderNavNodesListener;
import kit.route.a.lot.controller.listener.SelectNavNodeListener;
import kit.route.a.lot.controller.listener.LoadRouteListener;
import kit.route.a.lot.controller.listener.SpeedListener;
import kit.route.a.lot.controller.listener.SaveRouteListner;
import kit.route.a.lot.controller.listener.ChangeViewListener;
import kit.route.a.lot.gui.GUI;
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
            state.getLoadedMapInfo().buildZoomlevels();
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
            guiHandler.updateMapList(state.getImportedMaps());
        }
       
    }

    /**
     * Operation addNavNode
     * 
     * @return
     */
    public void addNavNode(Coordinates pos, int position) {
        //if (position < state.getNavigationNodes().size()) {   TODO ass soon as gui functionate 
        //    state.getNavigationNodes().remove(position);
        //}
        Selection newSel = state.getLoadedMapInfo().select(pos);
        if (newSel != null) {
            state.getNavigationNodes().add(position,
                    state.getLoadedMapInfo().select(pos));
        }
        calculateRoute();
        //TODO duration
        //render(context, state.getDetailLevel());
        guiHandler.updateGUI();
    }

    /**
     * Operation deleteNavNode
     */
    public void deleteNavNode(int pos) {
        if (pos < state.getNavigationNodes().size()) {
            state.getNavigationNodes().remove(pos);
            state.setCurrentRoute(new ArrayList<Integer>());
            calculateRoute();
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
                //guiHandler.deleteNavNodeFromList(i);
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
        Collection<Selection> col = Router.optimizeRoute();  
        Router.calculateRoute();
        //guiHandler.setNavNodesOrder(new ArrayList<Integer>());   //TODO make this list
        state.setNavigationNodes(new ArrayList<Selection>());
        state.getNavigationNodes().addAll(col);
    }

    /**
     * Operation addFavorite
     */
    public void addFavorite(Coordinates pos, String name, String description) {  
        state.getLoadedMapInfo().addFavorite(pos, new POIDescription(name, 0, description));  //TODO category
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
        File routeFile = new File(path);
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

//    /**
//     * Operation searchPOI
//     */
//    public void searchPOI() {   //TODO needed?
//    }
//
//    /**
//     * Operation searchFavorite
//     */
//    public void searchFavorite() {  //TODO needed?
//    }

    /**
     * Operation setSpeed
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
                guiHandler.thisWasClicked(GUI.NAVNODE, pos);
                return;
            }
        }    
        if (state.getLoadedMapInfo().getPOIDescription(pos, state.getClickRadius()) != null) {
            guiHandler.thisWasClicked(GUI.POI, pos);
            return;
        } 
        if(state.getLoadedMapInfo().isFavorite(pos)) {
            guiHandler.thisWasClicked(GUI.FAVORITE, pos);
        }
        guiHandler.thisWasClicked(GUI.FREEMAPSPACE, pos);
    }

    /**
     * Operation getPOIInfo
     */
    public void getPOIInfo(Coordinates pos) {   
        POIDescription info = state.getLoadedMapInfo().getPOIDescription(pos, state.getClickRadius());
        //TODO tell gui
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
                //for(Integer inte : state.getCurrentRoute()) {
                    // System.out.println(inte);
                //}
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
    
    /**
     * Operation main
     * 
     * @param args :-)
     */
    public static void main(String[] args) {
        
        PropertyConfigurator.configure("config/log4j.conf");
        Controller ctrl = new Controller();
        File defaultMap = new File("./test/resources/karlsruhe_big.osm");
        loadState();
        if (false && ctrl.state.getLoadedMapFile() != null && ctrl.state.getLoadedMapFile().exists()) {
              ctrl.loadMap(ctrl.state.getLoadedMapFile());
        } else {
            if (defaultMap.exists()) {
                logger.info("import default map...");
                ctrl.importMap(defaultMap);
                ctrl.setViewToMapCenter();
                try {
                    StateIO.saveState(new File("./state.state")); // TODO: move saveState call to program exit
                } catch (IOException e) {
                    logger.error("state saving: Write error occurred.");
                    e.printStackTrace();
                }
            } else {
                logger.warn("no map loaded."); //TODO not loading map 
            }
        }    
        ctrl.guiHandler.addListenerAddNavNode(new SelectNavNodeListener(ctrl));
        ctrl.guiHandler.addChangedViewListener(new ChangeViewListener(ctrl));
        ctrl.guiHandler.addListenerImportMap(new ImportOsmFileListener(ctrl));  
        ctrl.setViewToMapCenter();
        System.out.println(ctrl.state.getImportedMaps().size());
        ctrl.guiHandler.setView(ctrl.state.getCenterCoordinates());
        ctrl.guiHandler.updateMapList(ctrl.state.getImportedMaps());
        ctrl.guiHandler.addOptimizeRouteListener(new OrderNavNodesListener(ctrl));
        ctrl.guiHandler.addDeleteNavNodeListener(new DeleteNavNodeListener(ctrl));
        ctrl.guiHandler.addLoadMapListener(new LoadMapListener(ctrl));
        ctrl.guiHandler.addAddFavoriteListener(new AddFavoriteListener(ctrl));
        ctrl.guiHandler.addSaveRouteListener(new SaveRouteListner(ctrl));
        ctrl.guiHandler.addLoadRouteListener(new LoadRouteListener(ctrl));
        ctrl.guiHandler.addExportRouteListener(new ExportRouteListener(ctrl));
        ctrl.guiHandler.addSetSpeedListener(new SpeedListener(ctrl));
        ctrl.guiHandler.addClickPositionListener(new ClickPositionListener(ctrl));
        ctrl.guiHandler.addHeightMalusListener(new HeightMalusListener(ctrl));
        ctrl.guiHandler.addHighwayMalusListener(new HighwayMalusListener(ctrl));
        ctrl.guiHandler.addCloseListener(new CloseListener(ctrl));
    }
}
