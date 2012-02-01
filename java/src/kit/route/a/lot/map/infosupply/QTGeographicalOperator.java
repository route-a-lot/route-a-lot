package kit.route.a.lot.map.infosupply;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.OSMType;
import kit.route.a.lot.common.POIDescription;
import kit.route.a.lot.common.Projection;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.common.WayInfo;
import kit.route.a.lot.map.Area;
import kit.route.a.lot.map.MapElement;
import kit.route.a.lot.map.POINode;
import kit.route.a.lot.map.Street;

import org.apache.log4j.Logger;

public class QTGeographicalOperator implements GeographicalOperator {

    /** The warning and error console output for this class */
    private static Logger logger = Logger.getLogger(QTGeographicalOperator.class);
    
    private static float baseLayerMultiplier = 3;
    
    private static int countZoomlevel = 9;
    
    /** The QuadTrees storing the distributed base layer and overlay, one for each zoom level */
    private QuadTree zoomlevels[] = new QuadTree[countZoomlevel];
    
    public QTGeographicalOperator() {
        for(int i = 0; i < countZoomlevel; i++) {
            zoomlevels[i] = new QTNode(new Coordinates(0,0), new Coordinates(0,0));
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
        if (logger.isTraceEnabled()) {
            logger.trace("called: getBaseLayer()");
            logger.trace(" upLeft: " + upLeft);
            logger.trace(" bottomRight: " + bottomRight);
            logger.trace(" QT Bounds UL Lon: " + zoomlevels[0].getUpLeft().getLongitude());
            logger.trace(" QT Bounds UL Lat: " + zoomlevels[0].getUpLeft().getLatitude());
            logger.trace(" QT Bounds BR Lon: " + zoomlevels[0].getBottomRight().getLongitude());
            logger.trace(" QT Bounds BR Lat: " + zoomlevels[0].getBottomRight().getLatitude());
        }

        if (zoomlevel >= countZoomlevel) {
            zoomlevel = countZoomlevel - 1;
        }
        
        HashSet<MapElement> elements = new HashSet<MapElement>();
        zoomlevels[zoomlevel].addBaseLayerElementsToCollection(upLeft, bottomRight, elements);
        return elements;
    }
    
    @Override
    public Collection<MapElement> getOverlay(int zoomlevel, Coordinates upLeft,
            Coordinates bottomRight) {
        HashSet<MapElement> elements = new HashSet<MapElement>();
        if (zoomlevel >= countZoomlevel) {
            zoomlevel = countZoomlevel - 1;
        }
        zoomlevels[zoomlevel].addOverlayElementsToCollection(upLeft, bottomRight, elements);
        return elements;
    }
        
    @Override
    public void addToBaseLayer(MapElement element) {
        zoomlevels[0].addToBaseLayer(element);
        
        MapElement reduced;
        float multiplier = baseLayerMultiplier;
        int maxZoomlevel = countZoomlevel;
        if (element instanceof Area) {
            if (((Area) element).getWayInfo().isBuilding()) {
                maxZoomlevel = 4;
            }
        }
        if (element instanceof Street) {
            WayInfo wayInfo = ((Street) element).getWayInfo();
            switch (wayInfo.getType()) {
                case OSMType.HIGHWAY_MOTORWAY:
                case OSMType.HIGHWAY_MOTORWAY_JUNCTION:
                case OSMType.HIGHWAY_MOTORWAY_LINK:
                case OSMType.HIGHWAY_PRIMARY:
                case OSMType.HIGHWAY_PRIMARY_LINK:
                case OSMType.HIGHWAY_SECONDARY:
                case OSMType.HIGHWAY_SECONDARY_LINK:
                    break;
                case OSMType.HIGHWAY_TERTIARY:
                case OSMType.HIGHWAY_TERTIARY_LINK:
                case OSMType.HIGHWAY_RESIDENTIAL:
                case OSMType.HIGHWAY_LIVING_STREET:
                case OSMType.HIGHWAY_CYCLEWAY:
                    maxZoomlevel = 8;
                    break;
                default:
                    maxZoomlevel = 6;
            }
        }
        for (int detail = 1; detail < maxZoomlevel; detail++) {
            reduced = element.getReduced(detail, Projection.getZoomFactor(detail) * multiplier);
            if (reduced == null) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Ignoring " + element + " for zoomlevel " + detail);
                }
            } else {
                if (!zoomlevels[detail].addToBaseLayer(reduced)) {
                    logger.error("Reduced element could not be added to the quadtree.");
                }
            }
        }
    }

    @Override
    public void addToOverlay(MapElement element) {
        for (int i = 0; i < countZoomlevel; i++) {
            MapElement reduced = element.getReduced(i, 0);
            if (reduced != null) {
                zoomlevels[i].addToOverlay(reduced);
            }
        }
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
    public POIDescription getPOIDescription(Coordinates pos, float radius, int detailLevel) {
        if (detailLevel > 2) {
            return null;
        }
        Coordinates UL = new Coordinates();
        Coordinates BR = new Coordinates();
        UL.setLatitude(pos.getLatitude() - Projection.getZoomFactor(detailLevel) *  radius);
        UL.setLongitude(pos.getLongitude() -Projection.getZoomFactor(detailLevel) *  radius);
        BR.setLatitude(pos.getLatitude() + Projection.getZoomFactor(detailLevel) * radius);
        BR.setLongitude(pos.getLongitude() + Projection.getZoomFactor(detailLevel) * radius);
        Collection<MapElement> elements = getOverlay(0, UL, BR);
        for (MapElement element : elements) {
            if (element instanceof POINode && element.isInBounds(UL, BR) && ((POINode) element).getInfo().getName() != null
                    && !((POINode) element).getInfo().getName().equals("")) {
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
