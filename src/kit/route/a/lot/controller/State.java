package kit.route.a.lot.controller;

import kit.route.a.lot.map.infosupply.MapInfo;
import kit.route.a.lot.heightinfo.IHeightmap;
import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.RouteDescription;

public class State

{
    /** Attributes */
    /**
     * 
     */
    public String loadedMapName;
    /**
     * 
     */
    public MapInfo loadedMapInfo;
    /**
     * 
     */
    public Graph loadedGraph;
    /**
     * 
     */
    public IHeightmap loadedHeightmap;
    /**
     * 
     */
    public List<Selection> navigationNodes;
    /**
     * 
     */
    public List<int> currentRoute;
    /**
     * 
     */
    public Coordinates areaCoord;
    /**
     * 
     */
    public int detailLevel;
    /**
     * 
     */
    public int clickRadius;
    /**
     * 
     */
    public RouteDescription routeDescription;
    /**
     * 
     */
    public int speed;
    /**
     * 
     */
    public int duration;
    /**
     * 
     */
    public int heightMalus;
    /**
     * 
     */
    public int heighwayMalus;
    /**
     * Operation getInstance
     *
     * @return State
     */
    public static State getInstance (  ){}
}

