package kit.route.a.lot.map.infosupply;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.RouteDescription;
import kit.route.a.lot.controller.State;
import kit.route.a.lot.map.MapElement;
import kit.route.a.lot.map.Street;

public class ComplexInfoSupplier {

    /**
     * Operation getDuration
     * 
     * @param route
     *            -
     * @param speed
     *            -
     * @return int
     */
    public static int getDuration(ArrayList<Integer> route, int speed) {
        double speedInMeterPerSecond = speed * 3.6; 
        int time = 0;
        MapInfo mapInfo = State.getInstance().getLoadedMapInfo();
        for(int i = 0; i < route.size() - 1; i++){
            time += Street.getDistanceProj(mapInfo.getNodePosition(route.get(i)), mapInfo.getNodePosition(route.get(i + 1))) / speedInMeterPerSecond;
        }
        return time;
    }

    
    
    /**
     * Operation getRouteDescription
     * 
     * @param list
     *            -
     * @return RouteDescription
     */
    public static RouteDescription getRouteDescription(List<Integer> list) {
        MapInfo mapInfo = State.getInstance().getLoadedMapInfo();
        RouteDescription rd = new RouteDescription();
        int i = 0;
        Street currentStreet = null;
        return rd;
    }
    
    //returns the street which the point in this route relies to
    private static Street giveMeCurrentStreet(int index, ArrayList<Integer> route) {
        boolean found = false;
        int i = index;
        ArrayList<Street> pointStreets= getStreet(State.getInstance().getLoadedMapInfo().getNodePosition(route.get(index)));
        if (i  == route.size() - 1 || pointStreets.size() == 1) {
            found = true;
        }
        i++;
        while(index < route.size() && found == false) {
            ArrayList<Street> nextStreets = getStreet(State.getInstance().getLoadedMapInfo().getNodePosition(route.get(i)));
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
        Collection<MapElement> elements = State.getInstance().getLoadedMapInfo().getBaseLAyerForPositionAndRadius(position, 0);
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
