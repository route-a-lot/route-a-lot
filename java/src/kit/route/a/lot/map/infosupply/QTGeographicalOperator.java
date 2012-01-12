package kit.route.a.lot.map.infosupply;

import java.awt.geom.Line2D;import java.awt.geom.Point2D;import java.io.InputStream;import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.POIDescription;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.map.Edge;
import kit.route.a.lot.map.MapElement;
import kit.route.a.lot.map.infosupply.GeographicalOperator;
import kit.route.a.lot.map.infosupply.QuadTree;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class QTGeographicalOperator implements GeographicalOperator {

    private static Logger logger = Logger.getLogger(QTGeographicalOperator.class);
    
    /** Associations */
    private QuadTree zoomlevels[];
    
    private Collection<QTLeaf> lastElementsOfSelectedMEs;

    @Override
    public void setBounds(Coordinates upLeft, Coordinates bottomRight) {
        zoomlevels = new QuadTree[9];
        for (int i = 0; i < zoomlevels.length; i++) {
            zoomlevels[i] = new QTNode(upLeft, bottomRight);
        }

    }

    @Override
    public void buildZoomlevels() {
        // TODO Auto-generated method stub

    }

    @Override
    public Selection select(Coordinates pos) {
        float radius = 0.01f;
        Selection sel = null;
        while(sel == null && radius < 1000) {  //360 for avoiding errors on maps without edges
            sel = select(pos, radius);
            radius *= 10;  // if we found no edge we have to search in a bigger area
        }
        return  sel;
    }
    
    private Selection select(Coordinates pos, float radius) {
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
    }
    
    private Selection getSelectionfromPointAndEdge(Edge edge, Coordinates point) {
        return new Selection(edge.getStart().getID(), edge.getEnd().getID(), 0.0f, point); //TODO lot
    }
        
    @Override
    public ArrayList<MapElement> getBaseLayer(int zoomlevel, Coordinates upLeft,
            Coordinates bottomRight) {
        logger.info("getBasseLayer called");
        logger.info("upLeft long: " + upLeft.getLongitude());
        logger.info("upLeft lal: " + upLeft.getLatitude());
        logger.info("QT Bounds ULLong: " + zoomlevels[0].getUpLeft().getLongitude());
        logger.info("QT Bounds ULLAL: " + zoomlevels[0].getUpLeft().getLatitude());
        logger.info("QT Bounds BRLONG: " + zoomlevels[0].getBottomRight().getLongitude());
        logger.info("QT Bounds BRLAL: " + zoomlevels[0].getBottomRight().getLatitude());
        
        ArrayList<MapElement> mapElements = new ArrayList<MapElement>();
        lastElementsOfSelectedMEs = zoomlevels[0].getLeafs(upLeft, bottomRight);//TODO zoomlevel
        for (QTLeaf qtL : lastElementsOfSelectedMEs) {
            for (MapElement mapEle : qtL.getBaseLayer()) {
                if(mapEle.isInBounds(upLeft, bottomRight) && !mapElements.contains(mapEle)) { //TODO use set
                    mapElements.add(mapEle);
                }
            }
        }
        logger.debug("baseLAyerSize: " + mapElements.size());
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
    public void addToBaseLayer(MapElement element) {
        logger.info("addToBaseLayer called");
        logger.debug(element);
        zoomlevels[0].addToBaseLayer(element);
    }

    @Override
    public void addToOverlay(MapElement element) {
        zoomlevels[0].addToOverlay(element);
    }

    @Override
    public void loadFromStream(InputStream stream) {
        // TODO Auto-generated method stub

    }

    @Override
    public void saveToStream(OutputStream stream) {
        // TODO Auto-generated method stub

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

    /**
     * Returns the correspondending overlay to the last baseLayer  
     */
    @Override
    public Collection<MapElement> getOverlayToLastBaseLayer(Coordinates upLeft,
            Coordinates bottomRight) {
        ArrayList<MapElement> mapElements = new ArrayList<MapElement>();
        for (QTLeaf qtL : lastElementsOfSelectedMEs) {
            for (MapElement mapEle : qtL.getBaseLayer()) {
                if(mapEle.isInBounds(upLeft, bottomRight) && !mapElements.contains(mapEle)) { //TODO use set
                    mapElements.add(mapEle); 
                }
            }
        }
        return mapElements;
    }
}
