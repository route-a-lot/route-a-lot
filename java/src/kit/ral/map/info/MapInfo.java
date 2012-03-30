package kit.ral.map.info;

import static kit.ral.common.util.Util.getSharedElementAtEnd;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kit.ral.common.Bounds;
import kit.ral.common.Coordinates;
import kit.ral.common.Selection;
import kit.ral.common.description.Address;
import kit.ral.common.description.POIDescription;
import kit.ral.common.description.WayInfo;
import kit.ral.map.Area;
import kit.ral.map.MapElement;
import kit.ral.map.Node;
import kit.ral.map.POINode;
import kit.ral.map.Street;
import kit.ral.map.info.geo.FileQTGeoOperator;
import kit.ral.map.info.geo.GeographicalOperator;
import kit.ral.map.info.geo.QTGeographicalOperator;

import org.apache.log4j.Logger;


public class MapInfo {

    private static Logger logger = Logger.getLogger(MapInfo.class);

    protected ElementDB elementDB = new ArrayElementDB();
    private AddressOperator addressOperator = new TrieAddressOperator();

    //private GeographicalOperator geoOperator = new FileQTGeoOperator();
    private GeographicalOperator geoOperator = new QTGeographicalOperator();
    
    private Bounds geoBounds = new Bounds();

    private static boolean useDirectFile = false;
    private File elementsFile;

    Map<String, Collection<Street>> streetsForAddress = new HashMap<String, Collection<Street>>();

    
    // CONSTRUCTOR
    
    public MapInfo() {
        if (useDirectFile) {
            try {
                elementsFile = File.createTempFile("elements", null);
                elementsFile.deleteOnExit();         
            } catch (IOException e) {
                e.printStackTrace();
            }
            elementDB = new FileElementDB(elementsFile);
        }
    }

    
    // GETTERS & SETTERS
    
    public void setGeoBounds(Bounds bounds) {
        geoBounds = bounds.clone();
    }
    
    public Coordinates getGeoTopLeft() {
        return geoBounds.getTopLeft();
    }

    public Coordinates getGeoBottomRight() {
        return geoBounds.getBottomRight();
    }
 
    /**
     * Sets the entire area of the map.
     * @param upLeft the upper left corner of the map
     * @param bottomRight the bottom right corner of the area
     */
    public void setBounds(Bounds bounds) {
        geoOperator.setBounds(bounds);
    }

    /**
     * Writes the topLeft and bottomRight values of the current map to the given variables.
     */
    public Bounds getBounds() {
        return geoOperator.getBounds();
    }
    
    
    // CONSTRUCTIVE OPERATIONS
    
    // TODO Funktion, um Nodes zu finden, die nicht gebraucht werden, und löschen
    
    /**
     * Adds a node to the data structures.
     * @param position the position of the node
     * @param id the unique id of the node
     */
    public void addNode(Coordinates position, int id, Address address) {
        Node newNode = new Node(position, id);
        if (elementDB == null) {
            throw new IllegalStateException();
        }
        elementDB.addNode(id, newNode);
    }

    /**
     * Adds a way the the data structures.
     * @param ids the id's of the nodes in the Way (builds edges)
     * @param name the name of the street
     * @param type the type of the street
     */
    public void addWay(List<Integer> ids, String name, WayInfo wayInfo) {
        int i = 0;
        Node[] nodes = new Node[ids.size()];
        if (wayInfo.isStreet()) {
            Street street = new Street(name, wayInfo);
            if (useDirectFile) { 
                for (int id : ids) {
                    nodes[i++] = new Node(id);
                }
                street.setNodes(nodes);
                elementDB.addMapElement(street);
            } else {
                String mapId = wayInfo.getType() + wayInfo.getOneway() + wayInfo.getBicycle()
                                            + wayInfo.getAccess() + wayInfo.getAddress().getStreet();
                Collection<Street> streets = streetsForAddress.get(mapId);
                
                for (int id : ids) {
                    nodes[i++] = getNode(id);
                }
                street.setNodes(nodes);

                if (streets == null) {
                    streets = new HashSet<Street>();
                    streetsForAddress.put(mapId, streets);
                } else if (wayInfo.getAddress().getStreet().length() != 0) {
                    Iterator<Street> streetsIterator = streets.iterator();
                    while (streetsIterator.hasNext()) {
                        if (mergeStreetsIfPossible(street, streetsIterator.next())) {
                            streetsIterator.remove();
                        }
                    }
                }
                streets.add(street);
            }
        } else {  
            Area area = new Area(name, wayInfo);
            if (useDirectFile) {
                for (int id : ids) {
                    nodes[i++] = new Node(id);
                }
            } else {
                for (int id : ids) {
                    nodes[i++] = getNode(id);
                } 
            }          
            area.setNodes(nodes);
            elementDB.addMapElement(area);
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
    public void addPOI(Coordinates position, POIDescription description, Address address) {
        POINode newPOI = new POINode(position, description);
        elementDB.addMapElement(newPOI);
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

    
    // DIRECTIVE OPERATIONS
    
    public void swapNodeIds(int id1, int id2) {
        elementDB.swapNodeIDs(id1, id2);
        if (logger.isTraceEnabled()) {
            logger.trace("Swapping node " + id1 + " and " + id2);
        }
    }
    
    public void lastElementAdded() {
        if (!useDirectFile) {
            for (Collection<Street> streets : streetsForAddress.values()) {
                for (Street street : streets) {
                    elementDB.addMapElement(street);
                    addressOperator.add(street);         
                }
            }
        } else {
            ((FileElementDB) elementDB).lastElementAdded();
//            elementDB = new ArrayElementDB();
//            try {
//                elementDB.loadFromInput(new DataInputStream(new BufferedInputStream(
//                        new FileInputStream(elementsFile))));
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            streetsForAddress = null;
        }
        geoOperator.fill(elementDB);
    }
    
    
    // QUERY OPERATIONS
    
    /**
     * Return the MapElements of the base layer that are within the given boundary.
     * @param zoomlevel the zoomlevel of the view
     * @param topLeft the coordinates of the upper left corner of the view
     * @param bottomRight the coordinates of the bottom right corner of the view
     * @return the correspondending mapElements
     */
    public Set<MapElement> queryElements(int zoomlevel, Bounds area, boolean exact) {
        Set<MapElement> elements = geoOperator.queryElements(area, zoomlevel, exact);
        for (POINode favorite : elementDB.getFavorites()) {
            if (favorite.isInBounds(area)) {
                elements.add(favorite);
            }
        }
        return elements;
    } 
    
    /**
     * Returns a description of the POI at the given area (little area around the given position)
     * @param pos the position of the POI
     * @param radius the maximum distance a POI may have to <code>pos</code>
     * @param detailLevel the level of detail currently shown
     * @return the description of the POI
     */
    public POIDescription getPOIDescription(Coordinates pos, float radius, int detailLevel) {
        POIDescription result = elementDB.getFavoriteDescription(pos, detailLevel, radius);
        return (result != null) ? result : geoOperator.getPOIDescription(pos, radius, detailLevel);
    }    
    
    /**
     * Returns a selection to a given coordinate.
     * @param pos the given coordinate
     * @return the correspondenting selection
     */
    public Selection select(Coordinates pos) {
        return geoOperator.select(pos);
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
     * @param nodeID the id of the node
     * @return the corresponding node object.
     */
    public Node getNode(int nodeID) {
        return elementDB.getNode(nodeID);
    }
    
    /**
     * Returns the MapElement (but no Node!) with the given ID.
     * @param nodeID the id of the node
     * @return the corresponding node object.
     */
    public MapElement getMapElement(int elementID) {
        return elementDB.getMapElement(elementID);
    }
    
    public List<String> getCompletions(String expression) {
        return addressOperator.getCompletions(expression);
    }

    public Selection select(String address) {
        return addressOperator.select(address);
    }


    // I/O OPERATIONS
    
    /**
     * Loads the map from the given stream.
     * 
     * @param input
     *            the source stream.
     * @throws IOException
     *             a stream read error occurred
     */
    public void loadFromInput(DataInput input) throws IOException {
        geoBounds = Bounds.loadFromInput(input);
        elementDB.loadFromInput(input);
        addressOperator.loadFromInput(input);
        geoOperator.loadFromInput(input);        
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
        geoBounds.saveToOutput(output);
        elementDB.saveToOutput(output);
        addressOperator.saveToOutput(output);
        geoOperator.saveToOutput(output);
    }

    public void compactify() {
        // todo pack elementdb as well?
        geoOperator.compactify();
        addressOperator.compactify();
    }
        
    
    // MISCELLANEOUS
    
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

            boolean resultBackwards = resultIds.get(0).equals(sharedId);
            boolean otherBackwards = otherIds.get(0).equals(sharedId);
            
            if (resultBackwards != otherBackwards) {
                int oneway = resultStreet.getWayInfo().getOneway();
                if (oneway != otherStreet.getWayInfo().getOneway()) {
                    logger.error("Oneway of streets to merge is not equal.");
                }
                if (oneway != WayInfo.ONEWAY_NO) {
                    return false;
                }
            }
            
            int resultStart = resultBackwards ? resultIds.size() - 1 : 0;
            int resultStep = resultBackwards ? -1 : 1;
            int otherStart = otherBackwards ? 1 : otherIds.size() - 2;
            int otherStep = otherBackwards ? 1 : -1;

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
    
    public void printQuadTree() {
        if (geoOperator instanceof QTGeographicalOperator) {
            ((QTGeographicalOperator) geoOperator).printQuadTree();
        }
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
                && geoOperator.equals(mapInfo.geoOperator)
                && addressOperator.equals(mapInfo.addressOperator)
                && geoBounds.equals(mapInfo.geoBounds);
    }
  
}
