package kit.route.a.lot.map.infosupply;

import java.io.InputStream;import java.io.OutputStream;import java.util.ArrayList;

import org.apache.log4j.Logger;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.map.infosupply.ElementDB;
import kit.route.a.lot.map.MapElement;
import kit.route.a.lot.map.Node;
import kit.route.a.lot.map.POINode;

public class ArrayElementDB implements ElementDB {

    private static Logger logger = Logger.getLogger(ArrayElementDB.class);
    
    private ArrayList<Node> nodes;
   
    private ArrayList<MapElement> mapElements;
    
    private ArrayList<POINode> favorites;

    private int mapEleID = 0;   //counts mapElemnts

    
    public ArrayElementDB() {
        nodes = new ArrayList<Node>();
        mapElements = new ArrayList<MapElement>();
    }

    @Override
    public Coordinates getNodePosition(int nodeID) {
        return nodes.get(nodeID).getPos();
    }

    @Override
    public void addNode(Node node) throws IllegalArgumentException {
        if (node.getID() > nodes.size()) {
            throw new IllegalArgumentException("Previous numbers weren't insert, yet");
        }
        nodes.add(node.getID(), node);
        logger.debug("NodeArraySize: " + nodes.size());
    }

    @Override
    public Node getNode(int nodeID) {
        return nodes.get(nodeID);
    }

    @Override
    public void addMapElement(MapElement element) throws IllegalArgumentException {
        mapElements.add(element);
        element.setID(mapEleID);
        mapEleID++;

    }

    @Override
    public MapElement getMapElement(int id) throws IllegalArgumentException {
        if (id >= mapElements.size()) { 
            throw new IllegalArgumentException("There's no mapElement with this number");
        }
        return mapElements.get(id);
    }
    
    public void addFavorite(POINode favorite) {
        favorites.add(favorite);
    }
    
    public void deleteFavorite(int id) {
        favorites.remove(new POINode(id, null, null));
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
