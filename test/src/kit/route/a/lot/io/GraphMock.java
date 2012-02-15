package kit.route.a.lot.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;

import kit.route.a.lot.routing.RoutingGraph;


public class GraphMock implements RoutingGraph {
    
    int[] startIDs, endIDs;
    int[] weights;

    @Override
    public void buildGraph(int[] startID, int[] endID, int[] weight, int maxId) {
        startIDs = startID;
        endIDs = endID;
        weights = weight;
    }

    @Override
    public byte getAreaID(int node) {
        return 0;
    }

    @Override
    public void setAreaID(int node, byte id) {       
    }

    @Override
    public int getWeight(int first, int last) {
        return 0;
    }

    @Override
    public int getIDCount() {
        return 0;
    }

    @Override
    public RoutingGraph getInverted() {
        return null;
    }

    @Override
    public Collection<Integer> getRelevantNeighbors(int node, byte[] bs) {
        return null;
    }

    @Override
    public Collection<Integer> getAllNeighbors(int node) {
        return null;
    }

    @Override
    public void loadFromStream(DataInputStream stream) throws IOException {
      
    }

    @Override
    public void saveToStream(DataOutputStream stream) throws IOException {        
    }

    @Override
    public void setArcFlag(int node, int node2, byte area) {       
    }

    @Override
    public String getMetisRepresentation() {
        return null;
    }

    @Override
    public void readAreas(String areas) {
    }

    @Override
    public void buildGraphWithUniqueEdges(int[] uniqueEdgeStartIDs, int[] uniqueEdgeEndIDs, int maxWayNodeId) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int[] getStartIDArray() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int[] getEdgesArray() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int[] getWeightsArray() {
        // TODO Auto-generated method stub
        return null;
    }
    
}
