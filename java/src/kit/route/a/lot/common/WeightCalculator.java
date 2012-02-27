package kit.route.a.lot.common;

import java.lang.Math;
import kit.route.a.lot.controller.State;
import kit.route.a.lot.heightinfo.IHeightmap;

public class WeightCalculator {

    private static WeightCalculator instance;
    private Projection projection;

    protected WeightCalculator() {
    }

    public static WeightCalculator getInstance() {
        if (instance == null) {
            instance = new WeightCalculator();
        }
        return instance;
    }

    public void setProjection(Projection projection) {
        this.projection = projection;
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
    public int calcHeightWeight(int fromID, int toID) {
        Coordinates from = State.getInstance().getMapInfo().getNodePosition(fromID);
        Coordinates to = State.getInstance().getMapInfo().getNodePosition(toID);
        IHeightmap heightmap = State.getInstance().getLoadedHeightmap();

        int flatWeight = calcWeight(fromID, toID);
        float fromHeight = heightmap.getHeight(from) / 100;
        float toHeight = heightmap.getHeight(to) / 100;
        
        float heightDifference = 0;
        
        
        
        float weight = 0;
        return (int) weight;
    }// end calcHeightWeight


    public int calcWeight(int fromID, int toID) {
        Coordinates from = State.getInstance().getMapInfo().getNodePosition(fromID);
        Coordinates to = State.getInstance().getMapInfo().getNodePosition(toID);

        Coordinates geoFrom = projection.getGeoCoordinates(from);
        Coordinates geoTo = projection.getGeoCoordinates(to);

        double geoFromLonRad = Math.abs(geoFrom.getLongitude() / 180 * Math.PI);
        double geoFromLalRad = Math.abs(geoFrom.getLatitude() / 180 * Math.PI);
        double geoToLonRad = Math.abs(geoTo.getLongitude() / 180 * Math.PI);
        double geoToLalRad = Math.abs(geoTo.getLatitude() / 180 * Math.PI);

        // Kugelkoordinaten berechnet distance in km
        double distanceRad =
                Math.acos(Math.sin(geoFromLalRad) * Math.sin(geoToLalRad) + Math.cos(geoFromLalRad)
                        * Math.cos(geoToLalRad) * Math.cos(geoFromLonRad - geoToLonRad));
        return (int) (100 * 6371000.785 * distanceRad); // 6371000 is earthRadius in meter, so result will be
                                                        // given in cm
    }


}
