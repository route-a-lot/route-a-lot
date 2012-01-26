package kit.route.a.lot.routing;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;


public class AdjacentFieldsRoutingGraph implements RoutingGraph {
    
    private static Logger logger = Logger.getLogger(AdjacentFieldsRoutingGraph.class);
    
    private int[] edgesPos;
    private byte[] areaID;
    private int[] edges;
    private int[] weights;
    private long[] arcFlags;
    private AdjacentFieldsRoutingGraph metisGraph;
    
    @Override
    public void buildGraph(int[] startID, int[] endID, int[] weight, int maxNodeID) {
        logger.info("Creating routing graph with " + maxNodeID + " ID's and "  + startID.length + " edges");
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

        // copy data to internal structures
        edgesPos = new int[maxNodeID + 1];
        edgesPos[0] = 0;
        for (int i = 1; i < startID.length; i++) {
            // for each edge
            for (int id = startID[i - 1] + 1; id <= startID[i]; id++) {
                // for each node between the old startID and the new one
                edgesPos[id] = i;
            }
        }
        areaID = new byte[maxNodeID + 2];
        edges = endID; //TODO DISCUSS: .clone()? 
        weights = weight;
        arcFlags = new long[startID.length];
        Arrays.fill(arcFlags, ~ (long) 0);
    }
        
    public void buildGraphWithUniqueEdges(int[] startID, int[] endID, int maxNodeID) {
        int[] newStartID = new int[startID.length * 2];
        int[] newEndID = new int[endID.length * 2];
        for (int i = 0; i < startID.length; i++) {
            newStartID[i] = startID[i];
            newEndID[i] = endID[i];
        }
        for (int i = 0; i < startID.length; i++) {
            newStartID[startID.length + i - 1] = endID[i];
            newEndID[startID.length + i - 1] = startID[i];
        }
        metisGraph = new AdjacentFieldsRoutingGraph();
        metisGraph.buildGraph(newStartID, newEndID, newStartID, maxNodeID);
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
        if (node >= edgesPos.length - 1 || node < 0) {
            logger.warn("Node " + String.valueOf(node) + " does not exist, aborting");
            return new ArrayList<Integer>();
        }
        Collection<Integer> relevantEdges = new ArrayList<Integer>();
        logger.info("Getting neighbours for node " + node);
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
        for (int neighbor: relevantEdges) {
            logger.debug("Neighbor to " + node + ": " + neighbor);
        }
        if (relevantEdges.size() == 0) {
            logger.debug("No Neighbors found for " + node);
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
        Collection<Integer> relevantEdges = new ArrayList<Integer>();
        for (int i = edgesPos[node]; i < edgesPos[node+1]; i++) {
            // don't filter at all
            relevantEdges.add(edges[i]);
        }
        return relevantEdges;
    }

    public int getWeight(int from, int to) {
        for (int i = edgesPos[from]; i < edgesPos[from+1]; i++) {
            if (edges[i] == to) {
                if (weights[i] > 0) {
                    logger.debug("Weight from " + from + " to " + to + " is " + weights[i]);
                    return weights[i];
                } else {
                    logger.error("Got zero weight from " + from + " to " + to);
                    return 1;
                }
            }
        }
        logger.warn("No weight found from ID " + Integer.valueOf(from) + " to " + Integer.valueOf(to));
        return -1;
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
        for (int i = 0; i < edgesPos.length - 1; i++) {
            for (int j = edgesPos[i]; j < edgesPos[i + 1]; j++) {
                weight[j] = weights[j];
                startID[j] = edges[j];
                endID[j] = i;
            }
        }
        result.buildGraph(startID, endID, weight, edgesPos.length - 1);
        return result;
    }

    @Override
    public String getMetisRepresentation() {
        return metisGraph.getMetis();
    }
    
    private String getMetis() {
        // returns a representation of the graph suitable for Metis.
        String result = "";
        result += String.valueOf(edgesPos.length);  
        /* -1 because of .length,
         * -1 because the last one is a dummy and 
         * +1 because we increase every ID by 1 for Metis.
         */
        result += " ";
        result += String.valueOf(edges.length / 2);
        result += "\n";
        for (int i = 0; i < edgesPos.length - 1; i++) {
            // For all Nodes
            for (int node: getAllNeighbors(i)) {
                /*if (getWeight(node, i) < 0 || getWeight(i, node) < 0) {
                    logger.fatal("Got inconsisten graph (missing edge between + " + i + " and " + node + ")");
                }*/
            }
            for (int j = edgesPos[i]; j < edgesPos[i+1]; j++) {
                result += String.valueOf(edges[j] + 1); // Metis doesn't like ID 0.
                result += " ";
            }
            result += "\n";
        }
        return result + "\0";
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
