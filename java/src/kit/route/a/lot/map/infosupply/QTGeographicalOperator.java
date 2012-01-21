package kit.route.a.lot.map.infosupply;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.POIDescription;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.controller.State;
import kit.route.a.lot.map.MapElement;
import kit.route.a.lot.map.POINode;
import kit.route.a.lot.map.Street;

import org.apache.log4j.Logger;

public class QTGeographicalOperator implements GeographicalOperator {

    /** The warning and error console output for this class */
    private static Logger logger = Logger.getLogger(QTGeographicalOperator.class);
    
    /** The QuadTrees storing the distributed base layer and overlay, one for each zoom level */
    private QuadTree zoomlevels[];
    
    private static int countZoomlevel = 9;
    
    @Override
    public void setBounds(Coordinates upLeft, Coordinates bottomRight) {    //TODO search better solution
        zoomlevels = new QuadTree[countZoomlevel];
        for (int i = 0; i < countZoomlevel; i++) {
            zoomlevels[i] = new QTNode(upLeft, bottomRight);
        }
    }
    
    @Override
    public void getBounds(Coordinates upLeft, Coordinates bottomRight) {
        upLeft.setLatitude(zoomlevels[0].getUpLeft().getLatitude());
        upLeft.setLongitude(zoomlevels[0].getUpLeft().getLongitude());
        bottomRight.setLatitude(zoomlevels[0].getBottomRight().getLatitude());
        bottomRight.setLongitude(zoomlevels[0].getBottomRight().getLongitude());
    }

    @Override
    public void buildZoomlevels() {
        //TODO: proper implementation
        MapElement reduced;
        float multiplier = 300;
        for (int i = 1; i < countZoomlevel; i++) {
            for (MapElement element: State.getInstance().getLoadedMapInfo().getAllElements()) {
                reduced = element.getReduced(i, i * multiplier);
                if (reduced == null) {
                    logger.info("Ignoring " + element + " for zoomlevel " + i);
                } else {
                    zoomlevels[i].addToBaseLayer(reduced);
                }
            }
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
        System.out.println(quadTreeAsString(0));
    }
    
    private String quadTreeAsString(int level) {
        return zoomlevels[level].toString(0, new ArrayList<Integer>());
    }
    
    private Collection<MapElement> getOverlayForAPositionAndRadius(Coordinates pos, float radius) {
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

    @Override
    public void getOverlayAndBaseLayer(int zoomlevel, Coordinates upLeft, Coordinates bottomRight,
            Set<MapElement> baseLayer, Set<MapElement> overlay) {
        zoomlevels[zoomlevel].addBaseLayerAndOverlayElementsToCollection(upLeft, bottomRight, baseLayer, overlay);
    }

    @Override
    public void trimm() {
        // TODO Auto-generated method stub
        
    }
}
