package kit.route.a.lot.map.infosupply;

import java.io.InputStream;import java.io.OutputStream;import java.util.List;
import java.util.Set;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.POIDescription;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.map.*;

public class MapInfo {

    /** Associations */
    private ElementDB elementDB;
    private GeographicalOperator geographicalOperator;
    private AddressOperator addressOperator;

    /**
     * Constructor
     */
    public MapInfo(){
        geographicalOperator = new PrimitivArrayGeoOperator();
        elementDB = new ArrayElementDB();
        
    }
    
    /**
     * Operation setBounds
     * 
     * @param upLeft
     *            -
     * @param bottomRight
     *            -
     * @return
     * @return
     */
    public void setBounds(Coordinates upLeft, Coordinates bottomRight) {
        geographicalOperator.setBounds(upLeft, bottomRight);
    }

    /**
     * Operation addNode <
     * 
     * @param position
     *            -
     * @param id
     *            -
     * @return
     * @return
     */
    public void addNode(Coordinates position, int id) {
        Node newNode = new Node(id, position);
        elementDB.addNode(newNode);
        geographicalOperator.addToBaseLayer(newNode);
    }

    /**
     * Operation addWay
     * 
     * @param ids
     *            -
     * @param name
     *            -
     * @param type
     *            -
     * @return
     * @return
     */
    public void addWay(List<Integer> ids, String name, int type) {
            
        if(type > 0) {      //TODO define types
            Street street = new Street(type, name);
            elementDB.addMapElement(street);
            for(int i = 0; i < ids.size() - 1; i++) {
                Node start = elementDB.getNode(ids.get(i));
                Node end = elementDB.getNode(ids.get(i + 1));
                Edge edge = new Edge(start, end, street);
                street.addEdge(edge);
                elementDB.addMapElement(edge);
                geographicalOperator.addToBaseLayer(edge);
                }
        } else {    
            Area area = new Area(type, name);
            elementDB.addMapElement(area);
            geographicalOperator.addToBaseLayer(area);
            for (int i = 0; i < ids.size(); i++) {
                area.addNode(elementDB.getNode(ids.get(i)));
            }    
        }
    }
    

    /**
     * Operation addPOI
     * 
     * @param position
     *            -
     * @param id
     *            -
     * @param description
     *            -
     * @return
     * @return
     */
    public void addPOI(Coordinates position, int id, POIDescription description) {
            POINode newPOI = new POINode(id, position, description);
            elementDB.addNode(newPOI);
            geographicalOperator.addToOverlay(newPOI);
    }

    /**
     * Operation addFavorite
     * 
     * @param pos
     *            -
     * @param info
     *            -
     * @return
     * @return
     */
    public void addFavorite(Coordinates pos, String info) {
    }

    /**
     * Operation deleteFavorite
     * 
     * @param position
     *            -
     * @return
     * @return
     */
    public void deleteFavorite(Coordinates position) {
    }

    /**
     * Operation getPOIDescription
     * 
     * @param pos
     *            -
     * @return POIDescription
     */
    public POIDescription getPOIDescription(Coordinates pos) {
        return null;
    }

    /**
     * Operation getNodePosition
     * 
     * @param nodeID
     *            -
     * @return Coordinates
     */
    public Coordinates getNodePosition(int nodeID) {
        return elementDB.getNodePosition(nodeID);
    }

    /**
     * Operation buildZoomlevels
     * 
     * @return
     * 
     * @return
     */
    public void buildZoomlevels() {
    }

    /**
     * Operation suggestCompletions
     * 
     * @param expression
     *            -
     * @return List<String>
     */
    public List<String> suggestCompletions(String expression) {
        return null;
    }

    /**
     * Operation select
     * 
     * @param address
     *            -
     * @return Selection
     */
    public Selection select(String address) {
        return null;
    }

    /**
     * Operation select
     * 
     * @param pos
     *            -
     * @return Selection
     */
    public Selection select(Coordinates pos) {
        return geographicalOperator.select(pos);
    }

    /**
     * Operation getBaseLayer
     * 
     * @param zoomlevel
     *            -
     * @param upLeft
     *            -
     * @param bottomRight
     *            -
     * @return Set<MapElement>
     */
    public List<MapElement> getBaseLayer(int zoomlevel, Coordinates upLeft,
            Coordinates bottomRight) {
        return geographicalOperator.getBaseLayer(zoomlevel, upLeft, bottomRight);
    }

    /**
     * Operation getOverlay
     * 
     * @param zoomlevel
     *            -
     * @param upLeft
     *            -
     * @param bottomRight
     *            -
     * @return Set<MapElement>
     */
    public List<MapElement> getOverlay(int zoomlevel, Coordinates upLeft,
            Coordinates bottomRight) {
        return geographicalOperator.getOverlay(zoomlevel, upLeft, bottomRight);
    }

    /**
     * Operation loadFromStream
     * 
     * @param stream
     *            -
     * @return
     * @return
     */
    public void loadFromStream(InputStream stream) {
    }

    /**
     * Operation saveToStream
     * 
     * @param stream
     *            -
     * @return
     * @return
     */
    public void saveToStream(OutputStream stream) {
    }
}
