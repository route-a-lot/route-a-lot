package kit.route.a.lot.map.infosupply;

import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Collection;

import kit.route.a.lot.common.Coordinates;
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
    public void deleteFavorite(Coordinates pos);
    
    /**
     * Loads all elements from the given stream to the id store. 
     * 
     * @param stream the source stream
     * @throws IOException a stream read error occurred
     */
    public void loadFromStream(DataInputStream stream) throws IOException;

    /**
     * Saves all id stored elements to the given stream. 
     * 
     * @param stream the destination stream
     * @throws IOException a stream write error occurred
     */
    public void saveToStream(DataOutputStream stream) throws IOException;

    public MapElement[] getAllElements();
    
    public boolean isFavorite(Coordinates pos);
    
    public void swapNodeIds(int id1, int id2);
    
    public ArrayList<POINode> getFavorites();

}
