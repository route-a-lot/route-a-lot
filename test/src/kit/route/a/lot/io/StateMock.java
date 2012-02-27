package kit.route.a.lot.io;

import kit.route.a.lot.common.RouteDescription;
import kit.route.a.lot.controller.State;
import kit.route.a.lot.map.infosupply.MapInfo;
import kit.route.a.lot.routing.RoutingGraph;


class StateMock extends State {
    
    private GraphMock loadedGraph;
    private MapInfoMock loadedMapInfo;

    public StateMock() {
        loadedMapInfo = new MapInfoMock();
        loadedGraph = new GraphMock();
    }
    
    public boolean equals(Object other) {
        if(other == this) {
            return true;
        }
        if(!(other instanceof State)) {
            return false;
        }
        State comparee = (State) other;
        return super.equals(other);
    }
    
    @Override
    public RoutingGraph getLoadedGraph() {
        return loadedGraph;
    }
    
    @Override
    public MapInfo getMapInfo() {
        return loadedMapInfo;
    }
    
    int getGraphStartIDsSize() {
        return loadedGraph.startIDs.length;
    }
    
    int getGraphEndIDsSize() {
        return loadedGraph.endIDs.length;
    }
    
    int getGraphWeightsSize() {
        return loadedGraph.weights.length;
    }
    
    long getNodeCount() {
        return loadedMapInfo.nodeCount;
    }
    
    long getWayCount() {
        return loadedMapInfo.wayCount;
    }

}
