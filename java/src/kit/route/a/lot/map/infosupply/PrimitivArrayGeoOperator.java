package kit.route.a.lot.map.infosupply;

import java.io.InputStream;import java.io.OutputStream;import java.util.Collection;import java.util.ArrayList;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import org.apache.log4j.Logger;


import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.POIDescription;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.controller.State;
import kit.route.a.lot.map.*;


public class PrimitivArrayGeoOperator implements GeographicalOperator {
    private ArrayList<MapElement> overlay;
    private ArrayList<MapElement> baseLayer;
    private Logger logger = Logger.getLogger(PrimitivArrayGeoOperator.class);
     
    public PrimitivArrayGeoOperator() {
        overlay = new ArrayList<MapElement>();
        baseLayer = new ArrayList<MapElement>();
    }

    @Override
    public void setBounds(Coordinates upLeft, Coordinates bottomRight) {
        // TODO Auto-generated method stub

    }

    @Override
    public void buildZoomlevels() {
        // TODO Auto-generated method stub

    }

    @Override
    public Selection select(Coordinates pos) {
        Point2D.Double selectedPoint = new Point2D.Double(pos.getLongitude(), pos.getLatitude());
        Edge currentClosest = null;
        double distance = -1;
        for(MapElement mapEle: baseLayer) {
            if (mapEle instanceof Edge) {
                Line2D.Double line = new Line2D.Double(((Edge) mapEle).getStart().getPos().getLongitude(),
                                                       ((Edge) mapEle).getStart().getPos().getLatitude(),
                                                       ((Edge) mapEle).getEnd().getPos().getLongitude(),
                                                       ((Edge) mapEle).getEnd().getPos().getLatitude());
                if (distance == -1 
                        || line.ptLineDist(selectedPoint) < distance) {
                    currentClosest = (Edge)mapEle;
                    distance = line.ptLineDist(selectedPoint);
                }
            }
        }
        return new Selection(currentClosest.getStart().getID(), currentClosest.getEnd().getID(), 0.0f, pos);    //TODO lot
    }

    @Override
    public ArrayList<MapElement> getBaseLayer(int zoomlevel, Coordinates upLeft,
            Coordinates bottomRight) {
        logger.info("getBasseLayer called");
        logger.info("upLeft long: " + upLeft.getLongitude());
        logger.info("upLeft lal: " + upLeft.getLatitude());
        logger.info("middle long: " + State.getInstance().getAreaCoord().getLongitude());
        logger.info("middle lal: " + State.getInstance().getAreaCoord().getLatitude());
        ArrayList<MapElement> baseLay = new ArrayList<MapElement>();
        for(MapElement mapEle: baseLayer){
            if(mapEle.isInBounds(upLeft, bottomRight)){
                baseLay.add(mapEle);
            }
        }
        logger.debug("BaseLayerSize: " + baseLay.size());
        return baseLay;
    }

    @Override
    public ArrayList<MapElement> getOverlay(int zoomlevel, Coordinates upLeft,
            Coordinates bottomRight) {
        ArrayList<MapElement> ovLay = new ArrayList<MapElement>();
        for(MapElement mapEle: overlay){
            if(mapEle.isInBounds(upLeft, bottomRight)){
                ovLay.add(mapEle);
            }
        }
        return ovLay;
    }

    @Override
    public void addToBaseLayer(MapElement element) {
        baseLayer.add(element);

    }

    @Override
    public void addToOverlay(MapElement element) {
        overlay.add(element);

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

    @Override
    public Collection<MapElement> getOverlayToLastBaseLayer(Coordinates upLeft,
            Coordinates bottomRight) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addEdge(Edge edge) {
        // TODO Auto-generated method stub
        
    }

}
