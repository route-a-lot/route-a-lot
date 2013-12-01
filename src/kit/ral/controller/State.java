
/**
Copyright (c) 2012, Matthias Grundmann, Malte Wolff, Jan Jacob, Daniel Krauß, Josua Stabenow
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
        * Redistributions of source code must retain the above copyright
          notice, this list of conditions and the following disclaimer.
        * Redistributions in binary form must reproduce the above copyright
          notice, this list of conditions and the following disclaimer in the
          documentation and/or other materials provided with the distribution.
        * The names of the contributors may not be used to endorse or promote products
          derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
**/

package kit.ral.controller;

import kit.ral.common.Coordinates;
import kit.ral.common.Selection;
import kit.ral.common.description.RouteDescription;
import kit.ral.heightinfo.HashHeightmap;
import kit.ral.heightinfo.Heightmap;
import kit.ral.map.info.MapInfo;
import kit.ral.map.rendering.Renderer;
import kit.ral.routing.AdjacentFieldsRoutingGraph;
import kit.ral.routing.RoutingGraph;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class State {
    private static State singleton = new State();
    
    private File loadedMapFile;
    private MapInfo loadedMapInfo;
    private RoutingGraph loadedGraph;
    private Heightmap loadedHeightmap = new HashHeightmap();
    private Renderer activeRenderer = new Renderer();
    private List<Selection> navigationNodes;
    private List<Integer> currentRoute;
    private Coordinates centerCoordinates = new Coordinates(0, 0);
    private int detailLevel = 4;
    private int clickRadius = 10;
    private RouteDescription routeDescription;
    private int speed = 15;
    private int duration = 0;
    private int heightMalus = 0;
    private int highwayMalus = 0;

    public State() {
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
        return navigationNodes.equals(comparee.navigationNodes)
                && currentRoute.equals(comparee.currentRoute)
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


    public MapInfo getMapInfo() {
        return loadedMapInfo;
    }

    public void setMapInfo(MapInfo mapInfo) {
        this.loadedMapInfo = mapInfo;
    }


    public RoutingGraph getLoadedGraph() {
        return loadedGraph;
    }

    public void setLoadedGraph(RoutingGraph loadedGraph) {
        this.loadedGraph = loadedGraph;
    }


    public Heightmap getLoadedHeightmap() {
        return loadedHeightmap;
    }

    public void setLoadedHeightmap(Heightmap loadedHeightmap) {
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

    public void setCenterCoordinates(Coordinates center) {
        this.centerCoordinates = center;
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
