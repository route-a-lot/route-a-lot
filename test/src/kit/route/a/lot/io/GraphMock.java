package kit.route.a.lot.io;

import java.io.InputStream;
import java.io.OutputStream;
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
    public void loadFromStream(InputStream stream) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void saveToStream(OutputStream stream) {
        // TODO Auto-generated method stub
        
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
    public void setArcFlags(int startID, int endID, int flags) {
        // TODO Auto-generated method stub
        
    }

    
}
