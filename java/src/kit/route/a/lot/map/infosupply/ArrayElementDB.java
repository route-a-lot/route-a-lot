package kit.route.a.lot.map.infosupply;

import java.io.InputStream;
import java.io.OutputStream;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.map.infosupply.ElementDB;
import kit.route.a.lot.map.MapElement;
import kit.route.a.lot.map.Node;

public class ArrayElementDB implements ElementDB {

    /** Attributes */
    /**
     * 
     */
    private Node[] nodes;
    /**
     * 
     */
    private MapElement[] mapElements;

    @Override
    public Coordinates getNodePosition(int nodeID) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addNode(Node node) {
        // TODO Auto-generated method stub

    }

    @Override
    public Node getNode(int nodeID) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addMapElement(MapElement element) {
        // TODO Auto-generated method stub

    }

    @Override
    public MapElement getMapElement(int id) {
        // TODO Auto-generated method stub
        return null;
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
