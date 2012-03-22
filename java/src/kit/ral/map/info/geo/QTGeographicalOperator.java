package kit.ral.map.info.geo;

import java.awt.Color;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import kit.ral.common.Coordinates;
import static kit.ral.common.description.OSMType.*;
import kit.ral.common.Bounds;
import kit.ral.common.Selection;
import kit.ral.common.description.POIDescription;
import kit.ral.common.description.WayInfo;
import kit.ral.common.projection.Projection;
import kit.ral.common.util.MathUtil;
import kit.ral.controller.State;
import kit.ral.map.Area;
import kit.ral.map.MapElement;
import kit.ral.map.POINode;
import kit.ral.map.Street;
import kit.ral.map.info.ElementDB;

import org.apache.log4j.Logger;

public class QTGeographicalOperator implements GeographicalOperator {

    private static Logger logger = Logger.getLogger(QTGeographicalOperator.class);
    public static boolean drawFrames = false;
    
    protected Bounds bounds;
    
    /** The QuadTrees storing the distributed base layer and overlay, one for each zoom level */
    protected QuadTree trees[];
    
    
    // CONSTRUCTOR
    
    public QTGeographicalOperator() {
        setBounds(new Bounds());
    }
    
    
    // GETTERS & SETTERS
    
    @Override
    public void setBounds(Bounds bounds) {
        this.bounds = bounds.clone();
        trees = new QuadTree[NUM_LEVELS];
    }

    @Override
    public Bounds getBounds() {
        return bounds;
    }
        
    // BASIC OPERATIONS
    
    @Override
    public void fill(ElementDB elementDB) {
        if (elementDB == null) {
            throw new IllegalArgumentException();
        }
        for (int detail = 0; detail < NUM_LEVELS; detail++) {
            trees[detail] = new QTNode(bounds);
        }
        Iterator<MapElement> elements = elementDB.getAllMapElements();
        while (elements.hasNext()) {
            MapElement element = elements.next();
            trees[0].addElement(element);
            int maxLevel = getMaximumZoomlevel(element);
            for (int detail = 1; detail < maxLevel; detail++) {
                MapElement reduced = element.getReduced(detail,
                        Projection.getZoomFactor(detail) * LAYER_MULTIPLIER);
                if (reduced == null) {
                    if (logger.isTraceEnabled()) {
                        logger.trace("Ignoring " + element + " for zoomlevel " + detail);
                    }
                } else {
                    if (!trees[detail].addElement(reduced)) {
                        logger.error("Reduced element could not be added to the quadtree.");
                    }
                }
            }
        }
    }
    
    @Override
    public Set<MapElement> queryElements(Bounds area, int zoomlevel, boolean exact) {
        /*if (logger.isTraceEnabled()) {
            logger.trace("called: getBaseLayer()");
            logger.trace(" upLeft: " + upLeft);
            logger.trace(" bottomRight: " + bottomRight);
            logger.trace(" QT Bounds UL Lon: " + zoomlevels[0].getUpLeft().getLongitude());
            logger.trace(" QT Bounds UL Lat: " + zoomlevels[0].getUpLeft().getLatitude());
            logger.trace(" QT Bounds BR Lon: " + zoomlevels[0].getBottomRight().getLongitude());
            logger.trace(" QT Bounds BR Lat: " + zoomlevels[0].getBottomRight().getLatitude());
        }*/    
        if (drawFrames) {
            State.getInstance().getActiveRenderer().addFrameToDraw(area, Color.red);
        }
        HashSet<MapElement> elements = new HashSet<MapElement>();
        QuadTree tree = trees[MathUtil.clip(zoomlevel, 0, NUM_LEVELS -1)];
        if (tree != null) {
            tree.queryElements(area, elements, exact);
        }
        return elements;
    }
   
    
    // ADVANCED OPERATIONS
    
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
        Collection<MapElement> elements = queryElements(new Bounds(pos, radius), 0, true);
        
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
        Collection<MapElement> elements = queryElements(new Bounds(pos, closestDistance), 0, true);
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
          

    // I/O OPERATIONS
     
    @Override
    public void loadFromInput(DataInput input) throws IOException {
        for(int i = 0; i < NUM_LEVELS; i++) {
            logger.trace("load zoom level " + i + "...");
            trees[i] = QuadTree.loadFromInput(input);
        }
    }

    @Override
    public void saveToOutput(DataOutput output) throws IOException {
        for(int i = 0; i < NUM_LEVELS; i++) {
            logger.info("save zoom level " + i + "...");
            QuadTree.saveToOutput(output, trees[i]);
        }
    }
    
    @Override
    public void compactify() {
        for (QuadTree tree : trees) {
            if(tree != null) {
                tree.compactify();
            }
        }
    } 
    
    
    // MISCELLANEOUS
    
    protected int getMaximumZoomlevel(MapElement element) {
        int result = NUM_LEVELS;
        if (element instanceof Area) {
            if (((Area) element).getWayInfo().isBuilding()) {
                result = 4;
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
                    result = 8;
                    break;
                default:
                    result = 6;
            }
        }
        return MathUtil.clip(result, 0, NUM_LEVELS);
    }
    
    public boolean equals(Object other) {
        if(other == this) {
            return true;
        }
        if(!(other instanceof QTGeographicalOperator)) {
            return false;
        }
        QTGeographicalOperator comparee = (QTGeographicalOperator) other;
        return java.util.Arrays.equals(trees, comparee.trees);
    }
       
    /**
     * Prints a string representing the quadtree.
     */
    public void printQuadTree() {
        System.out.println(trees[0].toString(0, new ArrayList<Integer>()));
    }


 
}
