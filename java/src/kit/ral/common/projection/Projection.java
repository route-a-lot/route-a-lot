package kit.route.a.lot.common.projection;

import kit.route.a.lot.common.Coordinates;


public abstract class Projection {

    public abstract Coordinates getLocalCoordinates(Coordinates geoCoordinates);
    
    public abstract Coordinates getGeoCoordinates(Coordinates localCoordinates);
    
    public static int getZoomFactor(int detail) {
        return (int) Math.pow(2, detail);
    }

}
