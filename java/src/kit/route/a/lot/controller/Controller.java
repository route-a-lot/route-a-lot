package kit.route.a.lot.controller;

import java.io.File;

import kit.route.a.lot.common.Context;
import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.gui.GUIHandler;
import kit.route.a.lot.io.OSMLoader;
import kit.route.a.lot.io.StateIO;
import kit.route.a.lot.map.rendering.Renderer;
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
        guiHandler.updateMap(); // TODO duration
    }

    /**
     * Operation setZoomLevel
     * 
     * @return
     */
    public void setZoomLevel() {
    }

    /**
     * Operation toggle3D
     * 
     * @return
     */
    public void toggle3D() {
    }

    /**
     * Operation loadMap
     * 
     * @return
     */
    public void loadMap() {
    }

    /**
     * Operation importMap
     * 
     * @return
     */
    public void importMap() {
    }

    /**
     * Operation addNavNode
     * 
     * @return
     */
    public void addNavNode(Coordinates pos, int position, Context context) {
        State state = State.getInstance();
        state.getNavigationNodes().add(position,
                state.getLoadedMapInfo().select(pos));
        calculateRoute();
        render(context, -1);
    }

    /**
     * Operation deleteNavNode
     * 
     * @return
     */
    public void deleteNavNode() {
    }

    /**
     * Operation switchNavNodes
     * 
     * @return
     */
    public void switchNavNodes() {
    }

    /**
     * Operation orderNavNodes
     * 
     * @return
     */
    public void orderNavNodes() {
    }

    /**
     * Operation addFavorite
     * 
     * @return
     */
    public void addFavorite() {
    }

    /**
     * Operation deleteFavorite
     * 
     * @return
     */
    public void deleteFavorite() {
    }

    /**
     * Operation saveRoute
     * 
     * @return
     */
    public void saveRoute() {
    }

    /**
     * Operation loadRoute
     * 
     * @return
     */
    public void loadRoute() {
    }

    /**
     * Operation exportRoute
     * 
     * @return
     */
    public void exportRoute() {
    }

    /**
     * Operation printRoute
     * 
     * @return
     */
    public void printRoute() {
    }

    /**
     * Operation typeAddress
     * 
     * @return
     */
    public void typeAddress() {
    }

    /**
     * Operation searchAddress
     * 
     * @return
     */
    public void searchAddress() {
    }

    /**
     * Operation searchPOI
     * 
     * @return
     */
    public void searchPOI() {
    }

    /**
     * Operation searchFavorite
     * 
     * @return
     */
    public void searchFavorite() {
    }

    /**
     * Operation setSpeed
     * 
     * @return
     */
    public void setSpeed() {
    }

    /**
     * Operation getPOIInfo
     * 
     * @return
     */
    public void getPOIInfo() {
    }

    /**
     * Operation showTextRoute
     * 
     * @return
     */
    public void showTextRoute() {
    }

    /**
     * Operation setHeightMalus
     * 
     * @return
     */
    public void setHeightMalus() {
    }

    /**
     * Operation setHighwayMalus
     * 
     * @return
     */
    public void setHighwayMalus() {
    }

    /**
     * Operation importHeightMap
     * 
     * @return
     */
    public void importHeightMap() {
    }

    /**
     * Operation render
     * 
     * @return
     */
    public void render(Context context, int zoomLevel) {
        if(zoomLevel != -1) {
            State.getInstance().setDetailLevel(zoomLevel);
        }
        renderer.render(context, State.getInstance().getDetailLevel()); 
        setView();
    }

    /**
     * Operation calculateRoute
     * 
     * @return
     */
    public void calculateRoute() {
        State state = State.getInstance();
        if (state.getNavigationNodes().size() >= 2) {
            Router.calculateRoute();
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
            StateIO.loadState(stateFile);
        } else {
            logger.warn("No state file found. Go on withloading map of Karlsruhe");
            File karlsruheMap = new File("test/resources/karlsruhe_small.osm");
            if(karlsruheMap.exists()) {
                logger.info("file exists");
                OSMLoader osmLoader = new OSMLoader();
                osmLoader.importMap(karlsruheMap);
                ctrl.guiHandler.createGUI(ctrl.state.getAreaCoord());
            } else {
                logger.warn("Not even KarlsruheMap found. Going on without loading map."); //TODO not loading map 
            }
        }
        ctrl.guiHandler.addListenerAddNavNode(new TargetSelectedListener(ctrl));
        ctrl.guiHandler.addChangedViewListener(new ViewChangedListener(ctrl));
    }
}
