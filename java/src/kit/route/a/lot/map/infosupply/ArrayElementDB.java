package kit.route.a.lot.map.infosupply;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import kit.route.a.lot.map.infosupply.ElementDB;
import kit.route.a.lot.map.MapElement;
import kit.route.a.lot.map.Node;
import kit.route.a.lot.map.POINode;

public class ArrayElementDB implements ElementDB {

    private static Logger logger = Logger.getLogger(ArrayElementDB.class);
    
    private ArrayList<Node> nodes;
   
    private ArrayList<MapElement> mapElements;
    
    private ArrayList<POINode> favorites;

    private int mapEleID = 0;   //counts mapElements

    
    public ArrayElementDB() {
        nodes = new ArrayList<Node>();
        mapElements = new ArrayList<MapElement>();
    }

    @Override
    public void addNode(int nodeID, Node node) {
        if (nodeID > nodes.size()) {
            throw new IllegalArgumentException("Previous numbers weren't insert, yet");
        }
        if (nodeID < nodes.size()) {
            nodes.remove(nodeID);
        }
        nodes.add(nodeID, node);
        logger.debug("NodeArraySize: " + nodes.size());
    }

    @Override
    public Node getNode(int nodeID) {
        return nodes.get(nodeID);
    }

    @Override
    public void addMapElement(MapElement element) throws IllegalArgumentException {
        mapElements.add(element);
        mapEleID++;
    }

    @Override
    public MapElement getMapElement(int id) throws IllegalArgumentException {
        if (id >= mapElements.size()) { 
            throw new IllegalArgumentException("There's no mapElement with this number");
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
    public void loadFromStream(InputStream stream) {
        // TODO Auto-generated method stub

    }

    @Override
    public void saveToStream(OutputStream stream) {
        // TODO Auto-generated method stub

    }
}
