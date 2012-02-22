package kit.route.a.lot.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;

import kit.route.a.lot.common.Progress;
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
    
    public boolean equals(Object other) {
        if(other == this) {
            return true;
        }
        if(!(other instanceof GraphMock)) {
            return false;
        }
        GraphMock comparee = (GraphMock) other;
        return java.util.Arrays.equals(startIDs, comparee.startIDs)
                && java.util.Arrays.equals(endIDs, comparee.endIDs)
                && java.util.Arrays.equals(weights, comparee.weights);
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
        if (stream == null) {
            throw new IllegalArgumentException();
        }
        int nodeCount = stream.readInt();
        for (int i = 0; i < nodeCount; i++) {
            startIDs[i] = stream.readInt();         
            endIDs[i] = stream.readInt(); 
            weights[i] = stream.readInt(); 
        }
    }

    @Override
    public void saveToStream(DataOutputStream stream) throws IOException {  
        if (stream == null) {
            throw new IllegalArgumentException();
        }
        stream.writeInt(startIDs.length);
        for (int i = 0; i < startIDs.length; i++) {
            stream.writeInt(startIDs[i]);
            stream.writeInt(endIDs[i]);
            stream.writeInt(weights[i]);
        }
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
