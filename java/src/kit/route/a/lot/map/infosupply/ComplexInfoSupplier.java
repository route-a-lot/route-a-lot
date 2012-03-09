package kit.route.a.lot.map.infosupply;

import java.util.List;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.RouteDescription;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.controller.State;
import kit.route.a.lot.map.Street;

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
                    Street.getDistanceInMeter(mapInfo.getNodePosition(navNodes.get(0).getFrom()),
                            mapInfo.getNodePosition(navNodes.get(0).getTo()));
        } else {
            length += (navNodes.get(0).getRatio()) *
                    Street.getDistanceInMeter(mapInfo.getNodePosition(navNodes.get(0).getTo()),
                            mapInfo.getNodePosition(navNodes.get(0).getFrom()));
        }
        
        if (route.get(route.size() - 1) == navNodes.get(navNodes.size() - 1).getTo()) {
            length += (1 - navNodes.get(navNodes.size() - 1).getRatio()) *
                    Street.getDistanceInMeter(mapInfo.getNodePosition(navNodes.get(navNodes.size() - 1).getTo()),
                            mapInfo.getNodePosition(navNodes.get(navNodes.size() - 1).getFrom()));
        } else {
            length += ((navNodes.get(0).getRatio())) *
                    Street.getDistanceInMeter(mapInfo.getNodePosition(navNodes.get(navNodes.size() - 1).getFrom()),
                            mapInfo.getNodePosition(navNodes.get(navNodes.size() - 1).getTo()));
        }
        Coordinates startSelection =  Coordinates.interpolate(mapInfo.getNodePosition(navNodes.get(0).getFrom()),
                mapInfo.getNodePosition(navNodes.get(0).getTo()), navNodes.get(0).getRatio());
        Coordinates endSelection =  Coordinates.interpolate(mapInfo.getNodePosition(navNodes.get(navNodes.size() - 1).getFrom()),
                mapInfo.getNodePosition(navNodes.get(navNodes.size() - 1).getTo()), navNodes.get(navNodes.size() - 1).getRatio());
        length += Street.getDistanceInMeter(startSelection, navNodes.get(0).getPosition());
        length += Street.getDistanceInMeter(endSelection, navNodes.get(navNodes.size() - 1).getPosition());
        
        for(int i = 1; i < route.size() - 1; i++){
            
            if(route.get(i) == -1) {
                if (route.get(i - 1) == navNodes.get(navNode).getFrom()) {
                    length += (navNodes.get(navNode).getRatio()) *
                            Street.getDistanceInMeter(mapInfo.getNodePosition(navNodes.get(navNode).getFrom()),
                                    mapInfo.getNodePosition(navNodes.get(navNode).getTo()));
                } else {
                    length += ((1 - navNodes.get(navNode).getRatio())) *
                            Street.getDistanceInMeter(mapInfo.getNodePosition(navNodes.get(navNode).getTo()),
                                    mapInfo.getNodePosition(navNodes.get(navNode).getFrom()));
                }
                if (route.get(i + 1) == navNodes.get(navNode).getFrom()) {
                    length += (navNodes.get(navNode).getRatio()) *
                            Street.getDistanceInMeter(mapInfo.getNodePosition(navNodes.get(navNode).getTo()),
                                    mapInfo.getNodePosition(navNodes.get(navNode).getFrom()));
                } else {
                    length += (1 - navNodes.get(navNode).getRatio()) *
                            Street.getDistanceInMeter(mapInfo.getNodePosition(navNodes.get(navNode).getFrom()),
                                    mapInfo.getNodePosition(navNodes.get(navNode).getTo()));
                }
                Coordinates ratioCoord =  Coordinates.interpolate(mapInfo.getNodePosition(navNodes.get(navNode).getFrom()),
                        mapInfo.getNodePosition(navNodes.get(navNode).getTo()), navNodes.get(navNode).getRatio());
                length += 2 * Street.getDistanceInMeter(ratioCoord, navNodes.get(navNode).getPosition());
                i++;
                navNode++;
                
            } else {
                length += Street.getDistanceInMeter(mapInfo.getNodePosition(route.get(i - 1)), mapInfo.getNodePosition(route.get(i)));
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
    
    
}
