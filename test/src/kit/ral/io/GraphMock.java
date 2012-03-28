package kit.ral.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;

import kit.ral.routing.RoutingGraph;


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
    public void loadFromInput(DataInput input) throws IOException {
        if (input == null) {
            throw new IllegalArgumentException();
        }
        int nodeCount = input.readInt();
        for (int i = 0; i < nodeCount; i++) {
            startIDs[i] = input.readInt();         
            endIDs[i] = input.readInt(); 
            weights[i] = input.readInt(); 
        }
    }

    @Override
    public void saveToOutput(DataOutput output) throws IOException {  
        if (output == null) {
            throw new IllegalArgumentException();
        }
        output.writeInt(startIDs.length);
        for (int i = 0; i < startIDs.length; i++) {
            output.writeInt(startIDs[i]);
            output.writeInt(endIDs[i]);
            output.writeInt(weights[i]);
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
    public void buildGraphWithUndirectedEdges(int[] uniqueEdgeStartIDs, int[] uniqueEdgeEndIDs, int maxWayNodeId) {
    }

    @Override
    public int[] getStartIDArray() {
        return null;
    }

    @Override
    public int[] getEdgesArray() {
        return null;
    }

    @Override
    public int[] getWeightsArray() {
        return null;
    }

    @Override
    public void setAllArcFlags() {
    }
    
}