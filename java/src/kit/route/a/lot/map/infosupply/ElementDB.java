package kit.route.a.lot.map.infosupply;

import java.io.InputStream;
import java.io.OutputStream;

import kit.route.a.lot.map.Node;
import kit.route.a.lot.map.MapElement;
import kit.route.a.lot.map.POINode;

public interface ElementDB {

    /**
     * Adds a node to the elementDB.
     * @param nodeID the ID of the node
     * @param node the node
     */
    public void addNode(int nodeID, Node node);

    /**
     * Returns the Node with the given id.
     * @param nodeID the ID of the node
     * @return the node which relies to the given id
     */
    public Node getNode(int nodeID);

    /**
     * Adds a element to the elementDB and gives it a unique ID.
     * @param element the element
     */
    public void addMapElement(MapElement element);

    /**
     * Returns the element with the given ID  
     * @param id the given ID
     * @return the element which relies to the given ID
     */
    public MapElement getMapElement(int id);

    /**
     * Adds a favorite to the elementDB.
     * @param favorite the favorite which should be added
     */
    public void addFavorite(POINode favorite);
    
    /**
     * Deletes the favorite with the given ID from the elementID.
     * @param id the ID of the favorite
     */
    public void deleteFavorite(int id);
    
    /**
     * Saves the elementDB to the given stream
     * @param stream the stream
     */
    public void loadFromStream(InputStream stream);

    /**
     * Loads a elementID from the give stream.
     * @param stream the stream
     */
    public void saveToStream(OutputStream stream);

}
