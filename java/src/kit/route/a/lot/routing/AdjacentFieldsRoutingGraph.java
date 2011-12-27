package kit.route.a.lot.routing;

import java.io.InputStream;
import java.lang.Math;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.LinkedList;

import kit.route.a.lot.common.IntTuple;


public class AdjacentFieldsRoutingGraph implements RoutingGraph {

    /** Attributes */
    /**
     * 
     */
    private int[] edgesPos;
    /**
     * 
     */
    private byte[] areaID;
    /**
     * 
     */
    private int[] edges;
    /**
     * 
     */
    private int[] weights;
    /**
     * 
     */
    private long[] arcFlags;

    @Override
    public void buildGraph(int[] startID, int[] endID, int[] weight) {
        int max = 0;
        for (int id: startID) {
            // Get maxID = edgesPos.size = edgeList.size
            max = Math.max(max, id);
        }
        // "You can't use generic array creation. It's a flaw/ feature of java generics." well, that sucks.
        ArrayList<LinkedList<IntTuple>> edgeLists = new ArrayList<LinkedList<IntTuple>>();
        Arrays.fill(edgesPos, 0);
        for (int i = 0; i < startID.length; i++) {
            edgeLists.get(startID[i]).add(new IntTuple(endID[i], weight[i]));
            // Create Mapping from ID => edge
        }
        int j = 0;  // Index of edges and weights
        int i = 1;  // Index of edgeLists and edgesPos
        for (LinkedList<IntTuple> edgeList: edgeLists) {
            // Fill Arrays
            for (IntTuple values: edgeList) {
                edgesPos[i]++;
                edges[j] = values.getFirst();
                weights[j] = values.getLast();
                j++;
            }
            i++;
        }

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
        LinkedList<Integer> relevantEdges = new LinkedList<Integer>();
        for (int i = edgesPos[node]; i < edgesPos[node+1]; i++) {
            relevantEdges.add(edges[edgesPos[node]+i]);
        }
        return relevantEdges;
    }

    @Override
    public byte getAreaID(int node) {
        return areaID[node];
    }

    @Override
    public void setAreaID(int node, byte id) {
        areaID[node] = id;
    }

    @Override
    public long getArcFlags(int startID, int endID) {
        return ~((long) 0);
    }

    @Override
    public void setArcFlags(int startID, int endID, int flags) {
        // TODO Auto-generated method stub
    }

    public int getWeight(int from, int to) {
        for (int i = edgesPos[from]; i < edgesPos[from+1]; i++) {
            if (edges[i] == to) {
                return weights[i];
            }
        }
        return 0;
    }
}
