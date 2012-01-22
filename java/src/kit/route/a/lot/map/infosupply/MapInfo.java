package kit.route.a.lot.map.infosupply;

import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Collection;
import java.util.List;

import kit.route.a.lot.common.Address;
import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.POIDescription;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.common.WayInfo;
import kit.route.a.lot.map.Area;
import kit.route.a.lot.map.MapElement;
import kit.route.a.lot.map.Node;
import kit.route.a.lot.map.POINode;
import kit.route.a.lot.map.Street;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;


public class MapInfo {

    private static Logger logger = Logger.getLogger(MapInfo.class);
    static {
        logger.setLevel(Level.INFO);
    }
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
     * Writes the topLeft and bottomRight values of the current map to the given variables.
     * 
     */
    public void getBounds(Coordinates upLeft, Coordinates bottomRight) {
        geographicalOperator.getBounds(upLeft, bottomRight);
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
        Node newNode = new Node(position);
        elementDB.addNode(id, newNode);
        // geographicalOperator.addToBaseLayer(newNode);
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
            Street street = new Street(name, wayInfo);
            
            Node[] nodes = new Node[ids.size()];
            for (int i = 0; i < ids.size(); i++) {
                nodes[i] = getNode(ids.get(i));
            }
            street.setNodes(nodes);
            
            elementDB.addMapElement(street);
            geographicalOperator.addToBaseLayer(street);
        } else {
            Area area = new Area(name, wayInfo);
            elementDB.addMapElement(area);
            geographicalOperator.addToBaseLayer(area);
            Node[] nodes = new Node[ids.size()];
            for (int i = 0; i < ids.size(); i++) {
                nodes[i] = elementDB.getNode(ids.get(i));
            }
            area.setNodes(nodes);
            geographicalOperator.addToBaseLayer(area);
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
        POINode newPOI = new POINode(position, description);
        elementDB.addNode(id, newPOI);
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
        POINode newFav = new POINode(pos, description);
        elementDB.addFavorite(newFav);
        //geographicalOperator.addToOverlay(newFav);  //TODO i would prefer to keep this out of quadTree(deleting . . .)
    }

    /**
     * Deletes the favorite in the given area (little area around the coordinate)
     * 
     * @param position
     *            the position of the favorite
     */
    public void deleteFavorite(Coordinates position) {
        elementDB.deleteFavorite(position);
    }

    /**
     * Returns a description of the POI at the given area (little area around the given position)
     * 
     * @param pos
     *            the position of the POI
     * 
     * @return the description of the POI
     */
    public POIDescription getPOIDescription(Coordinates pos, float radius) {
        return geographicalOperator.getPOIDescription(pos, radius);
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
        return elementDB.getNode(nodeID).getPos();
    }
    
    /**
     * Returns the Node with the given ID.
     * 
     * @param nodeID the id of the node
     * @return the corresponding node object.
     */
    // TODO: not design true, but needed by MapElement load methods
    public Node getNode(int nodeID) {
        return elementDB.getNode(nodeID);
    }
    
    /**
     * Returns the MapElement (but no Node!) with the given ID.
     * 
     * @param nodeID the id of the node
     * @return the corresponding node object.
     */
    public MapElement getMapElement(int elementID) {
        return elementDB.getMapElement(elementID);
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
     * @return List<String>
     */
    public List<String> suggestCompletions(String expression) {
        return addressOperator.suggestCompletions(expression);
    }

    /**
     * Operation select
     * 
     * @param address
     * @return Selection
     */
    public Selection select(String address) {
        return addressOperator.select(address);
    }

    /**
     * Returns a selection to a given coordinate.
     * 
     * @param pos the given coordinate
     * @return the correspondenting selection
     */
    public Selection select(Coordinates pos) {
        return geographicalOperator.select(pos);
    }

    /**
     * Return the, to given coordinates, belonging MapElements of the base layer.
     * 
     * @param zoomlevel the zoomlevel of the view
     * @param upLeft the coordinates of the upper left corner of the view
     * @param bottomRight the coordinates of the bottom right corner of the view
     * @return the correspondending mapElements
     */
    public Collection<MapElement> getBaseLayer(int zoomlevel, Coordinates upLeft, Coordinates bottomRight) {
        return geographicalOperator.getBaseLayer(zoomlevel, upLeft, bottomRight);
    }

    /**
     * Return the, to given coordinates, belonging MapElements of the overlay.
     * 
     * @param zoomlevel the zoomlevel of the view
     * @param upLeft the coordinates of the upper left corner of the view
     * @param bottomRight the coordinates of the bottom right corner of the view
     * @return the correspondending mapElements
     */
    public Collection<MapElement> getOverlay(int zoomlevel, Coordinates upLeft, Coordinates bottomRight) {
        return geographicalOperator.getOverlay(zoomlevel, upLeft, bottomRight);
    }

    /**
     * Loads the map from the given stream.
     * 
     * @param stream the source stream.
     * @throws IOException a stream read error occurred
     */
    public void loadFromStream(DataInputStream stream) throws IOException {
        logger.info("load element db...");
        elementDB.loadFromStream(stream);
        logger.info("load geo operator...");
        geographicalOperator.loadFromStream(stream);
        //TODO: load address operator
        //addressOperator.loadFromStream(stream);
    }

    /**
     * Saves the map to the given stream.
     * 
     * @param stream the destination stream.
     * @throws IOException a stream write error occurred
     */
    public void saveToStream(DataOutputStream stream) throws IOException {
        logger.info("save element db...");
        elementDB.saveToStream(stream);
        logger.info("save geo operator...");
        geographicalOperator.saveToStream(stream);
        //TODO: save address operator
        //addressOperator.saveToStream(stream);
    }

    public void printQuadTree() {
        if (geographicalOperator instanceof QTGeographicalOperator) {
            ((QTGeographicalOperator) geographicalOperator).printQuadTree();
        }
    }

    public MapElement[] getAllElements() {
        return elementDB.getAllElements();
    }
    
    public void trimm() {
        geographicalOperator.trimm();
    }
    
    public void swapNodeIds(int id1, int id2) {
        elementDB.swapNodeIds(id1, id2);
        logger.debug("Swapping node " + id1 + " and " + id2);
    }

}
