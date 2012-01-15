package kit.route.a.lot.map.infosupply;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.ArrayList;

import org.apache.log4j.Logger;


import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.POIDescription;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.controller.State;
import kit.route.a.lot.map.*;


public class PrimitiveArrayGeoOperator implements GeographicalOperator {
    private ArrayList<MapElement> overlay;
    private ArrayList<MapElement> baseLayer;
    private Logger logger = Logger.getLogger(PrimitiveArrayGeoOperator.class);
     
    public PrimitiveArrayGeoOperator() {
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
        /*Point2D.Double selectedPoint = new Point2D.Double(pos.getLongitude(), pos.getLatitude());
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
        */
        return null;
    }

    @Override
    public ArrayList<MapElement> getBaseLayer(int zoomlevel, Coordinates upLeft,
            Coordinates bottomRight) {
        logger.info("called: getBaseLayer()");
        logger.info(" upLeft Lon: " + upLeft.getLongitude());
        logger.info(" upLeft Lat: " + upLeft.getLatitude());
        logger.info(" middle Lon: " + State.getInstance().getAreaCoord().getLongitude());
        logger.info(" middle Lat: " + State.getInstance().getAreaCoord().getLatitude());
        ArrayList<MapElement> baseLay = new ArrayList<MapElement>();
        for(MapElement mapEle: baseLayer){
            if(mapEle.isInBounds(upLeft, bottomRight)){
                baseLay.add(mapEle);
            }
        }
        logger.debug(" Base Layer size: " + baseLay.size());
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
    public void loadFromStream(DataInputStream stream) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void saveToStream(DataOutputStream stream) throws IOException {
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

}
