package kit.route.a.lot.routing;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;


public class AdjacentFieldsRoutingGraph
 implements RoutingGraph
{
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
		// TODO Auto-generated method stub
		
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
		return null;
		// TODO Auto-generated method stub
		
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
		return 1;
	}
	@Override
	public void setArcFlags(int startID, int endID, int flags) {
		// TODO Auto-generated method stub
		
	}
}

