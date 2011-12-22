package kit.route.a.lot.map.infosupply;

import java.io.InputStream;import java.io.OutputStream;
import java.util.Set;
import java.util.ArrayList;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Line2D.*;


import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.map.*;


public class PrimitivArrayGeoOperator implements GeographicalOperator {
    private ArrayList<MapElement> overlay;
    private ArrayList<MapElement> baseLayer;
     
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
        Point2D.Double selectedPoint = new Point2D.Double(pos.getLon(), pos.getLat());
        Edge currentClosest;
        double distance = -1;
        for(MapElement mapEle: baseLayer) {
            if (mapEle instanceof Edge) {
                Line2D.Double line = new Line2D.Double(((Edge) mapEle).getStart().getPos().getLon(),
                                                       ((Edge) mapEle).getStart().getPos().getLat(),
                                                       ((Edge) mapEle).getEnd().getPos().getLon(),
                                                       ((Edge) mapEle).getEnd().getPos().getLat());
                if (distance == -1 
                        || line.ptLineDist(selectedPoint) < distance) {
                    currentClosest = (Edge)mapEle;
                    distance = line.ptLineDist(selectedPoint);
                }
            }
        }
        return null;    //TODO SELECTION!
    }

    @Override
    public ArrayList<MapElement> getBaseLayer(int zoomlevel, Coordinates upLeft,
            Coordinates bottomRight) {
        ArrayList<MapElement> baseLay = new ArrayList<MapElement>();
        for(MapElement mapEle: baseLayer){
            if(mapEle.isInBounds(upLeft, bottomRight)){
                baseLay.add(mapEle);
            }
        }
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

}
