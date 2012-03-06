package kit.route.a.lot.map.infosupply;

import java.awt.Color;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import kit.route.a.lot.common.Coordinates;
import static kit.route.a.lot.common.OSMType.*;
import kit.route.a.lot.common.POIDescription;
import kit.route.a.lot.common.Projection;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.common.Util;
import kit.route.a.lot.common.WayInfo;
import kit.route.a.lot.controller.State;
import kit.route.a.lot.map.Area;
import kit.route.a.lot.map.MapElement;
import kit.route.a.lot.map.POINode;
import kit.route.a.lot.map.Street;

import org.apache.log4j.Logger;

public class QTGeographicalOperator implements GeographicalOperator {

    private static Logger logger = Logger.getLogger(QTGeographicalOperator.class);
    public static final boolean DRAW_FRAMES = false;
    
    /** The QuadTrees storing the distributed base layer and overlay, one for each zoom level */
    private QuadTree zoomlevels[] = new QuadTree[NUM_LEVELS];
    
    
    public QTGeographicalOperator() {
        setBounds(new Coordinates(), new Coordinates());
    }
    
    
    @Override
    public void setBounds(Coordinates topLeft, Coordinates bottomRight) {
        zoomlevels = new QuadTree[NUM_LEVELS];
        for (int i = 0; i < NUM_LEVELS; i++) {
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
        
        int maxZoomlevel = NUM_LEVELS;
        if (element instanceof Area) {
            if (((Area) element).getWayInfo().isBuilding()) {
                maxZoomlevel = 4;
            }
        }
        if (element instanceof Street) {
            WayInfo wayInfo = ((Street) element).getWayInfo();
            switch (wayInfo.getType()) {
                case HIGHWAY_MOTORWAY:
                case HIGHWAY_MOTORWAY_JUNCTION:
                case HIGHWAY_MOTORWAY_LINK:
                case HIGHWAY_PRIMARY:
                case HIGHWAY_PRIMARY_LINK:
                case HIGHWAY_SECONDARY:
                case HIGHWAY_SECONDARY_LINK:
                    break;
                case HIGHWAY_TERTIARY:
                case HIGHWAY_TERTIARY_LINK:
                case HIGHWAY_RESIDENTIAL:
                case HIGHWAY_LIVING_STREET:
                case HIGHWAY_CYCLEWAY:
                    maxZoomlevel = 8;
                    break;
                default:
                    maxZoomlevel = 6;
            }
        }
        for (int detail = 1; detail < maxZoomlevel; detail++) {
            MapElement reduced = element.getReduced(detail,
                    Projection.getZoomFactor(detail) * LAYER_MULTIPLIER);
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
        for (int i = 0; i < NUM_LEVELS; i++) {
            MapElement reduced = element.getReduced(i, 0);
            if (reduced != null) {
                zoomlevels[i].addToOverlay(reduced);
            }
        }
    }
    
    @Override
    public Set<MapElement> getBaseLayer(int zoomlevel, Coordinates upLeft, Coordinates bottomRight, boolean exact) {
        /*if (logger.isTraceEnabled()) {
            logger.trace("called: getBaseLayer()");
            logger.trace(" upLeft: " + upLeft);
            logger.trace(" bottomRight: " + bottomRight);
            logger.trace(" QT Bounds UL Lon: " + zoomlevels[0].getUpLeft().getLongitude());
            logger.trace(" QT Bounds UL Lat: " + zoomlevels[0].getUpLeft().getLatitude());
            logger.trace(" QT Bounds BR Lon: " + zoomlevels[0].getBottomRight().getLongitude());
            logger.trace(" QT Bounds BR Lat: " + zoomlevels[0].getBottomRight().getLatitude());
        }*/    
        if (QTGeographicalOperator.DRAW_FRAMES) {
            State.getInstance().getActiveRenderer().addFrameToDraw(upLeft, bottomRight, Color.red);
        }
        HashSet<MapElement> elements = new HashSet<MapElement>();
        zoomlevels[Util.clip(zoomlevel, 0, NUM_LEVELS -1)].queryBaseLayer(upLeft, bottomRight, elements, exact);
        return elements;
    }
    
    @Override
    public Set<MapElement> getOverlay(int zoomlevel, Coordinates upLeft, Coordinates bottomRight, boolean exact) {
        HashSet<MapElement> elements = new HashSet<MapElement>();
        zoomlevels[Util.clip(zoomlevel, 0, NUM_LEVELS -1)].queryOverlay(upLeft, bottomRight, elements, exact);
        return elements;
    }
       
    @Override
    public Set<MapElement> getBaseLayer(Coordinates pos, float radius, boolean exact) {
        return getBaseLayer(0, pos.clone().add(-radius, -radius), pos.clone().add(radius, radius), exact);
    }    
       
    
    @Override
    public Selection select(Coordinates pos) {
//        drawFrames = true;
        State.getInstance().getActiveRenderer().resetFramesToDraw();
        float radius = 4;
        Selection sel = null;
        while(sel == null && radius < 1000000000) {  //limit for avoiding errors on maps without edges
            sel = select(pos, radius);
            radius *= 2;  // if we found no edge we have to search in a bigger area TODO optimize factors
        }
        if(sel != null) {
            logger.debug("StartNodeId: " + sel.getFrom());
            logger.debug("EndNodeId: " + sel.getTo());
        }
//        drawFrames = false;
//        State.getInstance().getActiveRenderer().redraw();
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
        Collection<MapElement> elements = getBaseLayer(pos, radius, true);
        
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
        Collection<MapElement> elements = getOverlay(0, UL, BR, true);
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
    public void compactifyDatastructures() {
        for (int i = 0; i < zoomlevels.length; i++) {
            zoomlevels[i].compactifyDataStructures();
        }
    } 
    
    
    @Override
    public void loadFromInput(DataInput input) throws IOException {
        logger.debug("Loading " + zoomlevels.length + " zoomlevels...");
        for(int i = 0; i < zoomlevels.length; i++) {
            logger.trace("load zoom level " + i + "...");
            zoomlevels[i] = QuadTree.loadFromInput(input);
        }
    }

    @Override
    public void saveToOutput(DataOutput output) throws IOException {
        for(int i = 0; i < zoomlevels.length; i++) {
            logger.info("save zoom level " + i + "...");
            QuadTree.saveToOutput(output, zoomlevels[i]);
        }
    }
    

    public boolean equals(Object other) {
        if(other == this) {
            return true;
        }
        if(!(other instanceof QTGeographicalOperator)) {
            return false;
        }
        QTGeographicalOperator comparee = (QTGeographicalOperator) other;
        return java.util.Arrays.equals(zoomlevels, comparee.zoomlevels);
    }
       
    /**
     * Prints a string representing the quadtree.
     */
    public void printQuadTree() {
        System.out.println(zoomlevels[0].toString(0, new ArrayList<Integer>()));
    }
    
}
