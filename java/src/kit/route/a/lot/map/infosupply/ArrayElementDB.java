package kit.route.a.lot.map.infosupply;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.map.infosupply.ElementDB;
import kit.route.a.lot.map.MapElement;
import kit.route.a.lot.map.Node;
import kit.route.a.lot.map.POINode;
import kit.route.a.lot.controller.State;

public class ArrayElementDB implements ElementDB {

    private static Logger logger = Logger.getLogger(ArrayElementDB.class);
    
    static {
        logger.setLevel(Level.INFO);
    }
    
    private ArrayList<Node> nodes = new ArrayList<Node>();
   
    private ArrayList<MapElement> mapElements = new ArrayList<MapElement>();
    
    private ArrayList<POINode> favorites = new ArrayList<POINode>();
 
    
    @Override
    public MapElement getMapElement(int id) throws IllegalArgumentException {
        if (id < 0 || id >= mapElements.size()) { 
            throw new IllegalArgumentException("Illegal ID: " + id);
        }
        return mapElements.get(id);
    }
    
    @Override
    public void addMapElement(MapElement element) throws IllegalArgumentException {
        if (mapElements.add(element)) {
            element.assignID(mapElements.size() - 1);
        }
    }

    
    @Override
    public Node getNode(int nodeID) {
        return nodes.get(nodeID);
    }
    
    @Override
    public void addNode(int nodeID, Node node) {
        if (nodeID > nodes.size()) {
            throw new IllegalStateException("Node ID out of range: " + nodeID);
        }
        if (nodeID < nodes.size()) {
            nodes.remove(nodeID);
            throw new IllegalStateException("Node ID conflict: " + nodeID);
        }
        nodes.add(nodeID, node);
        node.assignID(nodeID);
        //logger.debug("NodeArraySize: " + nodes.size());
    }
    
    
    @Override
    public void addFavorite(POINode favorite) {
        favorites.add(favorite);
        favorite.assignID(favorites.size() - 1);     
    }
    
    @Override
    public void deleteFavorite(Coordinates pos) {
        for (int i = 0; i < favorites.size(); i++) {
            Coordinates topLeft = new Coordinates();
            Coordinates bottomRight = new Coordinates();
            topLeft.setLatitude(pos.getLatitude() - State.getInstance().getClickRadius());
            topLeft.setLongitude(pos.getLatitude() - State.getInstance().getClickRadius());
            bottomRight.setLatitude(pos.getLatitude() + State.getInstance().getClickRadius());
            topLeft.setLongitude(pos.getLatitude() + State.getInstance().getClickRadius());
            if(favorites.get(i).isInBounds(topLeft, bottomRight)) {
                favorites.remove(i);
                favorites.add(i, null); //TODO: some other way for preserving indices
            }
        }
    }

    
    @Override
    public void loadFromStream(DataInputStream stream) throws IOException {
        logger.info("load node array...");
        int len = stream.readInt();
        nodes = new ArrayList<Node>(len);
        for (int i = 0; i < len; i++) {
            Node node = (Node) MapElement.loadFromStream(stream, false);
            nodes.add(node);
            node.assignID(i);
        }
        logger.info("load map element array...");
        len = stream.readInt();
        mapElements = new ArrayList<MapElement>(len);
        for (int i = 0; i < len; i++) {
            MapElement element = MapElement.loadFromStream(stream, false);
            mapElements.add(element);
            element.assignID(i);
        }
        logger.info("load favorite array...");
        len = stream.readInt();
        favorites = new ArrayList<POINode>(len);
        for (int i = 0; i < len; i++) {
            POINode favorite = (POINode) MapElement.loadFromStream(stream, false);
            nodes.add(favorite);
            favorite.assignID(i); // TODO: necessary?
        }
    }

    @Override
    public void saveToStream(DataOutputStream stream) throws IOException {  
        logger.info("save node array...");
        stream.writeInt(nodes.size());
        for (Node node: nodes) {
            MapElement.saveToStream(stream, node, false);
        }
        logger.info("save map element array...");
        stream.writeInt(mapElements.size());
        for (MapElement element: mapElements) {
            MapElement.saveToStream(stream, element, false);
        }
        logger.info("save favorite array...");
        stream.writeInt(favorites.size());
        for (POINode favorite: favorites) {
            MapElement.saveToStream(stream, favorite, false);
        }
    }

    
    @Override
    public MapElement[] getAllElements() {
        List<MapElement> result = new ArrayList<MapElement>();
        MapElement[] resultArray = new MapElement[nodes.size() + mapElements.size() + favorites.size()];
        result.addAll(nodes);
        result.addAll(mapElements);
        result.addAll(favorites);
        return (MapElement[]) result.toArray(resultArray);
    }

    @Override
    public void swapNodeIds(int id1, int id2) {  
        nodes.get(id1).setID(id2);
        nodes.get(id2).setID(id1);
        Collections.swap(nodes, id1, id2);
    }
}
