package kit.route.a.lot.map.infosupply;

import java.util.List;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.common.description.RouteDescription;
import kit.route.a.lot.common.projection.Projection;
import kit.route.a.lot.common.projection.ProjectionFactory;
import kit.route.a.lot.controller.State;

public class ComplexInfoSupplier {

    public static int getDuration(List<Integer> route, int speed,  List<Selection> navNodes) {
        return (int) (getLength(route, navNodes) / (speed / 3.6));
    }
    
    public static int getLength(List<Integer> route, List<Selection> navNodes) {
        if (navNodes.size() < 2 || route.size() == 0) {
            return 0;
        }
        int length = 0;
        int navNode = 1;
        MapInfo mapInfo = State.getInstance().getMapInfo();
        if (route.get(0) == navNodes.get(0).getTo()) {
            length += (1 - navNodes.get(0).getRatio()) *
                    getDistanceInMeter(mapInfo.getNodePosition(navNodes.get(0).getFrom()),
                            mapInfo.getNodePosition(navNodes.get(0).getTo()));
        } else {
            length += (navNodes.get(0).getRatio()) *
                    getDistanceInMeter(mapInfo.getNodePosition(navNodes.get(0).getTo()),
                            mapInfo.getNodePosition(navNodes.get(0).getFrom()));
        }
        
        if (route.get(route.size() - 1) == navNodes.get(navNodes.size() - 1).getTo()) {
            length += (1 - navNodes.get(navNodes.size() - 1).getRatio()) *
                    getDistanceInMeter(mapInfo.getNodePosition(navNodes.get(navNodes.size() - 1).getTo()),
                            mapInfo.getNodePosition(navNodes.get(navNodes.size() - 1).getFrom()));
        } else {
            length += ((navNodes.get(0).getRatio())) *
                    getDistanceInMeter(mapInfo.getNodePosition(navNodes.get(navNodes.size() - 1).getFrom()),
                            mapInfo.getNodePosition(navNodes.get(navNodes.size() - 1).getTo()));
        }
        Coordinates startSelection =  Coordinates.interpolate(mapInfo.getNodePosition(navNodes.get(0).getFrom()),
                mapInfo.getNodePosition(navNodes.get(0).getTo()), navNodes.get(0).getRatio());
        Coordinates endSelection =  Coordinates.interpolate(mapInfo.getNodePosition(navNodes.get(navNodes.size() - 1).getFrom()),
                mapInfo.getNodePosition(navNodes.get(navNodes.size() - 1).getTo()), navNodes.get(navNodes.size() - 1).getRatio());
        length += getDistanceInMeter(startSelection, navNodes.get(0).getPosition());
        length += getDistanceInMeter(endSelection, navNodes.get(navNodes.size() - 1).getPosition());
        
        for(int i = 1; i < route.size() - 1; i++){
            
            if(route.get(i) == -1) {
                if (route.get(i - 1) == navNodes.get(navNode).getFrom()) {
                    length += (navNodes.get(navNode).getRatio()) *
                            getDistanceInMeter(mapInfo.getNodePosition(navNodes.get(navNode).getFrom()),
                                    mapInfo.getNodePosition(navNodes.get(navNode).getTo()));
                } else {
                    length += ((1 - navNodes.get(navNode).getRatio())) *
                            getDistanceInMeter(mapInfo.getNodePosition(navNodes.get(navNode).getTo()),
                                    mapInfo.getNodePosition(navNodes.get(navNode).getFrom()));
                }
                if (route.get(i + 1) == navNodes.get(navNode).getFrom()) {
                    length += (navNodes.get(navNode).getRatio()) *
                            getDistanceInMeter(mapInfo.getNodePosition(navNodes.get(navNode).getTo()),
                                    mapInfo.getNodePosition(navNodes.get(navNode).getFrom()));
                } else {
                    length += (1 - navNodes.get(navNode).getRatio()) *
                            getDistanceInMeter(mapInfo.getNodePosition(navNodes.get(navNode).getFrom()),
                                    mapInfo.getNodePosition(navNodes.get(navNode).getTo()));
                }
                Coordinates ratioCoord =  Coordinates.interpolate(mapInfo.getNodePosition(navNodes.get(navNode).getFrom()),
                        mapInfo.getNodePosition(navNodes.get(navNode).getTo()), navNodes.get(navNode).getRatio());
                length += 2 * getDistanceInMeter(ratioCoord, navNodes.get(navNode).getPosition());
                i++;
                navNode++;
                
            } else {
                length += getDistanceInMeter(mapInfo.getNodePosition(route.get(i - 1)), mapInfo.getNodePosition(route.get(i)));
            }
            
        }
        return length;
    }

    public static RouteDescription getRouteDescription(List<Integer> list) {
        //MapInfo mapInfo = State.getInstance().getLoadedMapInfo();
        RouteDescription rd = new RouteDescription();
        //Street currentStreet = null;
        return rd;
    }
    

    /**
     * returns the distance between the given node and coordinate in meter
     * 
     * params are projected coordinates on the current map
     */
    private static double getDistanceInMeter(Coordinates pos1, Coordinates pos2) {
        Projection projection = ProjectionFactory.getCurrentProjection();
        Coordinates geoPos1 = projection.getGeoCoordinates(pos1);
        Coordinates geoPos2 = projection.getGeoCoordinates(pos2);
        double pos1LongRad = Math.toRadians(Math.abs(geoPos1.getLongitude())); // coordinates in deg
        double pos1LalRad = Math.toRadians(Math.abs(geoPos1.getLatitude()));
        double pos2LongRad = Math.toRadians(Math.abs(geoPos2.getLongitude()));
        double pos2LalRad = Math.toRadians(Math.abs(geoPos2.getLatitude()));

        double distanceRad =
                Math.acos(Math.sin(pos1LalRad) * Math.sin(pos2LalRad) + Math.cos(pos1LalRad)
                        * Math.cos(pos2LalRad) * Math.cos(pos1LongRad - pos2LongRad)); // distance in deg

        return distanceRad * 6371001; // 6371001 is the mean earth radius in meter
    }
    
}
