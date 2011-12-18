package kit.route.a.lot.map.infosupply;

import java.io.InputStream;
import java.io.OutputStream;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.map.Node;
import kit.route.a.lot.map.MapElement;

public interface ElementDB {

    /**
     * Operation getNodePosition
     * 
     * @param nodeID
     *            -
     * @return Coordinates
     */
    Coordinates getNodePosition(int nodeID);

    /**
     * Operation addNode
     * 
     * @param node
     *            -
     * @return
     * @return
     */
    void addNode(Node node);

    /**
     * Operation getNode
     * 
     * @param nodeID
     *            -
     * @return Node
     */
    Node getNode(int nodeID);

    /**
     * Operation addMapElement
     * 
     * @param element
     *            -
     * @return
     * @return
     */
    void addMapElement(MapElement element);

    /**
     * Operation getMapElement
     * 
     * @param id
     *            -
     * @return MapElement
     */
    MapElement getMapElement(int id);

    /**
     * Operation loadFromStream
     * 
     * @param stream
     *            -
     * @return
     * @return
     */
    void loadFromStream(InputStream stream);

    /**
     * Operation saveToStream
     * 
     * @param stream
     *            -
     * @return
     * @return
     */
    void saveToStream(OutputStream stream);

}
