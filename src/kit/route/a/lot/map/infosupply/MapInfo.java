package kit.route.a.lot.map.infosupply;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.POIDescription;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.map.MapElement;

public class MapInfo

{
    /** Associations */
    private ElementDB elementDB;
    private GeographicalOperator geographicalOperator;
    private AddressOperator addressOperator;
    /**
     * Operation setBounds
     *
     * @param upLeft - 
     * @param bottomRight - 
     * @return 
     * @return 
     */
    public void setBounds ( Coordinates upLeft, Coordinates bottomRight ){}
    /**
     * Operation addNode
     * <	
     *
     * @param position - 
     * @param id - 
     * @return 
     * @return 
     */
    public void addNode ( Coordinates position, int id ){}
    /**
     * Operation addWay
     *
     * @param ids - 
     * @param name - 
     * @param type - 
     * @return 
     * @return 
     */
    public void addWay ( List<Integer> ids, String name, int type ){}
    /**
     * Operation addPOI
     *
     * @param position - 
     * @param id - 
     * @param description - 
     * @return 
     * @return 
     */
    public void addPOI ( Coordinates position, int id, POIDescription description ){}
    /**
     * Operation addFavorite
     *
     * @param pos - 
     * @param info - 
     * @return 
     * @return 
     */
    public void addFavorite ( Coordinates pos, String info ){}
    /**
     * Operation deleteFavorite
     *
     * @param position - 
     * @return 
     * @return 
     */
    public void deleteFavorite ( Coordinates position ){}
    /**
     * Operation getPOIDescription
     *
     * @param pos - 
     * @return POIDescription
     */
    public POIDescription getPOIDescription ( Coordinates pos ){
		return null;}
    /**
     * Operation getNodePosition
     *
     * @param nodeID - 
     * @return Coordinates
     */
    public Coordinates getNodePosition ( int nodeID ){
		return null;}
    /**
     * Operation buildZoomlevels
     * @return 
     *
     * @return 
     */
    public void buildZoomlevels (  ){}
    /**
     * Operation suggestCompletions
     *
     * @param expression - 
     * @return List<String>
     */
    public List<String> suggestCompletions ( String expression ){
		return null;}
    /**
     * Operation select
     *
     * @param address - 
     * @return Selection
     */
    public Selection select ( String address ){
		return null;}
    /**
     * Operation select
     *
     * @param pos - 
     * @return Selection
     */
    public Selection select ( Coordinates pos ){
		return null;}
    /**
     * Operation getBaseLayer
     *
     * @param zoomlevel - 
     * @param upLeft - 
     * @param bottomRight - 
     * @return Set<MapElement>
     */
    public Set<MapElement> getBaseLayer ( int zoomlevel, Coordinates upLeft, Coordinates bottomRight ){
		return null;}
    /**
     * Operation getOverlay
     *
     * @param zoomlevel - 
     * @param upLeft - 
     * @param bottomRight - 
     * @return Set<MapElement>
     */
    public Set<MapElement> getOverlay ( int zoomlevel, Coordinates upLeft, Coordinates bottomRight ){
		return null;}
    /**
     * Operation loadFromStream
     *
     * @param stream - 
     * @return 
     * @return 
     */
    public void loadFromStream ( InputStream stream ){}
    /**
     * Operation saveToStream
     *
     * @param stream - 
     * @return 
     * @return 
     */
    public void saveToStream ( OutputStream stream ){}
}

