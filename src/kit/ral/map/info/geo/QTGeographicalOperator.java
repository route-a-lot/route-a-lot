
/**
Copyright (c) 2012, Matthias Grundmann, Jan Jacob, Daniel Krauß, Josua Stabenow
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
        * Redistributions of source code must retain the above copyright
          notice, this list of conditions and the following disclaimer.
        * Redistributions in binary form must reproduce the above copyright
          notice, this list of conditions and the following disclaimer in the
          documentation and/or other materials provided with the distribution.
        * The names of the contributors may not be used to endorse or promote products
          derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
**/

package kit.ral.map.info.geo;

import kit.ral.common.*;
import kit.ral.common.description.POIDescription;
import kit.ral.common.description.WayInfo;
import kit.ral.common.projection.Projection;
import kit.ral.common.util.MathUtil;
import kit.ral.controller.State;
import kit.ral.map.*;
import kit.ral.map.info.ElementDB;
import org.apache.log4j.Logger;

import java.awt.*;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.*;

import static kit.ral.common.description.OSMType.*;

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
        TreeSet<MapElement> elements = new TreeSet<MapElement>(new MapElementComparator());
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
            if(element instanceof Street && ((Street) element).getWayInfo().isRoutable()) {
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
        logger.info("load QT");
        if (!(input instanceof RandomReadStream)) {
            throw new IllegalArgumentException();
        }
        RandomReadStream source = (RandomReadStream) input;
        bounds = Bounds.loadFromInput(source);
        
        // load location table
        long[] table = new long[NUM_LEVELS];
        for (int d = 0; d < NUM_LEVELS; d++) {
            table[d] = source.readLong();
        }
        
        // load trees
        for(int d = 0; d < NUM_LEVELS; d++) {
            logger.info("QT level "+ d + "/" + NUM_LEVELS);
            source.setPosition(table[d]);
            trees[d] = QuadTree.loadFromInput(source);
        }
    }

    @Override
    public void saveToOutput(DataOutput output) throws IOException {
        logger.info("save QT");
        if (!(output instanceof RandomWriteStream)) {
            throw new IllegalArgumentException();
        }
        RandomWriteStream target = (RandomWriteStream) output;
        bounds.saveToOutput(target);
        
        // tree location table reservation
        long[] table = new long[NUM_LEVELS];
        long treeTableOffset = target.getPosition();
        for (int d = 0; d < NUM_LEVELS; d++) {
            target.writeLong(0);
        }
        
        // save trees
        for (int d = 0; d < NUM_LEVELS; d++) {
            logger.info("QT level "+ d + "/" + NUM_LEVELS);
            table[d] = target.getPosition();          
            QuadTree.saveToOutput(target, trees[d]);               
        }
        
        // fill tree location table
        for (int d = 0; d < NUM_LEVELS; d++) {
            target.writeLongToPosition(table[d], treeTableOffset + d * 8);
        }
    }
    
    @Override
    public void compactify() {
    } 
    
    
    // MISCELLANEOUS
    
    protected int getMaximumZoomlevel(MapElement element) {
        int result = NUM_LEVELS - 1;
        if (element instanceof Area) {
            if (((Area) element).getWayInfo().isBuilding()) {
                result = 4;
            }
        } else if (element instanceof Street) {
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
        } else if (element instanceof POINode) {
            result = 2;
        }
        return MathUtil.clip(result, 0, NUM_LEVELS - 1);
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
