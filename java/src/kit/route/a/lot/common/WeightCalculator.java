package kit.route.a.lot.common;

import java.lang.Math;

import kit.route.a.lot.controller.State;
import kit.route.a.lot.heightinfo.Heightmap;
import kit.route.a.lot.map.infosupply.MapInfo;


public class WeightCalculator {

    /**
     * Operation calcWeight
     * 
     * @param fromID
     *            -
     * @param toID
     *            -
     * @return int
     */
    public static int calcWeight(int fromID, int toID) {
        Coordinates from = MapInfo.getNodePosition(fromID);
        Coordinates to = MapInfo.getNodePosition(toID);
        Heightmap heightmap = State.getHeightMap();
        return (int) Math.sqrt(
                    Math.pow((from.getLatitude() - to.getLatitude()), 2) + 
                    Math.pow((from.getLongitude() - to.getLongitude()), 2) + 
                    Math.pow((State.getHeightMalus() * (heightmap.getHeight(from) - heightmap.getHeight(to))), 2));
    }
}
