package kit.route.a.lot.map.infosupply;

import static kit.route.a.lot.common.Util.getSharedElementAtEnd;

import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import org.apache.log4j.Logger;


public class MapInfo {

    private static Logger logger = Logger.getLogger(MapInfo.class);

    private ElementDB elementDB;
    private GeographicalOperator geographicalOperator;
    private AddressOperator addressOperator;

    private Coordinates geoTopLeft;
    private Coordinates geoBottomRight;

    private static boolean useDirectFile = false;
    private File outputFile;

    Map<String, Collection<Street>> streetsForAddress = new HashMap<String, Collection<Street>>();

    public MapInfo() {
        elementDB = new ArrayElementDB();
        geographicalOperator = new QTGeographicalOperator();
        addressOperator = new TrieAddressOperator();
        geoTopLeft = new Coordinates();
        geoBottomRight = new Coordinates();
        if (useDirectFile) {
            outputFile = new File("elements.tmp");
            outputFile.deleteOnExit();
            elementDB = new FileElementDB(outputFile);
        }
    }

    /**
     * Constructor
     */
    public MapInfo(Coordinates upLeft, Coordinates bottomRight) {
        this();
        geographicalOperator.setBounds(upLeft, bottomRight);
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
        Node newNode = new Node(position, id);
        if (elementDB == null) {
            throw new IllegalStateException();
        }
        elementDB.addNode(id, newNode);
        // geographicalOperator.addToBaseLayer(newNode);
    }

    // TODO Funktion, um Nodes zu finden, die nicht gebraucht werden, und löschen

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
            if (useDirectFile) {
                for (int i = 0; i < ids.size(); i++) {
                    nodes[i] = new Node(ids.get(i));
                }
                street.setNodes(nodes);
                elementDB.addMapElement(street);
            } else {
                String mapId = wayInfo.getType() + wayInfo.getOneway() + wayInfo.getBicycle()
                                            + wayInfo.getAccess() + wayInfo.getAddress().getStreet();
                Collection<Street> streets = streetsForAddress.get(mapId);

                for (int i = 0; i < ids.size(); i++) {
                    nodes[i] = getNode(ids.get(i));
                }
                street.setNodes(nodes);

                if (streets == null) {
                    streets = new HashSet<Street>();
                    streetsForAddress.put(mapId, streets);
                } else {
                    Iterator<Street> streetsIterator = streets.iterator();
                    while (streetsIterator.hasNext()) {
                        if (mergeStreetsIfPossible(street, streetsIterator.next())) {
                            streetsIterator.remove();
                        }
                    }
                }
                streets.add(street);
                addressOperator.add(street);
            }
        } else {
            Area area = new Area(name, wayInfo);
            Node[] nodes = new Node[ids.size()];
            if (useDirectFile) {
                for (int i = 0; i < ids.size(); i++) {
                    nodes[i] = new Node(ids.get(i));
                }
                area.setNodes(nodes);
                elementDB.addMapElement(area);
            } else {
                for (int i = 0; i < ids.size(); i++) {
                    nodes[i] = elementDB.getNode(ids.get(i));
                }
                area.setNodes(nodes);
                elementDB.addMapElement(area);
                geographicalOperator.addToBaseLayer(area);
            }
        }
    }

    /**
     * If possible (i.e. streets share a node at their end) the nodes from otherStreet are inserted into resultStreet.
     * 
     * @param resultStreet
     * @param otherStreet
     * @return true if the streets were merged into resultStreet
     */
    private boolean mergeStreetsIfPossible(Street resultStreet, Street otherStreet) {
        // TODO also merge WayInfo
        Node[] resultNodes = resultStreet.getNodes();
        List<Integer> resultIds = new ArrayList<Integer>(resultNodes.length);
        Node[] otherNodes = otherStreet.getNodes();
        List<Integer> otherIds = new ArrayList<Integer>(otherNodes.length);
        for (int i = 0; i < resultNodes.length; i++) {
            resultIds.add(resultNodes[i].getID());
        }
        for (int i = 0; i < otherNodes.length; i++) {
            otherIds.add(otherNodes[i].getID());
        }
        Integer sharedId = getSharedElementAtEnd(otherIds, resultIds);
        if (sharedId != null) {
            Node[] newNodes = new Node[otherNodes.length + resultNodes.length - 1];

            int resultStart = resultIds.get(0).equals(sharedId) ? resultIds.size() - 1 : 0;
            int resultStep = resultIds.get(0).equals(sharedId) ? -1 : 1;
            int otherStart = otherIds.get(0).equals(sharedId) ? 1 : otherIds.size() - 2;
            int otherStep = otherIds.get(0).equals(sharedId) ? 1 : -1;

            int newNodeCount = 0;
            for (int i = resultStart; i < resultIds.size() && i >= 0; i += resultStep) {
                newNodes[newNodeCount++] = resultNodes[i];
            }
            for (int i = otherStart; i < otherIds.size() && i >= 0; i += otherStep) {
                newNodes[newNodeCount++] = otherNodes[i];
            }
            resultStreet.setNodes(newNodes);
            return true;
        }
        return false;
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
        POINode newPOI = new POINode(position, description, id);
        elementDB.addNode(id, newPOI);
        geographicalOperator.addToOverlay(newPOI);
        addressOperator.add(newPOI);
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
    }

    /**
     * Deletes the favorite in the given area (little area around the coordinate)
     * 
     * @param position
     *            the position of the favorite
     */
    public void deleteFavorite(Coordinates position, int detailLevel, int radius) {
        elementDB.deleteFavorite(position, detailLevel, radius);
    }


    public POIDescription getFavoriteDescription(Coordinates position, int detailLevel, int radius) {
        return elementDB.getFavoriteDescription(position, detailLevel, radius);
    }

    /**
     * Returns a description of the POI at the given area (little area around the given position)
     * 
     * @param pos
     *            the position of the POI
     * @param radius
     *            the maximum distance a POI may have to <code>pos</code>
     * @param detailLevel
     *            the level of detail currently shown
     * @return the description of the POI
     */
    public POIDescription getPOIDescription(Coordinates pos, float radius, int detailLevel) {
        return geographicalOperator.getPOIDescription(pos, radius, detailLevel);
    }

    /**
     * Returns the coordinates of a given node id.
     * 
     * @param nodeID
     *            the id of the node
     * @return the coordinates of the node correspondenting to the give id.
     */
    public Coordinates getNodePosition(int nodeID) {
        return elementDB.getNode(nodeID).getPos();
    }

    /**
     * Returns the Node with the given ID.
     * 
     * @param nodeID
     *            the id of the node
     * @return the corresponding node object.
     */
    public Node getNode(int nodeID) {
        return elementDB.getNode(nodeID);
    }

    /**
     * Returns the MapElement (but no Node!) with the given ID.
     * 
     * @param nodeID
     *            the id of the node
     * @return the corresponding node object.
     */
    public MapElement getMapElement(int elementID) {
        return elementDB.getMapElement(elementID);
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
     * @param pos
     *            the given coordinate
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
     * @param upLeft
     *            the coordinates of the upper left corner of the view
     * @param bottomRight
     *            the coordinates of the bottom right corner of the view
     * @return the correspondending mapElements
     */
    public Set<MapElement> getBaseLayer(int zoomlevel, Coordinates upLeft, Coordinates bottomRight, boolean exact) {
        return geographicalOperator.getBaseLayer(zoomlevel, upLeft, bottomRight, exact);
    }

    /**
     * Return the, to given coordinates, belonging MapElements of the overlay.
     * 
     * @param zoomlevel
     *            the zoomlevel of the view
     * @param upLeft
     *            the coordinates of the upper left corner of the view
     * @param bottomRight
     *            the coordinates of the bottom right corner of the view
     * @return the correspondending mapElements
     */
    public Set<MapElement> getOverlay(int zoomlevel, Coordinates upLeft, Coordinates bottomRight, boolean exact) {
        Set<MapElement> overlay = geographicalOperator.getOverlay(zoomlevel, upLeft, bottomRight, exact);
        for (MapElement ele : elementDB.getFavorites()) {
            if (ele.isInBounds(upLeft, bottomRight)) {
                overlay.add(ele);
            }
        }
        return overlay;
    }

    /**
     * Loads the map from the given stream.
     * 
     * @param input
     *            the source stream.
     * @throws IOException
     *             a stream read error occurred
     */
    public void loadFromInput(DataInput input) throws IOException {
        geoTopLeft = Coordinates.loadFromInput(input);
        geoBottomRight = Coordinates.loadFromInput(input);
        elementDB.loadFromInput(input);
        geographicalOperator.loadFromInput(input);
     //   addressOperator.loadFromInput(input);
    }

    /**
     * Saves the map to the given stream.
     * 
     * @param output
     *            the destination stream.
     * @throws IOException
     *             a stream write error occurred
     */
    public void saveToOutput(DataOutput output) throws IOException {
        geoTopLeft.saveToOutput(output);
        geoBottomRight.saveToOutput(output);
        elementDB.saveToOutput(output);
        geographicalOperator.saveToOutput(output);
      //  addressOperator.saveToOutput(output);
    }

    public void lastElementAdded() {
        if (!useDirectFile) {
            for (Collection<Street> streets : streetsForAddress.values()) {
                for (Street street : streets) {
                    addressOperator.add(street);
                    elementDB.addMapElement(street);
                    geographicalOperator.addToBaseLayer(street);
                }
            }
            return;
        }
        ((FileElementDB) elementDB).lastElementAdded();
        elementDB = new ArrayElementDB();
        try {
            elementDB.loadFromInput(new DataInputStream(new BufferedInputStream(new FileInputStream(outputFile))));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        streetsForAddress = null;
    }

    public void printQuadTree() {
        if (geographicalOperator instanceof QTGeographicalOperator) {
            ((QTGeographicalOperator) geographicalOperator).printQuadTree();
        }
    }

    public void compactifyDatastructures() {
        // todo pack elementdb as well?
        geographicalOperator.compactifyDatastructures();
    }

    public void swapNodeIds(int id1, int id2) {
        elementDB.swapNodeIDs(id1, id2);
        if (logger.isTraceEnabled()) {
            logger.trace("Swapping node " + id1 + " and " + id2);
        }
    }

    public Collection<MapElement> getBaseLayerForPositionAndRadius(Coordinates pos, float radius, boolean exact) {
        return geographicalOperator.getBaseLayer(pos, radius, exact);
    }

    public Coordinates getGeoTopLeft() {
        return geoTopLeft;
    }

    public void setGeoTopLeft(Coordinates geoTopLeft) {
        this.geoTopLeft = geoTopLeft;
    }

    public Coordinates getGeoBottomRight() {
        return geoBottomRight;
    }

    public void setGeoBottomRight(Coordinates geoBottomRight) {
        this.geoBottomRight = geoBottomRight;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof MapInfo)) {
            return false;
        }
        MapInfo mapInfo = (MapInfo) other;
        return elementDB.equals(mapInfo.elementDB)
                && geographicalOperator.equals(mapInfo.geographicalOperator) // TODO: add compares
                && addressOperator.equals(mapInfo.addressOperator) && geoTopLeft.equals(mapInfo.geoTopLeft)
                && geoBottomRight.equals(mapInfo.geoBottomRight);
    }


}
