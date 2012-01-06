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
        assert startID.length == endID.length;
        assert endID.length == weight.length;
        int max = 0;
        for (int id: startID) {
            // Get maxID = edgesPos.size = edgeList.size
            max = Math.max(max, id);
        }
        assert max <= startID.length;
        // initialyze Arrays
        edgesPos = new int[max];
        areaID = new byte[max];
        edges = new int[startID.length];
        weights = new int[startID.length];
        arcFlags = new long[startID.length];
        // "You can't use generic array creation. It's a flaw/ feature of java generics." well, that sucks.
        ArrayList<LinkedList<IntTuple>> edgeLists = new ArrayList<LinkedList<IntTuple>>();
        // Create a bucket for each ID with a linked list full of Semi-Edges.// currentPath ALWAYS contains the shortest path to currentPath.getNode() (with regards to Arc-Flags). 
        for (int i = 0; i < startID.length; i++) {
            edgeLists.get(startID[i]).add(new IntTuple(endID[i], weight[i]));
            // Create Mapping from ID => edge
        }
        Arrays.fill(edgesPos, 0);
        int j = 0;  // Index of edges and weights
        int i = 1;  // Index of edgeLists and edgesPos
        for (LinkedList<IntTuple> edgeList: edgeLists) {
            // Fill Arrays
            for (IntTuple values: edgeList) {
                edgesPos[i]++;
                edges[j] = values.getFirst();
                weights[j] = values.getLast();
                arcFlags[j] = ~ ((long) 0);
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
        return getRelevantNeighbors(node, new byte[] {destArea});
    }
    
    
    public Collection<Integer> getRelevantNeighbors(int node, byte[] destAreas) {
        assert node <= edgesPos.length;
        LinkedList<Integer> relevantEdges = new LinkedList<Integer>();
        long flags = 0;
        for (byte area: destAreas) {
            flags |= 1 << area;
        }
        for (int i = edgesPos[node]; i < edgesPos[node+1]; i++) {
            if ((arcFlags[i] | flags) != 0) {
                relevantEdges.add(edges[i]);
            }
        }
        return relevantEdges;
    }
    
    @Override
    public Collection<Integer> getAllNeighbors(int node) {
        assert node <= edgesPos.length;
        LinkedList<Integer> relevantEdges = new LinkedList<Integer>();
        for (int i = edgesPos[node]; i < edgesPos[node+1]; i++) {
            relevantEdges.add(edges[i]);
        }
        return relevantEdges;
    }
    
    @Override
    public byte getAreaID(int node) {
        assert node <= edgesPos.length;
        return areaID[node];
    }

    @Override
    public void setAreaID(int node, byte id) {
        assert node <= edgesPos.length;
        areaID[node] = id;
    }

    @Override
    public long getArcFlags(int startID, int endID) {
        assert startID <= edgesPos.length;
        assert endID <= edgesPos.length;
        for (int i = edgesPos[startID]; i < edgesPos[startID+1]; i++) {
            if (endID == edges[i]) {
                return arcFlags[i];
            }
        }
        return (long) 0;
    }

    @Override
    public void setArcFlags(int startID, int endID, long flags) {
        assert startID <= edgesPos.length;
        assert endID <= edgesPos.length;
        for (int i = edgesPos[startID]; i < edgesPos[startID+1]; i++) {
            if (endID == edges[i]) {
                arcFlags[i] = flags;
                // There might be multiple entries for the same edge, so don't abort yet
                // to make it more robust.
            }
        }
    }

    public int getWeight(int from, int to) {
        for (int i = edgesPos[from]; i < edgesPos[from+1]; i++) {
            if (edges[i] == to) {
                return weights[i];
            }
        }
        return 0;
    }
    
    public int getIDCount() {
        return edgesPos.length;
    }

    @Override
    public RoutingGraph getInverted() {
        // Deep "copy"
        int[] startID = new int[edges.length];
        int[] endID = new int[edges.length];
        int[] weight = new int[edges.length];
        AdjacentFieldsRoutingGraph result = new AdjacentFieldsRoutingGraph();
        int j = 0;
        for (int i = 0; i < edges.length; i++) {
            for (; i >= edgesPos[j+1]; j++);
            weight[i] = weights[i];
            startID[i] = edges[i];
            endID[i] = j;
        }
        result.buildGraph(startID, endID, weight);
        return result;
    }

}
