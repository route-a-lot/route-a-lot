package kit.route.a.lot.map.infosupply;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.map.Node;
import kit.route.a.lot.map.MapElement;

public interface ElementDB

{
    /**
     * Operation getNodePosition
     *
     * @param nodeID - 
     * @return Coordinates
     */
    protected Coordinates getNodePosition ( int nodeID );

    /**
     * Operation addNode
     *
     * @param node - 
     * @return 
     */
    protected addNode ( Node node );

    /**
     * Operation getNode
     *
     * @param nodeID - 
     * @return Node
     */
    protected Node getNode ( int nodeID );

    /**
     * Operation addMapElement
     *
     * @param element - 
     * @return 
     */
    protected addMapElement ( MapElement element );

    /**
     * Operation getMapElement
     *
     * @param id - 
     * @return MapElement
     */
    protected MapElement getMapElement ( int id );

    /**
     * Operation loadFromStream
     *
     * @param stream - 
     * @return 
     */
    protected loadFromStream ( InputStream stream );

    /**
     * Operation saveToStream
     *
     * @param stream - 
     * @return 
     */
    protected saveToStream ( OutputStream stream );

}

