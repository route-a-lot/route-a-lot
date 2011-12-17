package kit.route.a.lot.routing;

import kit.route.a.lot.io.MapIO;
import kit.route.a.lot.io.OSMLoader;

public interface RoutingGraph

{
    /**
     * Operation buildGraph
     * bekommt ein 3-Tupel übergeben, das aus ID, ID und Gewicht besteht
     *
     * @param startID - 
     * @param endID - 
     * @param weight - 
     * @return 
     */
    public buildGraph ( int[] startID, int[] endID, int[] weight );

    /**
     * Operation loadFromStream
     *
     * @param stream - 
     * @return 
     */
    public loadFromStream ( InputStream stream );

    /**
     * Operation saveToStream
     *
     * @param stream - 
     * @return 
     */
    public saveToStream ( OutputStream stream );

    /**
     * Operation getRelevantNeighbors
     *
     * @param node - 
     * @param destArea - 
     * @return Collection<int>
     */
    protected Collection<int> getRelevantNeighbors ( int node, byte destArea );

    /**
     * Operation getAreaID
     *
     * @param node - 
     * @return byte
     */
    protected byte getAreaID ( int node );

    /**
     * Operation setAreaID
     *
     * @param node - 
     * @param id - 
     * @return 
     */
    protected setAreaID ( int node, byte id );

    /**
     * Operation getArcFlags
     *
     * @param startID - 
     * @param endID - 
     * @return int64
     */
    protected int64 getArcFlags ( int startID, int endID );

    /**
     * Operation setArcFlags
     *
     * @param startID - 
     * @param endID - 
     * @param flags - 
     * @return 
     */
    protected setArcFlags ( int startID, int endID, int flags );

}

