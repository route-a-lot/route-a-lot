package kit.route.a.lot.routing;

import java.io.InputStream;
import java.io.OutputStream;
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
    public void buildGraph(int[] startID, int[] endID, int[] weight);

    /**
     * Operation loadFromStream
     * 
     * @param stream
     *            -
     * @return
     * @return
     */
    public void loadFromStream(InputStream stream);

    /**
     * Operation saveToStream
     * 
     * @param stream
     *            -
     * @return
     * @return
     */
    public void saveToStream(OutputStream stream);

    /**
     * Operation getRelevantNeighbors
     * 
     * @param node
     *            -
     * @param destArea
     *            -
     * @return Collection<int>
     */
    Collection<Integer> getRelevantNeighbors(int node, byte destArea);

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
     * Operation getArcFlags
     * 
     * @param startID
     *            -
     * @param endID
     *            -
     * @return int64
     */
    long getArcFlags(int startID, int endID);

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
    void setArcFlags(int startID, int endID, int flags);

}
