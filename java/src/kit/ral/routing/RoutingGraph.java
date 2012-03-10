package kit.ral.routing;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;

public interface RoutingGraph {

    /**
     * Operation buildGraph bekommt ein 3-Tupel übergeben, das aus ID, ID und
     * Gewicht besteht
     * 
     * @param startID
     * @param endID
     * @param weight
     */
    public void buildGraph(int[] startID, int[] endID, int[] weight, int maxId);

    /**
     * Loads the underlying routing graph from the given stream.
     * Any old graph will be deleted doing so.
     * @param stream the source stream
     */
    public void loadFromInput(DataInput input) throws IOException;

    /**
     * Saves the underlying routing graph to the given stream.
     * @param stream the destination stream
     */
    public void saveToOutput(DataOutput output) throws IOException;

    byte getAreaID(int node);

    void setAreaID(int node, byte id);

    int getWeight(int first, int last);

    public int getIDCount();

    public RoutingGraph getInverted();

    public Collection<Integer> getRelevantNeighbors(int node, byte[] areas);
    public Collection<Integer> getAllNeighbors(int node);

    public void setArcFlag(int node, int node2, byte area);
    
    public String getMetisRepresentation();

    public void readAreas(String areas);

    public void buildGraphWithUniqueEdges(int[] uniqueEdgeStartIDs, int[] uniqueEdgeEndIDs, int maxWayNodeId);
    
    public int[] getStartIDArray();
    
    public int[] getEdgesArray();
    
    public int[] getWeightsArray();
    
    public void setAllArcFlags();

}
