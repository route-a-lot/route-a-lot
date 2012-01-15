package kit.route.a.lot.map.infosupply;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.POIDescription;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.map.MapElement;

import org.apache.log4j.Logger;

public class QTGeographicalOperator implements GeographicalOperator {

    /** The warning and error console output for this class */
    private static Logger logger = Logger.getLogger(QTGeographicalOperator.class);
    
    /** The QuadTrees storing the distributed base layer and overlay, one for each zoom level */
    private QuadTree zoomlevels[];
    
    /** The QuadTree leafs that were used by the last query. */
    private Collection<QTLeaf> lastQuery;

    @Override
    public void setBounds(Coordinates upLeft, Coordinates bottomRight) {
        zoomlevels = new QuadTree[9];
        for (int i = 0; i < zoomlevels.length; i++) {
            zoomlevels[i] = new QTLeaf(upLeft, bottomRight);
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
        float radius = 0.01f;
        Selection sel = null;
        while(sel == null && radius < 1000) {  //limit for avoiding errors on maps without edges
            sel = select(pos, radius);
            radius *= 10;  // if we found no edge we have to search in a bigger area
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
        // get all elements coming into question
        Collection<MapElement> elements = getBaseLayer(0,
                new Coordinates (pos.getLongitude() - radius, pos.getLatitude() + radius),
                new Coordinates(pos.getLongitude() + radius, pos.getLatitude() - radius));
        
        // find element nearest to pos
        MapElement closestElement = null;
        float closestDistance = Float.MAX_VALUE;  
        for (MapElement element: elements) {
            float distance = element.getDistanceTo(pos);
            if (distance < closestDistance) {
                closestDistance = distance;
                closestElement = element; 
            } 
        }
        
        // create and return selection from the element
        return (closestElement != null) ? closestElement.getSelection(pos) : null;
    }
    
    
    /*private Selection select(Coordinates pos, float radius) {
        Selection selection = null;
        Coordinates newUL = new Coordinates();  //Bounds for area to search in
        Coordinates newBR = new Coordinates();
        newUL.setLatitude(pos.getLatitude() + radius);
        newUL.setLongitude(pos.getLongitude() - radius);
        newBR.setLatitude(pos.getLatitude() - radius);
        newBR.setLongitude(pos.getLongitude() + radius);
        Collection<QTLeaf> matchingLeafs = zoomlevels[0].getLeafs(newUL, newBR);  //the mapElements in the new area
        Point2D.Double selectedPoint = new Point2D.Double(pos.getLongitude(), pos.getLatitude());  
        Edge currentClosest = null;
        double distance = -1;
        for (QTLeaf qtL : matchingLeafs) {
            for (MapElement mapEle : qtL.getBaseLayer()) {
                if (mapEle instanceof Edge) {
                    Line2D.Double line = new Line2D.Double(((Edge) mapEle).getStart().getPos().getLongitude(),
                                                           ((Edge) mapEle).getStart().getPos().getLatitude(),
                                                           ((Edge) mapEle).getEnd().getPos().getLongitude(),
                                                           ((Edge) mapEle).getEnd().getPos().getLatitude());
                    if (distance == -1 
                            || line.ptLineDist(selectedPoint) < distance) {  //we found an edge which is more closer to the point
                        currentClosest = (Edge)mapEle;
                        distance = line.ptLineDist(selectedPoint);
                    }
                }
            }
        }
        if (currentClosest != null) {
            selection = getSelectionfromPointAndEdge(currentClosest, pos);
        }
        return selection;
    }*/ //TODO if we have Edges in QT, this is the method we need
    
    /*private Selection select(Coordinates pos, float radius) {
        Selection selection = null;
        Coordinates newUL = new Coordinates();  //Bounds for area to search in
        Coordinates newBR = new Coordinates();
        newUL.setLatitude(pos.getLatitude() + radius);
        newUL.setLongitude(pos.getLongitude() - radius);
        newBR.setLatitude(pos.getLatitude() - radius);
        newBR.setLongitude(pos.getLongitude() + radius);
        Point2D.Double selectedPoint = new Point2D.Double(pos.getLongitude(), pos.getLatitude());  
        Edge currentClosest = null;
        double distance = -1;
        for (Edge mapEle : edges) {
            Line2D.Double line = new Line2D.Double(((Edge) mapEle).getStart().getPos().getLongitude(),
                                                  ((Edge) mapEle).getStart().getPos().getLatitude(),
                                                  ((Edge) mapEle).getEnd().getPos().getLongitude(),
                                                  ((Edge) mapEle).getEnd().getPos().getLatitude());
            if (distance == -1 
              || line.ptLineDist(selectedPoint) < distance) {  //we found an edge which is more closer to the point
                currentClosest = (Edge)mapEle;
                distance = line.ptLineDist(selectedPoint);    
            }
        }
        if (currentClosest != null) {
            selection = getSelectionfromPointAndEdge(currentClosest, pos);
        }
        return selection;
    }*/
    
    /*private Selection getSelectionfromPointAndEdge(Edge edge, Coordinates point) {
        return new Selection(edge.getStart().getID(), edge.getEnd().getID(), 0.0f, point); //TODO lot
    }*/
       
    
    @Override
    public ArrayList<MapElement> getBaseLayer(int zoomlevel, Coordinates upLeft,
            Coordinates bottomRight) {
        logger.info("called: getBaseLayer()");
        logger.info(" upLeft Lon: " + upLeft.getLongitude());
        logger.info(" upLeft Lat: " + upLeft.getLatitude());
        logger.info(" QT Bounds UL Lon: " + zoomlevels[0].getUpLeft().getLongitude());
        logger.info(" QT Bounds UL Lat: " + zoomlevels[0].getUpLeft().getLatitude());
        logger.info(" QT Bounds BR Lon: " + zoomlevels[0].getBottomRight().getLongitude());
        logger.info(" QT Bounds BR Lat: " + zoomlevels[0].getBottomRight().getLatitude());
        
        ArrayList<MapElement> mapElements = new ArrayList<MapElement>();
        lastQuery = zoomlevels[0].getLeafs(upLeft, bottomRight);//TODO zoomlevel
        for (QTLeaf qtL : lastQuery) {
            for (MapElement mapEle : qtL.getBaseLayer()) {
                if(mapEle.isInBounds(upLeft, bottomRight) && !mapElements.contains(mapEle)) { //TODO use set
                    mapElements.add(mapEle);
                }
            }
        }
        logger.debug(" base layer size: " + mapElements.size());
        return mapElements;
    }
    
    @Override
    public ArrayList<MapElement> getOverlay(int zoomlevel, Coordinates upLeft,
            Coordinates bottomRight) {
        ArrayList<MapElement> mapElements = new ArrayList<MapElement>();
        for (QTLeaf qtL : zoomlevels[zoomlevel].getLeafs(upLeft, bottomRight)) {
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
        logger.info("called: addToBaseLayer()");
        //logger.debug(element); // ?
        zoomlevels[0].addToBaseLayer(element);
    }

    @Override
    public void addToOverlay(MapElement element) {
        zoomlevels[0].addToOverlay(element);
    }
    
    /**
     * Frankly, I've no idea.
     * @return the same as above
     */
    public String print() {
        return zoomlevels[0].print(0, new ArrayList<Integer>());
    }
    
    @Override
    public int deleteFavorite(Coordinates pos) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public POIDescription getPOIDescription(Coordinates pos) {
        // TODO Auto-generated method stub
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
