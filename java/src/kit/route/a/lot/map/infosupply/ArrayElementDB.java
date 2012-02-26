package kit.route.a.lot.map.infosupply;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.apache.log4j.Logger;

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
    public Node getNode(int id) {
        if (id < 0 || id >= nodes.size()) { 
            throw new IllegalArgumentException("Illegal ID: " + id);
        }
        return nodes.get(id);
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
        //logger.debug("NodeArraySize: " + nodes.size());
    }
    
    
    @Override
    public void addFavorite(POINode favorite) {
        favorites.add(favorite);
        favorite.assignID(favorites.size() - 1);     
    }
    
    @Override
    public void deleteFavorite(Coordinates pos, int detailLevel, int radius) {
        Coordinates topLeft = new Coordinates();
        Coordinates bottomRight = new Coordinates();
        topLeft.setLatitude(pos.getLatitude() - (detailLevel + 1) * 2 * radius);
        topLeft.setLongitude(pos.getLongitude() -(detailLevel + 1) * 2 * radius);
        bottomRight.setLatitude(pos.getLatitude() + (detailLevel + 1) * 2 * radius);
        bottomRight.setLongitude(pos.getLongitude() + (detailLevel + 1) * 2 * radius);
        for (int i = 0; i < favorites.size(); i++) {
            if(favorites.get(i).isInBounds(topLeft, bottomRight)) {
                favorites.remove(i);
            }
        }
    }

    
    @Override
    public void loadFromStream(DataInputStream stream) throws IOException {
        logger.debug("load node array...");
        int len = stream.readInt();
        Node[] nodesArray = new Node[len];
        for (int i = 0; i < len; i++) {
            Node node = (Node) MapElement.loadFromInput(stream, false);
            nodesArray[node.getID()] = node;
        }
        nodes = new ArrayList<Node>(Arrays.asList(nodesArray));
        logger.debug("load map element array...");
        len = stream.readInt();
        mapElements = new ArrayList<MapElement>(len);
        for (int i = 0; i < len; i++) {
            MapElement element = MapElement.loadFromInput(stream, false);
            mapElements.add(element);
            element.assignID(i);
        }
        logger.debug("load favorite array...");
        len = stream.readInt();
        favorites = new ArrayList<POINode>(len);
        for (int i = 0; i < len; i++) {
            POINode favorite = (POINode) MapElement.loadFromInput(stream, false);
            nodes.add(favorite);
            favorite.assignID(i); // TODO: necessary?
        }
    }

    @Override
    public void saveToStream(DataOutputStream stream) throws IOException {  
        logger.info("save node array...");
        stream.writeInt(nodes.size());
        for (Node node: nodes) {
            MapElement.saveToOutput(stream, node, false);
        }
        logger.info("save map element array...");
        stream.writeInt(mapElements.size());
        for (MapElement element: mapElements) {
            MapElement.saveToOutput(stream, element, false);
        }
        logger.info("save favorite array...");
        stream.writeInt(favorites.size());
        for (POINode favorite: favorites) {
            MapElement.saveToOutput(stream, favorite, false);
        }
    }

    @Override
    public void swapNodeIDs(int id1, int id2) {  
        nodes.get(id1).setID(id2);
        nodes.get(id2).setID(id1);
        Collections.swap(nodes, id1, id2);
    }

    @Override
    public POIDescription getFavoriteDescription(Coordinates pos, int detailLevel, int radius) {
        float adaptedRadius = Projection.getZoomFactor(detailLevel) * radius;
        Coordinates UL = new Coordinates(pos.getLatitude() - adaptedRadius, pos.getLongitude() - adaptedRadius);
        Coordinates BR = new Coordinates(pos.getLatitude() + adaptedRadius, pos.getLongitude() + adaptedRadius);      
        for (POINode fav : favorites) {
            if(fav.isInBounds(UL, BR)) {
                return fav.getInfo();
            }
        }
        return null;
    }

    @Override
    public ArrayList<POINode> getFavorites() {
        return favorites;
    }

}
