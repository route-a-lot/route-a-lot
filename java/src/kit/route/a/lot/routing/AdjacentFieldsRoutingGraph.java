package kit.route.a.lot.routing;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.Math;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Arrays;

import org.apache.log4j.Logger;

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
    private static Logger logger = Logger.getLogger(AdjacentFieldsRoutingGraph.class);

    @Override
    public void buildGraph(int[] startID, int[] endID, int[] weight) {
        // ToDo: revise
        logger.debug("Creating Routinggraph...");
        if (startID.length != endID.length || endID.length != weight.length) {
            logger.error("The length of the arrays don't match, aborting.");
            return;
        }
        int max = 0;
        for (int id: startID) {
            // Get maxID = edgesPos.size = edgeList.size
            max = Math.max(max, id);
        }
        if (max > startID.length) {
            logger.error("ID's are NOT continuous, aborting.");
            return;
        }
        // Initialize Arrays
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
        logger.info("Creating edges...");
        for (LinkedList<IntTuple> edgeList: edgeLists) {
            logger.info("Creating edges for ID " + String.valueOf(i));
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
            logger.warn("ID's not within bounds");
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
