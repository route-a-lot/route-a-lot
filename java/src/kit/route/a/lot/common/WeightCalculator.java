package kit.route.a.lot.common;

import java.lang.Math;
import kit.route.a.lot.controller.State;
import kit.route.a.lot.heightinfo.Heightmap;
import kit.route.a.lot.map.rendering.MercatorProjection;
import kit.route.a.lot.map.rendering.Projection;

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
    public int calcWeightWithHeight(int fromID, int toID) {
        Coordinates from = State.getInstance().getLoadedMapInfo().getNodePosition(fromID);
        Coordinates to = State.getInstance().getLoadedMapInfo().getNodePosition(toID);
        Heightmap heightmap = State.getInstance().getHeightMap();
        // Pythagoras.
        return (int) Math.sqrt(Math.pow((from.getLatitude() - to.getLatitude()), 2)
                + Math.pow((from.getLongitude() - to.getLongitude()), 2)
                + Math.pow((State.getInstance().getHeightMalus() * (heightmap.getHeight(from) - heightmap
                        .getHeight(to))), 2));
    }


    public int calcWeight(int fromID, int toID) {
        Coordinates from = State.getInstance().getLoadedMapInfo().getNodePosition(fromID);
        Coordinates to = State.getInstance().getLoadedMapInfo().getNodePosition(toID);
        
        Coordinates geoFrom = projection.localCoordinatesToGeoCoordinates(from);
        Coordinates geoTo = projection.localCoordinatesToGeoCoordinates(to);

        // Kugelkoordinaten berechnet distance in km
        return (int) Math.floor((6378.388 * Math.acos(Math.sin(geoFrom.getLatitude())
                * Math.sin(geoTo.getLatitude()) + Math.cos(geoFrom.getLatitude()) * Math.cos(geoTo.getLatitude())
                * Math.cos(geoTo.getLongitude() - geoFrom.getLongitude()))));
    }

    public int calcWeight(Selection edge) {
        return calcWeight(edge.getFrom(), edge.getTo());
    }

    public int calcWeightWithUTM(int fromID, int toID) {
        Coordinates from = State.getInstance().getLoadedMapInfo().getNodePosition(fromID);
        Coordinates to = State.getInstance().getLoadedMapInfo().getNodePosition(toID);

        double lat1 = (double) from.getLatitude();
        double lon1 = (double) from.getLongitude();
        double lat2 = (double) to.getLatitude();
        double lon2 = (double) to.getLongitude();


        int entf = 0;
        UTMConverter converter = UTMConverter.getInstance();
        int[] utmDatenStart = converter.utmConverter(lat1, lon1);
        int[] utmDatenEnd = converter.utmConverter(lat2, lon2);
        entf =
                Math.round((float) Math.sqrt(Math.pow(utmDatenStart[0] - utmDatenEnd[0], 2)
                        + Math.pow(utmDatenStart[1] - utmDatenEnd[1], 2)));
        System.out.println(entf);
        return entf / 1000;
    }// end calcWeightWithUTM


}
