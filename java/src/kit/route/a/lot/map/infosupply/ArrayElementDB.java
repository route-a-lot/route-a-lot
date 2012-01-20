package kit.route.a.lot.map.infosupply;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import kit.route.a.lot.map.infosupply.ElementDB;
import kit.route.a.lot.map.MapElement;
import kit.route.a.lot.map.Node;
import kit.route.a.lot.map.POINode;

public class ArrayElementDB implements ElementDB {

    private static Logger logger = Logger.getLogger(ArrayElementDB.class);
    
    private ArrayList<Node> nodes = new ArrayList<Node>();
   
    private ArrayList<MapElement> mapElements = new ArrayList<MapElement>();
    
    private ArrayList<POINode> favorites = new ArrayList<POINode>();

    @Override
    public void addNode(int nodeID, Node node) {
        if (nodeID > nodes.size()) {
            throw new IllegalArgumentException("Previous numbers weren't insert, yet");
        }
        if (nodeID < nodes.size()) {
            nodes.remove(nodeID);
        }
        nodes.add(nodeID, node);
//        logger.debug("NodeArraySize: " + nodes.size());
    }

    @Override
    public Node getNode(int nodeID) {
        return nodes.get(nodeID);
    }

    @Override
    public void addMapElement(MapElement element) throws IllegalArgumentException {
        mapElements.add(element);
    }

    @Override
    public MapElement getMapElement(int id) throws IllegalArgumentException {
        if (id >= mapElements.size()) { 
            throw new IllegalArgumentException("There's no map mlement with this ID.");
        }
        return mapElements.get(id);
    }
    
    @Override
    public void addFavorite(POINode favorite) {
        favorites.add(favorite);
        favorite.initID(favorites.size() - 1);
        
    }
    
    @Override
    public void deleteFavorite(int id) {
        favorites.remove(id);
        favorites.add(id, null); //TODO: some other way for preserving indices
    }

    @Override
    public void loadFromStream(DataInputStream stream) throws IOException {
        int len = stream.readInt();
        nodes = new ArrayList<Node>(len);
        for (int i = 0; i < len; i++) {
            Node node = (Node) MapElement.loadFromStream(stream, false);
            node.initID(i);
            nodes.add(node);
        }
        len = stream.readInt();
        mapElements = new ArrayList<MapElement>(len);
        for (int i = 0; i < len; i++) {
            MapElement element = MapElement.loadFromStream(stream, false);
            element.initID(i);
            mapElements.add(element);
        }
        len = stream.readInt();
        favorites = new ArrayList<POINode>(len);
        for (int i = 0; i < len; i++) {
            POINode favorite = (POINode) MapElement.loadFromStream(stream, false);
            favorite.initID(i);
            nodes.add(favorite);
        }
    }

    @Override
    public void saveToStream(DataOutputStream stream) throws IOException {  
        stream.writeInt(nodes.size());
        for (Node node: nodes) {
            MapElement.saveToStream(stream, node, false);
        }
        stream.writeInt(mapElements.size());
        for (MapElement element: mapElements) {
            MapElement.saveToStream(stream, element, false);
        }
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
}
