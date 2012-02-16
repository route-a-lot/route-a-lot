package kit.route.a.lot.routing;

import java.io.DataInputStream;import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;


public class AdjacentFieldsRoutingGraphSimple implements RoutingGraph {
    
    private int[] edgesPos;
    private int[] edges;
    private int[] weights;

    @Override
    public void buildGraph(int[] startID, int[] endID, int[] weight, int maxNodeID) {
        if (startID == null || endID == null || weight == null ||
            startID.length != endID.length || endID.length != weight.length) {
            return;    //not possible to build graph
        }
        
        boolean sorted = false;
        int temp;
        while (!sorted){
           sorted = true;
           for (int i = 0; i < startID.length - 1; i++) 
              if (startID[i] > startID[i+1]) {                      
                 
                 temp       = startID[i];
                 startID[i] = startID[i+1];
                 startID[i+1] = temp;
                 
                 temp = endID[i];
                 endID[i] = endID[i+1];
                 endID[i+1] = temp;
                 
                 temp = weight[i];
                 weight[i] = weight[i+1];
                 weight[i+1] = temp;
                 
                 sorted = false;
              }
         } 
    
        edgesPos = new int[maxNodeID + 2];
        edgesPos[0] = 0;
        int index;
        
        for (int i = 0; i <= maxNodeID; i++) {
            index = edgesPos[i];
            for (int j = index; j < startID.length && startID[j] == i; j++) {
                index++;
            }
            edgesPos[i + 1] = index;
        }
        edges = endID.clone();
        weights = weight.clone();
        if(edges == null || weight == null || edgesPos == null) {
            System.err.println("warum?");
        }
    }
    
    
    //following functions are tested in another way
    @Override
    public int getWeight(int first, int last) {
        // TODO Auto-generated method stub
        return 0;
    }
    @Override
    public Collection<Integer> getRelevantNeighbors(int node, byte[] areas) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public RoutingGraph getInverted() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public void loadFromStream(DataInputStream stream) throws IOException {
        // TODO Auto-generated method stub
    }
    @Override
    public void saveToStream(DataOutputStream stream) throws IOException {
        // TODO Auto-generated method stub
    }
    @Override
    public byte getAreaID(int node) {
        // TODO Auto-generated method stub
        return 0;
    }
    @Override
    public void setAreaID(int node, byte id) {
        // TODO Auto-generated method stub
    }
    @Override
    public int getIDCount() {
        // TODO Auto-generated method stub
        return 0;
    }
    @Override
    public Collection<Integer> getAllNeighbors(int node) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public void setArcFlag(int node, int node2, byte area) {
        // TODO Auto-generated method stub

    }
    @Override
    public String getMetisRepresentation() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public void readAreas(String areas) {
        // TODO Auto-generated method stub
    }
    @Override
    public void buildGraphWithUniqueEdges(int[] uniqueEdgeStartIDs, int[] uniqueEdgeEndIDs, int maxWayNodeId) {
        // TODO Auto-generated method stub
    }


    @Override
    public int[] getStartIDArray() {
        return edgesPos.clone();
    }


    @Override
    public int[] getEdgesArray() {
        return edges.clone();
    }


    @Override
    public int[] getWeightsArray() {
        return weights.clone();
    }
}