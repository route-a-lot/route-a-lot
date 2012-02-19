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
import kit.route.a.lot.common.Util;
import kit.route.a.lot.common.WayInfo;
import kit.route.a.lot.map.Area;
import kit.route.a.lot.map.MapElement;
import kit.route.a.lot.map.POINode;
import kit.route.a.lot.map.Street;

import org.apache.log4j.Logger;

public class QTGeographicalOperator implements GeographicalOperator {

    private static Logger logger = Logger.getLogger(QTGeographicalOperator.class);
    private float baseLayerMultiplier = 3;
    private int countZoomlevel = 9;
    /** The QuadTrees storing the distributed base layer and overlay, one for each zoom level */
    private QuadTree zoomlevels[] = new QuadTree[countZoomlevel];
    
    public QTGeographicalOperator() {
        setBounds(new Coordinates(), new Coordinates());
    }
    
    public boolean equals(Object other) {
        if(other == this) {
            return true;
        }
        if(!(other instanceof QTGeographicalOperator)) {
            return false;
        }
        QTGeographicalOperator comparee = (QTGeographicalOperator) other;
        return baseLayerMultiplier == comparee.baseLayerMultiplier
                && countZoomlevel == comparee.countZoomlevel
                && java.util.Arrays.equals(zoomlevels, comparee.zoomlevels);
        
    }
    @Override
    public void setBounds(Coordinates topLeft, Coordinates bottomRight) {
        zoomlevels = new QuadTree[countZoomlevel];
        for (int i = 0; i < countZoomlevel; i++) {
            zoomlevels[i] = new QTNode(topLeft, bottomRight);
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
      
    @Override
    public Collection<MapElement> getBaseLayer(int zoomlevel, Coordinates upLeft, Coordinates bottomRight) {
        /*if (logger.isTraceEnabled()) {
            logger.trace("called: getBaseLayer()");
            logger.trace(" upLeft: " + upLeft);
            logger.trace(" bottomRight: " + bottomRight);
            logger.trace(" QT Bounds UL Lon: " + zoomlevels[0].getUpLeft().getLongitude());
            logger.trace(" QT Bounds UL Lat: " + zoomlevels[0].getUpLeft().getLatitude());
            logger.trace(" QT Bounds BR Lon: " + zoomlevels[0].getBottomRight().getLongitude());
            logger.trace(" QT Bounds BR Lat: " + zoomlevels[0].getBottomRight().getLatitude());
        }*/    
        HashSet<MapElement> elements = new HashSet<MapElement>();
        zoomlevels[Util.clip(zoomlevel, 0, countZoomlevel -1)].queryBaseLayer(upLeft, bottomRight, elements);
        return elements;
    }
    
    @Override
    public Collection<MapElement> getOverlay(int zoomlevel, Coordinates upLeft, Coordinates bottomRight) {
        HashSet<MapElement> elements = new HashSet<MapElement>();
        zoomlevels[Util.clip(zoomlevel, 0, countZoomlevel -1)].queryOverlay(upLeft, bottomRight, elements);
        return elements;
    }
       
    @Override
    public Collection<MapElement> getBaseLayer(Coordinates pos, float radius) {
        return getBaseLayer(0, pos.clone().add(-radius, -radius), pos.clone().add(radius, radius));
    }    
    
    /*private Collection<MapElement> getOverlay(Coordinates pos, float radius) {
        Coordinates UL = new Coordinates(pos.getLatitude() - radius, pos.getLongitude() - radius);
        Coordinates BR = new Coordinates(pos.getLatitude() + radius, pos.getLongitude() + radius);
        return getOverlay(0, UL, BR);
    }
    
    @Override
    public void getOverlayAndBaseLayer(int zoomlevel, Coordinates upLeft, Coordinates bottomRight,
            Set<MapElement> baseLayer, Set<MapElement> overlay) {
        zoomlevels[zoomlevel].addBaseLayerAndOverlayElementsToCollection(upLeft, bottomRight, baseLayer, overlay);
    }*/
    
    
    @Override
    public Selection select(Coordinates pos) {
        float radius = 1;
        Selection sel = null;
        while(sel == null && radius < 1000000000) {  //limit for avoiding errors on maps without edges
            sel = select(pos, radius);
            radius *= 2;  // if we found no edge we have to search in a bigger area TODO optimize factors
        }
        if(sel != null) {
            logger.debug("StartNodeId: " + sel.getFrom());
            logger.debug("EndNodeId: " + sel.getTo());
        }
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
        Collection<MapElement> elements = getBaseLayer(pos, radius);
        
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
       
    @Override
    public POIDescription getPOIDescription(Coordinates pos, float radius, int detailLevel) {
        if (detailLevel > 2) {
            return null;
        }
        POINode closestElement = null;
        float closestDistance = (Projection.getZoomFactor(detailLevel) + 1) *  radius;
        Coordinates UL = pos.clone().add(-closestDistance, -closestDistance);
        Coordinates BR = pos.clone().add(closestDistance, closestDistance);
        Collection<MapElement> elements = getOverlay(0, UL, BR);
        for (MapElement element : elements) {
            if ((element instanceof POINode) && !((POINode) element).getInfo().getName().equals("")) {
                float newDistance = (float) Coordinates.getDistance(pos, ((POINode) element).getPos());
                if (newDistance < closestDistance) {
                    closestElement = (POINode) element;
                    closestDistance = newDistance;
                }
            }
        }
        return (closestElement == null) ? null : closestElement.getInfo();
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
    

    @Override
    public void compactifyDatastructures() {
        for (int i = 0; i < zoomlevels.length; i++) {
            zoomlevels[i].compactifyDataStructures();
        }
    } 
       
    /**
     * Prints a string representing the quadtree.
     */
    public void printQuadTree() {
        System.out.println(zoomlevels[0].toString(0, new ArrayList<Integer>()));
    }
    
}
