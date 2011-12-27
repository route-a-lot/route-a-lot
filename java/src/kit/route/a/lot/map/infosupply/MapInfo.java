package kit.route.a.lot.map.infosupply;

import java.io.InputStream;import java.io.OutputStream;import java.util.List;
import java.util.Set;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.POIDescription;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.map.*;

public class MapInfo {

    private static ElementDB elementDB;
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
     * Sets the entire area of the map. 
     * 
     * @param upLeft the upper left corner of the map
     *            
     * @param bottomRight the bottom right corner of the area
     */
    public void setBounds(Coordinates upLeft, Coordinates bottomRight) {
        geographicalOperator.setBounds(upLeft, bottomRight);
    }

    /**
     * Adds a node to the data structures.
     * 
     * @param position the position of the node
     *            
     * @param id the unique id of the node
     */
    public void addNode(Coordinates position, int id) {
        Node newNode = new Node(id, position);
        elementDB.addNode(newNode);
        geographicalOperator.addToBaseLayer(newNode);
    }

    /**
     * Adds a way the the data structures.
     * 
     * @param ids the id's of the nodes in the Way (builds edges)
     *            
     * @param name the name of the street
     *            
     * @param type the type of the street
     */
    public void addWay(List<Integer> ids, String name, int type) {
            
        if(type > 0) {      //TODO define types
            Street street = new Street(type, name);
            elementDB.addMapElement(street);
            for(int i = 0; i < ids.size() - 1; i++) {   //add edges
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
     * Adds a point of interest to the data structures.
     * 
     * @param position the position of the POI
     *            
     * @param id the unique id of the node
     *            
     * @param description the description of the POI
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
     * Returns the coordinates of a given node id.
     * 
     * @param nodeID the id of the node
     *            
     * @return the coordinates of the node correspondenting to the give id.
     */
    public static Coordinates getNodePosition(int nodeID) {
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
     * Returns a selection to a given coordinate.
     * 
     * @param pos the given coordinate
     *            
     * @return the correspondenting selection
     */
    public Selection select(Coordinates pos) {
        return geographicalOperator.select(pos);
    }

    /**
     * Return the, to given coordinates, belonging MapElements of the base layer.
     * 
     * @param zoomlevel the zoomlevel of the view
     *            
     * @param upLeft the coordinates of the upper left corner of the view
     *            
     * @param bottomRight the coordinates of the bottom right corner of the view
     *            
     * @return the correspondending mapElements
     */
    public List<MapElement> getBaseLayer(int zoomlevel, Coordinates upLeft,
            Coordinates bottomRight) {
        return geographicalOperator.getBaseLayer(zoomlevel, upLeft, bottomRight);
    }

    /**
     * Return the, to given coordinates, belonging MapElements of the overlay.
     * 
     * @param zoomlevel the zoomlevel of the view
     *            
     * @param upLeft the coordinates of the upper left corner of the view
     *            
     * @param bottomRight the coordinates of the bottom right corner of the view
     *            
     * @return the correspondending mapElements
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
