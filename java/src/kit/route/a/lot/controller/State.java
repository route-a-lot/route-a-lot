package kit.route.a.lot.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import kit.route.a.lot.map.infosupply.MapInfo;
import kit.route.a.lot.map.rendering.Renderer;
import kit.route.a.lot.heightinfo.Heightmap;
import kit.route.a.lot.heightinfo.IHeightmap;
import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.RouteDescription;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.routing.AdjacentFieldsRoutingGraph;
import kit.route.a.lot.routing.RoutingGraph;

public class State {

    private ArrayList<String> importedMaps = new ArrayList<String>(); /*
                                                                       * we need this list for the to know
                                                                       * which maps can be loaded, I would
                                                                       * save patch, names will be saved
                                                                       * implicit
                                                                       */

    private static State singleton = null;
    /** Attributes */
    /**
     * 
     */
    private File loadedMapFile;
    /**
     * 
     */
    private MapInfo loadedMapInfo;
    /**
     * 
     */
    private RoutingGraph loadedGraph;
    /**
     * 
     */
    private IHeightmap loadedHeightmap;
    /**
     * 
     */
    private List<Selection> navigationNodes;
    /**
     * 
     */
    private List<Integer> currentRoute;
    /**
     * 
     */
    private Coordinates centerCoordinates;
    /**
     * 
     */
    private int detailLevel;
    /**
     * 
     */
    private int clickRadius; // TODO needed?
    /**
     * 
     */
    private RouteDescription routeDescription;
    /**
     * 
     */
    private int speed;
    /**
     * 
     */
    private int duration;
    /**
     * 
     */
    private int heightMalus;
    /**
     * 
     */
    private int highwayMalus;

    /**
     * Operation getInstance
     * 
     * @return State
     */
    public static State getInstance() {
        if (singleton == null) {
            singleton = new State();
        }
        return singleton;
    }


    public State() {
        loadedMapFile = null;
        loadedMapInfo = new MapInfo();
        loadedGraph = new AdjacentFieldsRoutingGraph();
        loadedHeightmap = new Heightmap();
        navigationNodes = new ArrayList<Selection>();
        currentRoute = new ArrayList<Integer>();
        centerCoordinates = null;
        detailLevel = 2;
        clickRadius = 80; // TODO use it
        routeDescription = new RouteDescription();
        speed = 15;
        duration = 0;
        heightMalus = 0;
        highwayMalus = 0;
        importedMaps = new ArrayList<String>();
    }


    public ArrayList<String> getImportedMaps() {
        return importedMaps;
    }


    public void setImportedMaps(ArrayList<String> importedMaps) {
        this.importedMaps = importedMaps;
    }


    public File getLoadedMapFile() {
        return this.loadedMapFile;
    }


    public void setLoadedMapFile(File loadedMapFile) {
        this.loadedMapFile = loadedMapFile;
    }


    public MapInfo getLoadedMapInfo() {
        return loadedMapInfo;
    }


    public void setLoadedMapInfo(MapInfo loadedMapInfo) {
        this.loadedMapInfo = loadedMapInfo;
    }


    public RoutingGraph getLoadedGraph() {
        return loadedGraph;
    }


    public void setLoadedGraph(RoutingGraph loadedGraph) {
        this.loadedGraph = loadedGraph;
    }


    public IHeightmap getLoadedHeightmap() {
        return loadedHeightmap;
    }


    public void setLoadedHeightmap(IHeightmap loadedHeightmap) {
        this.loadedHeightmap = loadedHeightmap;
    }


    public List<Selection> getNavigationNodes() {
        return navigationNodes;
    }


    public void setNavigationNodes(List<Selection> navigationNodes) {
        this.navigationNodes = navigationNodes;
    }


    public List<Integer> getCurrentRoute() {
        return currentRoute;
    }


    public void setCurrentRoute(List<Integer> currentRoute) {
        this.currentRoute = currentRoute;
    }


    public Coordinates getCenterCoordinates() {
        return centerCoordinates;
    }


    public void setCenterCoordinates(Coordinates areaCoord) {
        this.centerCoordinates = areaCoord;
    }


    public int getDetailLevel() {
        return detailLevel;
    }


    public void setDetailLevel(int detailLevel) {
        this.detailLevel = detailLevel;
    }


    public int getClickRadius() {
        return clickRadius;
    }


    public void setClickRadius(int clickRadius) {
        this.clickRadius = clickRadius;
    }


    public RouteDescription getRouteDescription() {
        return routeDescription;
    }


    public void setRouteDescription(RouteDescription routeDescription) {
        this.routeDescription = routeDescription;
    }


    public int getSpeed() {
        return speed;
    }


    public void setSpeed(int speed) {
        this.speed = speed;
    }


    public int getDuration() {
        return duration;
    }


    public void setDuration(int duration) {
        this.duration = duration;
    }


    public int getHeightMalus() {
        return heightMalus;
    }


    public void setHeightMalus(int heightMalus) {
        this.heightMalus = heightMalus;
    }


    public int getHighwayMalus() {
        return highwayMalus;
    }


    public void setHighwayMalus(int heighwayMalus) {
        this.highwayMalus = heighwayMalus;
    }


    public Heightmap getHeightMap() {
        // TODO Auto-generated method stub
        return null;
    }

    public void resetMap() {
        loadedMapFile = null;
        loadedMapInfo = new MapInfo();
        loadedGraph = new AdjacentFieldsRoutingGraph();
        navigationNodes = new ArrayList<Selection>();
        currentRoute = new ArrayList<Integer>();
        centerCoordinates = new Coordinates();
        detailLevel = 2;
        routeDescription = new RouteDescription();
    }
}
