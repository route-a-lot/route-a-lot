package kit.route.a.lot.map.infosupply;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.POIDescription;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.controller.State;
import kit.route.a.lot.map.MapElement;
import kit.route.a.lot.map.Node;
import kit.route.a.lot.map.POINode;
import kit.route.a.lot.map.Street;
import kit.route.a.lot.map.rendering.Projection;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class QTGeographicalOperator implements GeographicalOperator {

    /** The warning and error console output for this class */
    private static Logger logger = Logger.getLogger(QTGeographicalOperator.class);
    
    static {
        logger.setLevel(Level.INFO);
    }
    
    private static int countZoomlevel = 9;
    
    /** The QuadTrees storing the distributed base layer and overlay, one for each zoom level */
    private QuadTree zoomlevels[] = new QuadTree[countZoomlevel];
    
    public QTGeographicalOperator() {
        for(int i = 0; i < countZoomlevel; i++) {
            zoomlevels[i] = null;
        }
    }
    
    @Override
    public void setBounds(Coordinates upLeft, Coordinates bottomRight) {
        zoomlevels = new QuadTree[countZoomlevel];
        for (int i = 0; i < countZoomlevel; i++) {
            zoomlevels[i] = new QTNode(upLeft, bottomRight);
        }
    }

    @Override
    public void getBounds(Coordinates upLeft, Coordinates bottomRight) {
        if (upLeft != null) {
            upLeft.setLatitude(zoomlevels[0].getUpLeft().getLatitude());
            upLeft.setLongitude(zoomlevels[0].getUpLeft().getLongitude());
        }
        if (bottomRight != null) {
            bottomRight.setLatitude(zoomlevels[0].getBottomRight().getLatitude());
            bottomRight.setLongitude(zoomlevels[0].getBottomRight().getLongitude());
        }
    }

    @Override
    public void buildZoomlevels() {
        //TODO: proper implementation
        MapElement reduced;
        float multiplier = 4;
        MapElement[] elements = State.getInstance().getLoadedMapInfo().getAllElements();
        for (int detail = 1; detail < countZoomlevel; detail++) {
            for (MapElement element: elements) {
                if (element instanceof Node) {
                    continue;
                }
                reduced = element.getReduced(detail, Projection.getZoomFactor(detail) * multiplier);
                if (reduced == null) {
                    logger.debug("Ignoring " + element + " for zoomlevel " + detail);
                } else {
                    if (!zoomlevels[detail].addToBaseLayer(reduced)) {
                        logger.error("Reduced element could not be added to the quadtree.");
                    }
                }
            }
        }
    }

    
    @Override
    public Selection select(Coordinates pos) {
        logger.debug("ClickPositionCoordinates(long/lal): " + pos.getLongitude() + " / " + pos.getLatitude());
        float radius = 1f;
        Selection sel = null;
        while(sel == null && radius < 1000000000) {  //limit for avoiding errors on maps without edges
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
            if(element instanceof Street && ((Street) element).getWayInfo().isRoutable()) {  //TODO only routeable
                float distance = ((Street) element).getDistanceTo(pos);
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestElement = element; 
                } 
            }
        }
        return (closestElement != null) ? ((Street) closestElement).getSelection(pos) : null;
    }
       
    public Collection<MapElement> getBaseLayerForAPositionAndRadius(Coordinates pos, float radius) {
        Coordinates UL = new Coordinates();
        Coordinates BR = new Coordinates();
        UL.setLatitude(pos.getLatitude() - radius);
        UL.setLongitude(pos.getLongitude() - radius);
        BR.setLatitude(pos.getLatitude() + radius);
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

        if (zoomlevel >= countZoomlevel) {
            zoomlevel = countZoomlevel - 1;
        }
        
        HashSet<MapElement> elements = new HashSet<MapElement>();
        zoomlevels[zoomlevel].addBaseLayerElementsToCollection(upLeft, bottomRight, elements);
       // printQuadTree();
        return elements;
    }
    
    @Override
    public Collection<MapElement> getOverlay(int zoomlevel, Coordinates upLeft,
            Coordinates bottomRight) {
        HashSet<MapElement> elements = new HashSet<MapElement>();
        zoomlevels[zoomlevel].addOverlayElementsToCollection(upLeft, bottomRight, elements);
        return elements;
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
        // System.out.println(quadTreeAsString(0));
    }
    
    /*private String quadTreeAsString(int level) {
        return zoomlevels[level].toString(0, new ArrayList<Integer>());
    }*/
    
    private Collection<MapElement> getOverlayForAPositionAndRadius(Coordinates pos, float radius) {
        Coordinates UL = new Coordinates();
        Coordinates BR = new Coordinates();
        UL.setLatitude(pos.getLatitude() - radius);
        UL.setLongitude(pos.getLongitude() - radius);
        BR.setLatitude(pos.getLatitude() + radius);
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
            logger.info("load zoom level " + i + "...");
            zoomlevels[i] = QuadTree.loadFromStream(stream);
        }
    }

    @Override
    public void saveToStream(DataOutputStream stream) throws IOException {
        for(int i = 0; i < zoomlevels.length; i++) {
            logger.info("save zoom level " + i + "...");
            QuadTree.saveToStream(stream, zoomlevels[i]);
        }
    }
    
    /*@Override
    public void getOverlayAndBaseLayer(int zoomlevel, Coordinates upLeft, Coordinates bottomRight,
            Set<MapElement> baseLayer, Set<MapElement> overlay) {
        zoomlevels[zoomlevel].addBaseLayerAndOverlayElementsToCollection(upLeft, bottomRight, baseLayer, overlay);
    }*/

    @Override
    public void trimm() {
        for (int i = 0; i < zoomlevels.length; i++) {
            zoomlevels[i].trimm();
        }
    } 
}
