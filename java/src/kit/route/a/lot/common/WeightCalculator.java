package kit.route.a.lot.common;

import java.lang.Math;
import kit.route.a.lot.controller.State;
import kit.route.a.lot.heightinfo.Heightmap;

public class WeightCalculator {
    private static WeightCalculator instance;
    
    protected WeightCalculator() { }
    
    public static WeightCalculator getInstance() {
        if (instance == null) {
            instance = new WeightCalculator();
        }
        return instance;
    }

    /**
     * Operation calcWeight
     * 
     * @param fromID
     *            -
     * @param toID
     *            -
     * @return int
     */
    public int calcWeight(int fromID, int toID) {
        Coordinates from = State.getInstance().loadedMapInfo.getNodePosition(fromID);
        Coordinates to = State.getInstance().loadedMapInfo.getNodePosition(toID);
        Heightmap heightmap = State.getInstance().getHeightMap();
        // Pythagoras.
        return (int) Math.sqrt(
                    Math.pow((from.getLatitude() - to.getLatitude()), 2) + 
                    Math.pow((from.getLongitude() - to.getLongitude()), 2) + 
                    Math.pow((State.getHeightMalus() * (heightmap.getHeight(from) - heightmap.getHeight(to))), 2));
    }
    
    public int calcWeight(Selection edge) {
        return calcWeight(edge.getFrom(), edge.getTo());
    }
}
