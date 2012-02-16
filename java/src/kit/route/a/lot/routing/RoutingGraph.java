package kit.route.a.lot.routing;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;

public interface RoutingGraph {

    /**
     * Operation buildGraph bekommt ein 3-Tupel übergeben, das aus ID, ID und
     * Gewicht besteht
     * 
     * @param startID
     *            -
     * @param endID
     *            -
     * @param weight
     *            -
     * @return
     * @return
     */
    public void buildGraph(int[] startID, int[] endID, int[] weight, int maxId);

    /**
     * Loads the underlying routing graph from the given stream.
     * Any old graph will be deleted doing so.
     * 
     * @param stream the source stream
     */
    public void loadFromStream(DataInputStream stream) throws IOException;

    /**
     * Saves the underlying routing graph to the given stream.
     * 
     * @param stream the destination stream
     */
    public void saveToStream(DataOutputStream stream) throws IOException;

    /**
     * Operation getAreaID
     * 
     * @param node
     *            -
     * @return byte
     */
    byte getAreaID(int node);

    /**
     * Operation setAreaID
     * 
     * @param node
     *            -
     * @param id
     *            -
     * @return
     * @return
     */
    void setAreaID(int node, byte id);

    /**
     * Operation setArcFlags
     * 
     * @param startID
     *            -
     * @param endID
     *            -
     * @param flags
     *            -
     * @return
     * @return
     */


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

}
