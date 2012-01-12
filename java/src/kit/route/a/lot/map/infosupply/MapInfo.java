package kit.route.a.lot.map.infosupply;

import java.io.InputStream;import java.io.OutputStream;
import java.util.Collection;
import java.util.List;


import kit.route.a.lot.common.Address;
import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.POIDescription;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.common.WayInfo;
import kit.route.a.lot.map.Area;
import kit.route.a.lot.map.Edge;
import kit.route.a.lot.map.MapElement;
import kit.route.a.lot.map.Node;
import kit.route.a.lot.map.POINode;
import kit.route.a.lot.map.Street;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class MapInfo {

    private static Logger logger = Logger.getLogger(MapInfo.class);
    private ElementDB elementDB;
    private GeographicalOperator geographicalOperator;
    private AddressOperator addressOperator;

    public MapInfo() {
        elementDB = new ArrayElementDB();
        geographicalOperator = new QTGeographicalOperator();
    }

    /**
     * Constructor
     */
    public MapInfo(Coordinates upLeft, Coordinates bottomRight) {
        geographicalOperator = new QTGeographicalOperator();
        geographicalOperator.setBounds(upLeft, bottomRight);
        elementDB = new ArrayElementDB();
    }

    /**
     * Sets the entire area of the map.
     * 
     * @param upLeft
     *            the upper left corner of the map
     * 
     * @param bottomRight
     *            the bottom right corner of the area
     */
    public void setBounds(Coordinates upLeft, Coordinates bottomRight) {
        geographicalOperator.setBounds(upLeft, bottomRight);
    }

    /**
     * Adds a node to the data structures.
     * 
     * @param position
     *            the position of the node
     * 
     * @param id
     *            the unique id of the node
     */
    public void addNode(Coordinates position, int id, Address address) {
        
        
        Node newNode = new Node(id, position);
        elementDB.addNode(newNode);
        geographicalOperator.addToBaseLayer(newNode);
        logger.warn("asdasds");
    }

    /**
     * Adds a way the the data structures.
     * 
     * @param ids
     *            the id's of the nodes in the Way (builds edges)
     * 
     * @param name
     *            the name of the street
     * 
     * @param type
     *            the type of the street
     */
    public void addWay(List<Integer> ids, String name, WayInfo wayInfo) {

        if (wayInfo.isStreet()) { 
            Street street = new Street(wayInfo.getType(), name, wayInfo);
            addEdgesToStreet(street, ids);
            elementDB.addMapElement(street);
            geographicalOperator.addToBaseLayer(street);
        } else {
            Area area = new Area(wayInfo.getType(), name, wayInfo);
            elementDB.addMapElement(area);
            geographicalOperator.addToBaseLayer(area);
            for (int i = 0; i < ids.size(); i++) {
                area.addNode(elementDB.getNode(ids.get(i)));
            }
            geographicalOperator.addToBaseLayer(area);
        }
    }

    private void addEdgesToStreet(Street street, List<Integer> ids) {
        for (int i = 0; i < ids.size() - 1; i++) {
            Node start = elementDB.getNode(ids.get(i));
            Node end = elementDB.getNode(ids.get(i + 1));
            Edge edge = new Edge(start, end, street);
            start.addOutgoingEdge(edge);
            street.addEdge(edge);
        }
    }


    /**
     * Adds a point of interest to the data structures.
     * 
     * @param position
     *            the position of the POI
     * 
     * @param id
     *            the unique id of the node
     * 
     * @param description
     *            the description of the POI
     */
    public void addPOI(Coordinates position, int id, POIDescription description, Address address) {
        POINode newPOI = new POINode(id, position, description);
        elementDB.addNode(newPOI);
        geographicalOperator.addToOverlay(newPOI);
    }

    /**
     * Adds a Favorite to the data structures.
     * 
     * @param the
     *            position of the favorite
     * 
     * @param description
     *            a description of the favorite
     */
    public void addFavorite(Coordinates pos, POIDescription description) {
        POINode newFav = new POINode(0, pos, description);
        elementDB.addMapElement(newFav);
        geographicalOperator.addToOverlay(newFav);
    }

    /**
     * Deletes the favorite in the given area (little area around the coordinate)
     * 
     * @param position
     *            the position of the favorite
     */
    public void deleteFavorite(Coordinates position) {
        elementDB.deleteFavorite(geographicalOperator.deleteFavorite(position));
    }

    /**
     * Returns a description of the POI at the given area (little area around the given position)
     * 
     * @param pos
     *            the position of the POI
     * 
     * @return the description of the POI
     */
    public POIDescription getPOIDescription(Coordinates pos) {
        return geographicalOperator.getPOIDescription(pos);
    }

    /**
     * Returns the coordinates of a given node id.
     * 
     * @param nodeID
     *            the id of the node
     * 
     * @return the coordinates of the node correspondenting to the give id.
     */
    public Coordinates getNodePosition(int nodeID) {
        return elementDB.getNodePosition(nodeID);
    }

    /**
     * Builds the zoomLevels for the view.
     */
    public void buildZoomlevels() {
        geographicalOperator.buildZoomlevels();
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
     * @param pos
     *            the given coordinate
     * 
     * @return the correspondenting selection
     */
    public Selection select(Coordinates pos) {
        return geographicalOperator.select(pos);
    }

    /**
     * Return the, to given coordinates, belonging MapElements of the base layer.
     * 
     * @param zoomlevel
     *            the zoomlevel of the view
     * 
     * @param upLeft
     *            the coordinates of the upper left corner of the view
     * 
     * @param bottomRight
     *            the coordinates of the bottom right corner of the view
     * 
     * @return the correspondending mapElements
     */
    public Collection<MapElement> getBaseLayer(int zoomlevel, Coordinates upLeft, Coordinates bottomRight) {
        return geographicalOperator.getBaseLayer(zoomlevel, upLeft, bottomRight);
    }

    /**
     * Return the, to given coordinates, belonging MapElements of the overlay.
     * 
     * @param zoomlevel
     *            the zoomlevel of the view
     * 
     * @param upLeft
     *            the coordinates of the upper left corner of the view
     * 
     * @param bottomRight
     *            the coordinates of the bottom right corner of the view
     * 
     * @return the correspondending mapElements
     */
    public Collection<MapElement> getOverlay(int zoomlevel, Coordinates upLeft, Coordinates bottomRight) {
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
