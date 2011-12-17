package kit.route.a.lot.map.infosupply;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.POIDescription;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.io.MapIO;
import kit.route.a.lot.io.OSMLoader;
import kit.route.a.lot.map.rendering.Renderer;
import kit.route.a.lot.map.infosupply.ElementDB;
import kit.route.a.lot.map.infosupply.GeographicalOperator;
import kit.route.a.lot.map.infosupply.AddressOperator;

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
     */
    public setBounds ( Coordinates upLeft, Coordinates bottomRight ){}
    /**
     * Operation addNode
     * <	
     *
     * @param position - 
     * @param id - 
     * @return 
     */
    public addNode ( Coordinates position, int id ){}
    /**
     * Operation addWay
     *
     * @param ids - 
     * @param name - 
     * @param type - 
     * @return 
     */
    public addWay ( List<int> ids, String name, int type ){}
    /**
     * Operation addPOI
     *
     * @param position - 
     * @param id - 
     * @param description - 
     * @return 
     */
    public addPOI ( Coordinates position, int id, POIDescription description ){}
    /**
     * Operation addFavorite
     *
     * @param pos - 
     * @param info - 
     * @return 
     */
    public addFavorite ( Coordinates pos, String info ){}
    /**
     * Operation deleteFavorite
     *
     * @param position - 
     * @return 
     */
    public deleteFavorite ( Coordinates position ){}
    /**
     * Operation getPOIDescription
     *
     * @param pos - 
     * @return POIDescription
     */
    public POIDescription getPOIDescription ( Coordinates pos ){}
    /**
     * Operation getNodePosition
     *
     * @param nodeID - 
     * @return Coordinates
     */
    public Coordinates getNodePosition ( int nodeID ){}
    /**
     * Operation buildZoomlevels
     *
     * @return 
     */
    public buildZoomlevels (  ){}
    /**
     * Operation suggestCompletions
     *
     * @param expression - 
     * @return List<String>
     */
    public List<String> suggestCompletions ( String expression ){}
    /**
     * Operation select
     *
     * @param address - 
     * @return Selection
     */
    public Selection select ( String address ){}
    /**
     * Operation select
     *
     * @param pos - 
     * @return Selection
     */
    public Selection select ( Coordinates pos ){}
    /**
     * Operation getBaseLayer
     *
     * @param zoomlevel - 
     * @param upLeft - 
     * @param bottomRight - 
     * @return Set<MapElement>
     */
    public Set<MapElement> getBaseLayer ( int zoomlevel, Coordinates upLeft, Coordinates bottomRight ){}
    /**
     * Operation getOverlay
     *
     * @param zoomlevel - 
     * @param upLeft - 
     * @param bottomRight - 
     * @return Set<MapElement>
     */
    public Set<MapElement> getOverlay ( int zoomlevel, Coordinates upLeft, Coordinates bottomRight ){}
    /**
     * Operation loadFromStream
     *
     * @param stream - 
     * @return 
     */
    public loadFromStream ( InputStream stream ){}
    /**
     * Operation saveToStream
     *
     * @param stream - 
     * @return 
     */
    public saveToStream ( OutputStream stream ){}
}

