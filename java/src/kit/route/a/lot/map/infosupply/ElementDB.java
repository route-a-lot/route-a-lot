package kit.route.a.lot.map.infosupply;

import java.io.InputStream;
import java.io.OutputStream;

import kit.route.a.lot.map.Node;
import kit.route.a.lot.map.MapElement;
import kit.route.a.lot.map.POINode;

public interface ElementDB {

    public void addNode(int nodeID, Node node);

    public Node getNode(int nodeID);

    public void addMapElement(MapElement element);

    public MapElement getMapElement(int id);

    public void addFavorite(POINode favorite);
    
    public void deleteFavorite(int id);
    
    public void loadFromStream(InputStream stream);

    public void saveToStream(OutputStream stream);

}
