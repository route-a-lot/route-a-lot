package kit.ral.common;

import java.lang.Math;

import kit.ral.common.description.OSMType;
import kit.ral.common.projection.Projection;
import kit.ral.controller.State;
import kit.ral.heightinfo.IHeightmap;

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
        return (int) (weightWithHeight * (getMalusFactor(wayType, State.getInstance().getHighwayMalus())));
    }
    
    private static double getMalusFactor(int wayType, int malus) {
        if (malus == 0) {
            return 1;
        }
        float malusFactor = 0.03f;
        switch (wayType) {
            case OSMType.HIGHWAY_MOTORWAY:
            case OSMType.HIGHWAY_MOTORWAY_JUNCTION:
            case OSMType.HIGHWAY_MOTORWAY_LINK:
                malusFactor = 2.f;
                break;
            case OSMType.HIGHWAY_PRIMARY:
            case OSMType.HIGHWAY_PRIMARY_LINK:
                malusFactor = 1f;
                break;
            case OSMType.HIGHWAY_SECONDARY:
            case OSMType.HIGHWAY_SECONDARY_LINK:
                malusFactor = 0.5f;
                break;
            case OSMType.HIGHWAY_TERTIARY:
            case OSMType.HIGHWAY_TERTIARY_LINK:
                malusFactor = 0.25f;
                break;
            case OSMType.HIGHWAY_RESIDENTIAL:
            case OSMType.HIGHWAY_LIVING_STREET:
            case OSMType.HIGHWAY_UNCLASSIFIED:
                malusFactor = 0.02f;
                break;
            case OSMType.HIGHWAY_CYCLEWAY:
                malusFactor = 0;
                break;
        }
        return 1 + malusFactor / 150 * Math.pow(7, malus);
    }
    
    //private static void print(String string) {
    //    System.out.println(string);
    //}

    public int calcWeightWithHeight(int fromID, int toID) {
        Coordinates from = projection.getGeoCoordinates(State.getInstance().getMapInfo().getNodePosition(fromID));
        Coordinates to = projection.getGeoCoordinates(State.getInstance().getMapInfo().getNodePosition(toID));
        IHeightmap heightmap = State.getInstance().getLoadedHeightmap();

        int flatWeight = calcWeight(fromID, toID);
        float fromHeight = heightmap.getHeight(from) * 100;
        float toHeight = heightmap.getHeight(to) * 100;

        double heightDifference = Math.pow(Math.abs(fromHeight - toHeight), 2) * getHeightMalusFactor(State.getInstance().getHeightMalus());
        if (toHeight < fromHeight) {
            heightDifference = 0;
        }

        int weight = (int) Math.sqrt(Math.pow(flatWeight, 2) + Math.pow(heightDifference, 2));
//        print("Way from " + fromID + " to " + toID);
//        print("flatWeight: " + flatWeight);
//        print("weight with height: " + weight);
        return weight;
    }// end calcHeightWeight
    
    private static double getHeightMalusFactor(int heightMalus) {
        if (heightMalus == 0) {
            return 0;
        }
        return Math.pow(2, State.getInstance().getHeightMalus());
    }


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
