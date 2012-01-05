package kit.route.a.lot.controller;

import kit.route.a.lot.common.Context;
import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.gui.GUIHandler;
import kit.route.a.lot.map.rendering.Renderer;
import kit.route.a.lot.routing.Router;

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
        render(context);
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
    public void render(Context context) {
        renderer.render(context, State.getInstance().getDetailLevel()); // TODO is rendering calculating coordinates?
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
    }
}
