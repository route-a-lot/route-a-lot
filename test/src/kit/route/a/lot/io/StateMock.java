package kit.route.a.lot.io;

import kit.route.a.lot.controller.State;
import kit.route.a.lot.map.infosupply.MapInfo;
import kit.route.a.lot.routing.RoutingGraph;


class StateMock extends State {
    
    private GraphMock graph;
    private MapInfoMock mapInfo;

    public StateMock() {
        mapInfo = new MapInfoMock();
        graph = new GraphMock();
    }
    
    @Override
    public RoutingGraph getLoadedGraph() {
        return graph;
    }
    
    @Override
    public MapInfo getLoadedMapInfo() {
        return mapInfo;
    }
    
    int getGraphStartIDsSize() {
        return graph.startIDs.length;
    }
    
    int getGraphEndIDsSize() {
        return graph.endIDs.length;
    }
    
    int getGraphWeightsSize() {
        return graph.weights.length;
    }
    
    long getNodeCount() {
        return mapInfo.nodeCount;
    }

}
