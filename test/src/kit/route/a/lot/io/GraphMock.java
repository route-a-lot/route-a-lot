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
    public void buildGraph(int[] startID, int[] endID, int[] weight) {
        startIDs = startID;
        endIDs = endID;
        weights = weight;
    }

    @Override
    public Collection<Integer> getRelevantNeighbors(int node, byte destArea) {
        // TODO Auto-generated method stub
        return null;
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
    public long getArcFlags(int startID, int endID) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getWeight(int first, int last) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getIDCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public RoutingGraph getInverted() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setArcFlags(int startID, int endID, long flags) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Collection<Integer> getRelevantNeighbors(int node, byte[] bs) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<Integer> getAllNeighbors(int node) {
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
    public void setArcFlag(int node, int node2, byte area) {
        // TODO Auto-generated method stub
        
    }
    
}
