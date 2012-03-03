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
    
    public int calcWeightWithHeightAndHighwayMalus(int fromID, int toID, int wayType) {
        int weightWithHeight = calcWeightWithHeight(fromID, toID);
        return weightWithHeight * (getMalus(wayType) * State.getInstance().getHighwayMalus() + 1);
    }
    
    private static int getMalus(int wayType) {
        int malus = 0;
        switch (wayType) {
            case OSMType.HIGHWAY_MOTORWAY:
            case OSMType.HIGHWAY_MOTORWAY_JUNCTION:
            case OSMType.HIGHWAY_MOTORWAY_LINK:
                malus = 4;
                break;
            case OSMType.HIGHWAY_PRIMARY:
            case OSMType.HIGHWAY_PRIMARY_LINK:
                malus = 3;
                break;
            case OSMType.HIGHWAY_SECONDARY:
            case OSMType.HIGHWAY_SECONDARY_LINK:
                malus = 2;
                break;
            case OSMType.HIGHWAY_TERTIARY:
            case OSMType.HIGHWAY_TERTIARY_LINK:
                malus = 1;
                break;
            case OSMType.HIGHWAY_RESIDENTIAL:
            case OSMType.HIGHWAY_LIVING_STREET:
            case OSMType.HIGHWAY_UNCLASSIFIED:
                malus = 0;
                break;
            case OSMType.HIGHWAY_CYCLEWAY:
                malus = 0;
                break;
        }
        return malus;
    }

    public int calcWeightWithHeight(int fromID, int toID) {
        Coordinates from = projection.getGeoCoordinates(State.getInstance().getMapInfo().getNodePosition(fromID));
        Coordinates to = projection.getGeoCoordinates(State.getInstance().getMapInfo().getNodePosition(toID));
        IHeightmap heightmap = State.getInstance().getLoadedHeightmap();

        int flatWeight = calcWeight(fromID, toID);
        float fromHeight = heightmap.getHeight(from) / 100;
        float toHeight = heightmap.getHeight(to) / 100;

        float heightDifference = Math.abs(fromHeight - toHeight) * State.getInstance().getHeightMalus();

        int weight = (int) Math.sqrt(Math.pow(flatWeight, 2) + Math.pow(heightDifference, 2));
        return weight;
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
