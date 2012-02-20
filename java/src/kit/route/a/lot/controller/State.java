package kit.route.a.lot.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.RouteDescription;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.heightinfo.Heightmap;
import kit.route.a.lot.heightinfo.IHeightmap;
import kit.route.a.lot.map.infosupply.MapInfo;
import kit.route.a.lot.map.rendering.Renderer;
import kit.route.a.lot.routing.AdjacentFieldsRoutingGraph;
import kit.route.a.lot.routing.RoutingGraph;

public class State {
    private static State singleton = new State();
    
    private File loadedMapFile;
    private MapInfo loadedMapInfo;
    private RoutingGraph loadedGraph;
    private IHeightmap loadedHeightmap;
    private Renderer activeRenderer;
    private List<Selection> navigationNodes;
    private List<Integer> currentRoute;
    private Coordinates centerCoordinates;
    private int detailLevel;
    private int clickRadius;
    private RouteDescription routeDescription;
    private int speed;
    private int duration;
    private int heightMalus;
    private int highwayMalus;

    public State() {
        detailLevel = 2;
        clickRadius = 10;
        speed = 15;
        duration = 0;
        heightMalus = 0;
        highwayMalus = 0;
        loadedHeightmap = new Heightmap();
        activeRenderer = new Renderer();
        resetMap();
    }
    
    public boolean equals(Object other) {
        if(other == this) {
            return true;
        }
        if(!(other instanceof State)) {
            return false;
        }
        State comparee = (State) other;
        return loadedMapInfo.equals(comparee.getLoadedMapInfo())
                && loadedGraph.equals(comparee.getLoadedGraph())
                && loadedHeightmap.equals(comparee.getLoadedHeightmap())
                && centerCoordinates.equals(comparee.getCenterCoordinates())
                && detailLevel == comparee.getDetailLevel()
                && clickRadius == comparee.getClickRadius()
                && routeDescription.equals(comparee.getRouteDescription())
                && speed == comparee.getSpeed()
                && duration == comparee.getDuration()
                && heightMalus == comparee.getHeightMalus()
                && highwayMalus == comparee.getHighwayMalus();
    }
    
    public void resetMap() {
        loadedMapFile = null;
        loadedMapInfo = new MapInfo();
        loadedGraph = new AdjacentFieldsRoutingGraph();      
        navigationNodes = new ArrayList<Selection>();
        currentRoute = new ArrayList<Integer>();
        centerCoordinates = new Coordinates(0, 0);
        routeDescription = new RouteDescription();    
        activeRenderer.resetCache();
    }
    
    public static State getInstance() {
        return singleton;
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
    
    public Renderer getActiveRenderer() {
        return activeRenderer;
    }

    public void setActiveRenderer(Renderer renderer) {
        this.activeRenderer = renderer;
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

}
