package kit.route.a.lot.map.infosupply;

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
                if (!findAndChangeStreetIfPossible(ids, name, wayInfo)) {
//                    elementDB.addMapElement(street);
//                    geographicalOperator.addToBaseLayer(street);
                }
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
     * Searches for a street that has the same street name as wayInfo
     *      if a street is found and the street and the given id list share an id the found street is changed and true returned
     * 
     * @param ids
     * @param wayInfo
     * @return true if the street was changed
     */
    private boolean findAndChangeStreetIfPossible(List<Integer> ids, String name, WayInfo wayInfo) {
        String streetName = wayInfo.getAddress().getStreet();
        Collection<Street> streets = streetsForAddress.get(streetName);
        
        Street street = new Street(name, wayInfo);
        Node[] nodes = new Node[ids.size()];
        for (int i = 0; i < ids.size(); i++) {
            nodes[i] = getNode(ids.get(i));
        }
        street.setNodes(nodes);

        if (streets == null) {
            streets = new HashSet<Street>();
            streetsForAddress.put(streetName, streets);
        } else {
            for (Street otherStreet : streets) {
                if (mergeStreetsIfPossible(street, otherStreet)) {
                    streets.remove(otherStreet);
                }
            }
        }
        streets.add(street);
        addressOperator.add(street);
        return false;
    }

    private boolean mergeStreetsIfPossible(Street resultStreet, Street otherStreet) {
        Node[] resultNodes = resultStreet.getNodes();
        List<Integer> resultIds = new ArrayList<Integer>(resultNodes.length);
        Node[] otherNodes = otherStreet.getNodes();
        List<Integer> otherIds = new ArrayList<Integer>(otherNodes.length);
        Node[] newNodes = new Node[otherNodes.length + resultNodes.length];
        for (int i = 0; i < otherNodes.length; i++) {
            newNodes[i] = otherNodes[i];
            otherIds.add(otherNodes[i].getID());
        }
        if (idListsShareAnId(otherIds, resultIds)) {
            Iterator<Integer> idsIterator = resultIds.iterator();
            for (int i = otherNodes.length; i < newNodes.length; i++) {
                newNodes[i] = getNode(idsIterator.next());
            }
            resultStreet.setNodes(newNodes);
            return true;
        }
        return false;
    }

    private boolean idListsShareAnId(List<Integer> list1, List<Integer> list2) {
        // TODO algorithm could be in n*log n instead of n^2, but it has to be performance tested, if that would really be faster
        for (Integer int1 : list1) {
            for (Integer int2 : list2) {
                if (int1.equals(int2)) {
                    return true;
                }
            }
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
     * @param pos the position of the POI
     * @param radius the maximum distance a POI may have to <code>pos</code>
     * @param detailLevel the level of detail currently shown
     * @return the description of the POI
     */
    public POIDescription getPOIDescription(Coordinates pos, float radius, int detailLevel) {
        return geographicalOperator.getPOIDescription(pos, radius, detailLevel);
    }

    /**
     * Returns the coordinates of a given node id.
     * @param nodeID the id of the node
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
    public Collection<MapElement> getBaseLayer(int zoomlevel, Coordinates upLeft, Coordinates bottomRight, boolean exact) {
        return geographicalOperator.getBaseLayer(zoomlevel, upLeft, bottomRight, exact);
    }

    /**
     * Return the, to given coordinates, belonging MapElements of the overlay.
     * 
     * @param zoomlevel the zoomlevel of the view
     * @param upLeft the coordinates of the upper left corner of the view
     * @param bottomRight the coordinates of the bottom right corner of the view
     * @return the correspondending mapElements
     */
    public Collection<MapElement> getOverlay(int zoomlevel, Coordinates upLeft, Coordinates bottomRight, boolean exact) {
        Collection<MapElement> overlay = geographicalOperator.getOverlay(zoomlevel, upLeft, bottomRight, exact);
        for(MapElement ele : elementDB.getFavorites()) {
            if(ele.isInBounds(upLeft, bottomRight)) {
                overlay.add(ele);
            }
        }
        return overlay;
    }

    /**
     * Loads the map from the given stream.
     * 
     * @param input the source stream.
     * @throws IOException a stream read error occurred
     */
    public void loadFromInput(DataInput input) throws IOException {
        geoTopLeft = Coordinates.loadFromInput(input);
        geoBottomRight = Coordinates.loadFromInput(input);
        elementDB.loadFromInput(input);
        geographicalOperator.loadFromInput(input);
        addressOperator.loadFromInput(input);
    }

    /**
     * Saves the map to the given stream.
     * 
     * @param output the destination stream.
     * @throws IOException a stream write error occurred
     */
    public void saveToOutput(DataOutput output) throws IOException {
        geoTopLeft.saveToOutput(output);
        geoBottomRight.saveToOutput(output);
        elementDB.saveToOutput(output);
        geographicalOperator.saveToOutput(output);
        addressOperator.saveToOutput(output);
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
            elementDB.loadFromInput(new DataInputStream(
                    new BufferedInputStream(new FileInputStream(outputFile))));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        if(other == this) {
            return true;
        }
        if(!(other instanceof MapInfo)) {
            return false;
        }
        MapInfo mapInfo = (MapInfo) other;
        return elementDB.equals(mapInfo.elementDB) 
                && geographicalOperator.equals(mapInfo.geographicalOperator)    //TODO: add compares
                && addressOperator.equals(mapInfo.addressOperator)
                && geoTopLeft.equals(mapInfo.geoTopLeft)
                && geoBottomRight.equals(mapInfo.geoBottomRight);
    }
               

}
