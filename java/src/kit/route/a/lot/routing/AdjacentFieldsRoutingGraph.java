package kit.route.a.lot.routing;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.Math;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Arrays;

import org.apache.log4j.Logger;

import kit.route.a.lot.common.IntTuple;


public class AdjacentFieldsRoutingGraph implements RoutingGraph {
    
    private static Logger logger = Logger.getLogger(AdjacentFieldsRoutingGraph.class);
    
    private int[] edgesPos;
    private byte[] areaID;
    private int[] edges;
    private int[] weights;
    private long[] arcFlags;
    
    @Override
    public void buildGraph(int[] startID, int[] endID, int[] weight) {
        logger.info("Creating routing graph...");
        // assert same non-null array size
        if (startID.length == 0) {
            logger.error("Array length is zero, aborting.");
            return;
        }
        if (startID.length != endID.length || endID.length != weight.length) {
            logger.error("The lengths of the arrays don't match, aborting.");
            return;
        }
        // sort arrays simultaneously by startID
        sortByKey(startID, endID, weight);
        // find maximum node index
        int maxNodeID = startID[startID.length - 1];
        for (int i = 0; i < endID.length; i++) {
            maxNodeID = Math.max(maxNodeID, endID[i]);
        }
        
        // copy data to internal structures
        edgesPos = new int[maxNodeID + 1];
        edgesPos[0] = 0;
        for (int i = 1; i < startID.length; i++) {
            if (startID[i] > startID[i - 1]) {
                edgesPos[startID[i]] = i; 
            }
        }
        areaID = new byte[maxNodeID + 1];
        edges = endID.clone();
        weights = weight.clone();
        arcFlags = new long[startID.length];
        Arrays.fill(arcFlags, ~ (long) 0);
    }
    
    /**
     * Sorts the given integer arrays via Quicksort, using the key array as reference
     * while the data arrays are sorted in just the same manner.
     * 
     * @param key the reference array
     * @param data1 the first data array
     * @param data2 the second data array
     * @throws IllegalArgumentException either argument is <code>null</code>
     *          or the arrays are not of equal size
     */
    private void sortByKey(int[] key, int[] data1, int[] data2) {
        if ((key == null) || (data1 == null) || (data2 == null)
                || (key.length != data1.length) || (key.length != data2.length)) {
            throw new IllegalArgumentException();
        }
        sortByKeyInternal(key, data1, data2, 0, key.length - 1);
    }
    
    /**
     * Sorts an index range of the given integer arrays via Quicksort, using the
     * key array as reference while the data arrays are sorted in just the same manner.
     * The arguments <code>low</code> and <code>high</code> define the index range.
     * <i>This method should not be called directly. Use sortByKey() instead</i>.
     * 
     * @param key the reference array
     * @param data1 the first data array
     * @param data2 the second data array
     * @param low the lowest index to be sorted
     * @param high the highest index to be sorted
     */
    private void sortByKeyInternal(int[] key, int[] data1, int[] data2, int low, int high) {
        int i = low, j = high;
        int pivot = key[low + (high-low)/2];

        while (i <= j) {
            while (key[i] < pivot) {
                i++;
            }
            while (key[j] > pivot) {
                j--;
            }
            if (i <= j) {
                int temp = key[i];
                key[i] = key[j];
                key[j] = temp;
                temp = data1[i];
                data1[i] = data1[j];
                data1[j] = temp;
                temp = data2[i];
                data2[i] = data2[j];
                data2[j] = temp;     
                i++;
                j--;
            }
        }
        if (low < j) {
            sortByKeyInternal(key, data1, data2, low, j);
        }
        if (i < high) {
            sortByKeyInternal(key, data1, data2, i, high);
        }
    }

    @Override
    public void loadFromStream(DataInputStream stream) throws IOException {
        if (stream == null) {
            throw new IllegalArgumentException();
        }
        int nodeCount = stream.readInt();
        edgesPos = new int[nodeCount];
        areaID = new byte[nodeCount];
        for (int i = 0; i < nodeCount; i++) {
            edgesPos[i] = stream.readInt();
            areaID[i] = stream.readByte();
        } 
        int edgeCount = stream.readInt();
        edges = new int[edgeCount];
        weights = new int[edgeCount];
        arcFlags = new long[edgeCount];
        for (int i = 0; i < edgeCount; i++) {
            edges[i] = stream.readInt();
            weights[i] = stream.readInt();
            arcFlags[i] = stream.readLong();
        }
    }

    @Override
    public void saveToStream(DataOutputStream stream) throws IOException {
        if (stream == null) {
            throw new IllegalArgumentException();
        }
        stream.writeInt(edgesPos.length);
        for (int i = 0; i < edgesPos.length; i++) {
            stream.writeInt(edgesPos[i]);
            stream.writeByte(areaID[i]);
        }
        stream.writeInt(edges.length);
        for (int i = 0; i < edges.length; i++) {
            stream.writeInt(edges[i]);
            stream.writeInt(weights[i]);
            stream.writeLong(arcFlags[i]);
        }
    }
    
    
    public Collection<Integer> getRelevantNeighbors(int node, byte[] destAreas) {
        if (node > edgesPos.length) {
            logger.warn("Node " + String.valueOf(node) + " does not exist, aborting");
            return null;
        }
        LinkedList<Integer> relevantEdges = new LinkedList<Integer>();
        long flags = 0;
        for (byte area: destAreas) {
            // create bitmask
            flags |= 1 << area;
        }
        for (int i = edgesPos[node]; i < edgesPos[node+1]; i++) {
            // filter edges
            if ((arcFlags[i] | flags) != 0) {
                relevantEdges.add(edges[i]);
            }
        }
        return relevantEdges;
    }
    
    @Override
    public Collection<Integer> getAllNeighbors(int node) {
        // required for Precalculator.
        if (node > edgesPos.length) {
            logger.warn("Node " + String.valueOf(node) + " does not exist, aborting");
            return null;
        }
        LinkedList<Integer> relevantEdges = new LinkedList<Integer>();
        for (int i = edgesPos[node]; i < edgesPos[node+1]; i++) {
            // don't filter at all
            relevantEdges.add(edges[i]);
        }
        return relevantEdges;
    }
    
    @Override
    public byte getAreaID(int node) {
        if (node > edgesPos.length) {
            logger.warn("Node " + String.valueOf(node) + " does not exist, aborting");
            return (Byte) null;
        }
        return areaID[node];
    }

    @Override
    public void setAreaID(int node, byte id) {
        if (node > edgesPos.length) {
            logger.warn("Node " + String.valueOf(node) + " does not exist, aborting");
            return;
        }
        areaID[node] = id;
    }
    
    public void setArcFlag(int startID, int endID, byte area) {
        // Note: doesn't do anything if the edge is not found, maybe we should raise an error?
        if (startID > edgesPos.length || endID > edgesPos.length) {
            logger.warn("ID's are not within bounds");
            return;
        }
        for (int i = edgesPos[startID]; i < edgesPos[startID+1]; i++) {
            if (endID == edges[i]) {
                arcFlags[i] |= 1 << area;
            }
        }    
    }

    public int getWeight(int from, int to) {
        for (int i = edgesPos[from]; i < edgesPos[from+1]; i++) {
            if (edges[i] == to) {
                return weights[i];
            }
        }
        logger.warn("No weight found from ID " + Integer.valueOf(from) + " to " + Integer.valueOf(to));
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

    @Override
    public String getMetisRepresentation() {
        // returns a representation of the graph suitable for Metis.
        //TODO: edges.length isn't what is required by Metis (it works with undirected graphs only)
        String result = "";
        result += String.valueOf(edgesPos.length);
        result += " ";
        result += String.valueOf(edges.length);
        result += "\n";
        for (int i = 0; i < edgesPos.length; i++) {
            for (int j = edgesPos[i]; j < edgesPos[i+1]; j++) {
                result += String.valueOf(edges[j]);
                result += " ";
            }
            result += "\n";
        }
        return result;
    }

    @Override
    public void readAreas(String areas) {
        // TODO: way to slow, might require error-handling
        int i = 0;
        for (String area: areas.split("\\s+")) {
            areaID[i++] = (byte) Integer.parseInt(area);
        }
    }
    
    

}
