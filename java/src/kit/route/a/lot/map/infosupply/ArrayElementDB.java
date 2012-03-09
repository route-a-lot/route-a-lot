package kit.route.a.lot.map.infosupply;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import org.apache.log4j.Logger;

import kit.route.a.lot.common.Bounds;
import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.POIDescription;
import kit.route.a.lot.common.Projection;
import kit.route.a.lot.map.infosupply.ElementDB;
import kit.route.a.lot.map.MapElement;
import kit.route.a.lot.map.Node;
import kit.route.a.lot.map.POINode;

public class ArrayElementDB implements ElementDB {

    private static Logger logger = Logger.getLogger(ArrayElementDB.class);   
    
    private ArrayList<Node> nodes = new ArrayList<Node>();   
    private ArrayList<MapElement> mapElements = new ArrayList<MapElement>();    
    private ArrayList<POINode> favorites = new ArrayList<POINode>();
 
    
    // GETTERS
    
    @Override
    public ArrayList<POINode> getFavorites() {
        return favorites;
    }

    @Override
    public Iterator<Node> getAllNodes() {
        return nodes.iterator();
    }

    @Override
    public Iterator<MapElement> getAllMapElements() {
        return mapElements.iterator();
    }

    
    // CONSTRUCTIVE OPERATIONS
    
    @Override
    public void addNode(int nodeID, Node node) {
        if (nodeID != nodes.size()) {
            throw new IllegalArgumentException();
        }
        nodes.add(nodeID, node);
    }
    
    @Override
    public void addMapElement(MapElement element) throws IllegalArgumentException {
        if (element instanceof Node && !(element instanceof POINode)) {
            throw new IllegalArgumentException("Cannot save regular nodes in the map elements database.");
        }
        
        if (mapElements.add(element)) {
            element.setID(mapElements.size() - 1);
        }
    }
    
    @Override
    public void addFavorite(POINode favorite) {
        favorites.add(favorite);
        favorite.setID(favorites.size() - 1);     
    }
    
    @Override
    public void deleteFavorite(Coordinates pos, int detailLevel, int radius) {
        Bounds bounds = new Bounds(pos, (detailLevel + 1) * 2 * radius);
        for (int i = 0; i < favorites.size(); i++) {
            if(favorites.get(i).isInBounds(bounds)) {
                favorites.remove(i);
            }
        }
    }
    
    
    // QUERY OPERATIONS
    
    @Override
    public Node getNode(int id) {
        if (id < 0 || id >= nodes.size()) { 
            throw new IllegalArgumentException("Illegal Node ID: " + id);
        }
        return nodes.get(id);
    }
    
    @Override
    public MapElement getMapElement(int id) {
        if (id < 0 || id >= mapElements.size()) { 
            throw new IllegalArgumentException("Illegal Map Element ID: " + id);
        }
        return mapElements.get(id);
    }
   
    @Override
    public POIDescription getFavoriteDescription(Coordinates pos, int detailLevel, float radius) {
        Bounds bounds = new Bounds(pos, (Projection.getZoomFactor(detailLevel) + 1) * radius); 
        for (POINode fav : favorites) {
            if(fav.isInBounds(bounds)) {
                return fav.getInfo();
            }
        }
        return null;
    }
    
    
    // DIRECTIVE OPERATIONS
    
    @Override
    public void swapNodeIDs(int id1, int id2) {  
        nodes.get(id1).setID(id2);
        nodes.get(id2).setID(id1);
        Collections.swap(nodes, id1, id2);
    }
    

    // I/O OPERATIONS
    
    @Override
    public void loadFromInput(DataInput input) throws IOException {
        logger.debug("load node array...");
        int len = input.readInt();
        Node[] nodesArray = new Node[len];
        for (int i = 0; i < len; i++) {
            Node node = (Node) MapElement.loadFromInput(input);
            nodesArray[node.getID()] = node;
        }
        nodes = new ArrayList<Node>(Arrays.asList(nodesArray));
        logger.debug("load map element array...");
        len = input.readInt();
        mapElements = new ArrayList<MapElement>(len);
        for (int i = 0; i < len; i++) {
            MapElement element = MapElement.loadFromInput(input);
            mapElements.add(element);
            element.setID(i);
        }
        logger.debug("load favorite array...");
        len = input.readInt();
        favorites = new ArrayList<POINode>(len);
        for (int i = 0; i < len; i++) {
            POINode favorite = (POINode) MapElement.loadFromInput(input);
            nodes.add(favorite);
            favorite.setID(i); // TODO: necessary?
        }
    }

    @Override
    public void saveToOutput(DataOutput output) throws IOException {  
        logger.info("save node array...");
        output.writeInt(nodes.size());
        for (Node node: nodes) {
            MapElement.saveToOutput(output, node, false);
        }
        logger.info("save map element array...");
        output.writeInt(mapElements.size());
        for (MapElement element: mapElements) {
            MapElement.saveToOutput(output, element, false);
        }
        logger.info("save favorite array...");
        output.writeInt(favorites.size());
        for (POINode favorite: favorites) {
            MapElement.saveToOutput(output, favorite, false);
        }
    }


    // MISCELLANEOUS
    
    public boolean equals(Object other) {
        if(other == this) {
            return true;
        }
        if(!(other instanceof ArrayElementDB)) {
            return false;
        }
        ArrayElementDB arrayElementDB = (ArrayElementDB) other;
        return nodes.equals(arrayElementDB.nodes)
                && mapElements.equals(arrayElementDB.mapElements)
                && favorites.equals(arrayElementDB.favorites);
    }

}
