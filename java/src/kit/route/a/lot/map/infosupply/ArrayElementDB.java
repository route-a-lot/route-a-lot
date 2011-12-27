package kit.route.a.lot.map.infosupply;

import java.io.InputStream;import java.io.OutputStream;
import java.util.ArrayList;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.map.infosupply.ElementDB;
import kit.route.a.lot.map.MapElement;
import kit.route.a.lot.map.Node;

public class ArrayElementDB implements ElementDB {

    
    private ArrayList<Node> nodes;
   
    private ArrayList<MapElement> mapElements;

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
    public void addNode(Node node) {
        nodes.add(node.getID(), node);
    }

    @Override
    public Node getNode(int nodeID) {
        return nodes.get(nodeID);
    }

    @Override
    public void addMapElement(MapElement element) {
        mapElements.add(element);
        element.setID(mapEleID);
        mapEleID++;

    }

    @Override
    public MapElement getMapElement(int id) {
        return mapElements.get(id);
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
