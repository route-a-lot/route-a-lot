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
        Coordinates from = State.getInstance().getLoadedMapInfo().getNodePosition(fromID);
        Coordinates to = State.getInstance().getLoadedMapInfo().getNodePosition(toID);
        IHeightmap heightmap = State.getInstance().getLoadedHeightmap();
        /*
         * verbesserter Pythagoras, für kleine Entfernungen ausreichend, Abstand Breitenkreise 111.3km,
         * Abstand Längenkreise 111.3*cos(lat)km,wobei lat genau zwischen lat1 und lat2 liegt
         */
        float lat1 = from.getLatitude();
        float lon1 = from.getLongitude();
        float lat2 = to.getLatitude();
        float lon2 = to.getLongitude();
        double lat = (lat1 + lat2) * 0.5 * 0.017453292;
        double dx = 111.3 * Math.cos(lat) * (lon1 - lon2);
        double dy = 111.3 * (lat1 - lat2);
        double distance =
                (int) Math
                        .sqrt(Math.pow(dx, 2)
                                + Math.pow(dy, 2)
                                + Math.pow(
                                        (State.getInstance().getHeightMalus() * (heightmap.getHeight(from) * 0.001 - heightmap
                                                .getHeight(to) * 0.001)), 2)/* pow */);
        return (int) (distance * 1000);// Distanz in metern
    }// end calcHeightWeight


    public int calcWeight(int fromID, int toID) {
        Coordinates from = State.getInstance().getLoadedMapInfo().getNodePosition(fromID);
        Coordinates to = State.getInstance().getLoadedMapInfo().getNodePosition(toID);

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


    public int calcWeight(Selection edge) {
        return calcWeight(edge.getFrom(), edge.getTo());
    }


}
