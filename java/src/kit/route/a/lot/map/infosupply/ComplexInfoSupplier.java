package kit.route.a.lot.map.infosupply;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.RouteDescription;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.controller.State;
import kit.route.a.lot.map.MapElement;
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
    
    
    
    //returns the street which the point in this route relies to
    @SuppressWarnings("unused")
    private static Street giveMeCurrentStreet(int index, ArrayList<Integer> route) {
        boolean found = false;
        int i = index;
        ArrayList<Street> pointStreets= getStreet(State.getInstance().getMapInfo().getNodePosition(route.get(index)));
        if (i  == route.size() - 1 || pointStreets.size() == 1) {
            found = true;
        }
        i++;
        while(index < route.size() && found == false) {
            ArrayList<Street> nextStreets = getStreet(State.getInstance().getMapInfo().getNodePosition(route.get(i)));
            if (pointStreets.size() == 1) {
                return pointStreets.get(0);
            } else {
                int containNumber = containNumber(nextStreets, pointStreets);
                if (containNumber == 0) {
                    return pointStreets.get(0); 
                } else if (containNumber == 1) {
                    reduceAFromB(pointStreets, nextStreets);
                    return pointStreets.get(0);
                } else {
                    reduceAFromB(pointStreets, nextStreets);
                }
            }
            i++;
        }
        return pointStreets.get(0);
    }
    
    //returns the streets, which the point of this position is part of
    private static ArrayList<Street> getStreet(Coordinates position) {
        Collection<MapElement> elements = State.getInstance().getMapInfo().getBaseLayerForPositionAndRadius(position, 1, true);
        ArrayList<Street> streets = new ArrayList<Street>();
        for (MapElement element : elements) {
            if (element instanceof Street) {
                streets.add((Street)element);
            }
        }
        return streets;
        
    }
    
    //returns the number of elements the two lists have in common
    private static int containNumber(ArrayList<Street> a, ArrayList<Street> b) {
        int number = 0;
        for(Street streetA : a) {
            if(b.contains(streetA)) {
                number++;
            }
        }
        return number;
    }
    
    //deletes the streets from a, which aren't in b
    private static void reduceAFromB(ArrayList<Street> a, ArrayList<Street> b) {
        for(Street street : a) {
            if(!b.contains(street)) {
                a.remove(street);
            }
        }
    }
    
    
}
