package kit.route.a.lot.map.infosupply;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.POIDescription;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.map.MapElement;
import kit.route.a.lot.map.POINode;
import kit.route.a.lot.map.Street;

import org.apache.log4j.Logger;

public class QTGeographicalOperator implements GeographicalOperator {

    /** The warning and error console output for this class */
    private static Logger logger = Logger.getLogger(QTGeographicalOperator.class);
    
    /** The QuadTrees storing the distributed base layer and overlay, one for each zoom level */
    private QuadTree zoomlevels[];
    
    /** The QuadTree leafs that were used by the last query. */
    private Collection<QTLeaf> lastQuery;

    @Override
    public void setBounds(Coordinates upLeft, Coordinates bottomRight) {    //TODO search better solution
        zoomlevels = new QuadTree[9];
        for (int i = 0; i < zoomlevels.length; i++) {
            zoomlevels[i] = new QTNode(upLeft, bottomRight);
        }
    }

    @Override
    public void buildZoomlevels() {
        //TODO: proper implementation
        for (int i = 1; i < zoomlevels.length; i++) {
            zoomlevels[i] = zoomlevels[0];
        }
    }

    
    @Override
    public Selection select(Coordinates pos) {
        logger.debug("ClickPositionCoordinates(long/lal): " + pos.getLongitude() + " / " + pos.getLatitude());
        float radius = 0.01f;
        Selection sel = null;
        while(sel == null && radius < 1000) {  //limit for avoiding errors on maps without edges
            sel = select(pos, radius);
            radius *= 2;  // if we found no edge we have to search in a bigger area TODO optimize factors
        }
        logger.debug("StartNodeId: " + sel.getFrom());
        logger.debug("EndNodeId: " + sel.getTo());
        return sel;
    }
    
    /**
     * Selects the map element nearest to the given position, taking all map elements
     * within a search radius into consideration.
     * 
     * @param pos the given position
     * @param radius the search radius
     * @return a {@link Selection} derived from the nearest map element
     */
    private Selection select(Coordinates pos, float radius) {
        Collection<MapElement> elements = getBaseLayerForAPositionAndRadius(pos, radius);
        
        // find element nearest to pos
        MapElement closestElement = null;
        float closestDistance = Float.MAX_VALUE;  
        for (MapElement element: elements) {
            if(element instanceof Street) { //TODO only routeable streets
                float distance = ((Street) element).getDistanceTo(pos);
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestElement = element; 
                } 
            }
        }
        return (closestElement != null) ? ((Street) closestElement).getSelection(pos) : null;
    }
       
    private Collection<MapElement> getBaseLayerForAPositionAndRadius(Coordinates pos, float radius) {
        Coordinates UL = new Coordinates();
        Coordinates BR = new Coordinates();
        UL.setLatitude(pos.getLatitude() + radius);
        UL.setLongitude(pos.getLongitude() - radius);
        BR.setLatitude(pos.getLatitude() - radius);
        BR.setLongitude(pos.getLongitude() + radius);
        return getBaseLayer(0, UL, BR);
    }
    
    @Override
    public Collection<MapElement> getBaseLayer(int zoomlevel, Coordinates upLeft,
            Coordinates bottomRight) {
        logger.debug("called: getBaseLayer()");
        logger.debug(" upLeft: " + upLeft);
        logger.debug(" bottomRight: " + bottomRight);
        logger.debug(" QT Bounds UL Lon: " + zoomlevels[0].getUpLeft().getLongitude());
        logger.debug(" QT Bounds UL Lat: " + zoomlevels[0].getUpLeft().getLatitude());
        logger.debug(" QT Bounds BR Lon: " + zoomlevels[0].getBottomRight().getLongitude());
        logger.debug(" QT Bounds BR Lat: " + zoomlevels[0].getBottomRight().getLatitude());
        HashSet<MapElement> elements = new HashSet<MapElement>();
        zoomlevels[0].addBaseLayerElementsToCollection(upLeft, bottomRight, elements);
        return elements;
        
// odl method:        
//        Collection<MapElement> mapElements = new HashSet<MapElement>();
//        lastQuery = zoomlevels[0].getLeafs(upLeft, bottomRight);//TODO zoomlevel
//        System.out.println("last query: " + lastQuery);
//        for (QTLeaf qtL : lastQuery) {
//            for (MapElement mapEle : qtL.getBaseLayer()) {
//                mapElements.add(mapEle);
//            }
//        }
//        logger.debug(mapElements);
//        printQuadTree();
//        return mapElements;
        
    }
    
    @Override
    public ArrayList<MapElement> getOverlay(int zoomlevel, Coordinates upLeft,
            Coordinates bottomRight) {
        ArrayList<MapElement> mapElements = new ArrayList<MapElement>();
        for (QTLeaf qtL : zoomlevels[0].getLeafs(upLeft, bottomRight)) {
            for (MapElement mapEle : qtL.getOverlay()) {
                if(mapEle.isInBounds(upLeft, bottomRight) && !mapElements.contains(mapEle)) { //TODO use set
                    mapElements.add(mapEle);
                }
            }
        }
        return mapElements;
    }

    @Override
    public Collection<MapElement> getOverlayToLastBaseLayer(Coordinates upLeft, Coordinates bottomRight) {
        ArrayList<MapElement> mapElements = new ArrayList<MapElement>();
        for (QTLeaf qtL : lastQuery) {
            for (MapElement mapEle : qtL.getBaseLayer()) {
                if(mapEle.isInBounds(upLeft, bottomRight) && !mapElements.contains(mapEle)) { //TODO use set
                    mapElements.add(mapEle); 
                }
            }
        }
        return mapElements;
    }
        
    @Override
    public void addToBaseLayer(MapElement element) {
        zoomlevels[0].addToBaseLayer(element);
    }

    @Override
    public void addToOverlay(MapElement element) {
        zoomlevels[0].addToOverlay(element);
    }
    
    /**
     * prints a string representing the quadtree
     */
    public void printQuadTree() {
        System.out.println(quadTreeAsString(0));
    }
    
    private String quadTreeAsString(int level) {
        return zoomlevels[level].toString(0, new ArrayList<Integer>());
    }
    
    private ArrayList<MapElement> getOverlayForAPositionAndRadius(Coordinates pos, float radius) {
        Coordinates UL = new Coordinates();
        Coordinates BR = new Coordinates();
        UL.setLatitude(pos.getLatitude() + radius);
        UL.setLongitude(pos.getLongitude() - radius);
        BR.setLatitude(pos.getLatitude() - radius);
        BR.setLongitude(pos.getLongitude() + radius);
        return getOverlay(0, UL, BR);
    }
    
    @Override
    public int deleteFavorite(Coordinates pos) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public POIDescription getPOIDescription(Coordinates pos, float radius) {
        Collection<MapElement> elements = getOverlayForAPositionAndRadius(pos, radius);
        for (MapElement element : elements) {
            if (element instanceof POINode) {
                return ((POINode) element).getInfo();
            }
        }
        return null;
    }
    
    @Override
    public void loadFromStream(DataInputStream stream) throws IOException {
        for(int i = 0; i < zoomlevels.length; i++) {
            zoomlevels[i] = QuadTree.loadFromStream(stream);
        }
    }

    @Override
    public void saveToStream(DataOutputStream stream) throws IOException {
        for(int i = 0; i < zoomlevels.length; i++) {
            QuadTree.saveToStream(stream, zoomlevels[i]);
        }
    }
}
