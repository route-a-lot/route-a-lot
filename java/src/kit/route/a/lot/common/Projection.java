package kit.route.a.lot.common;

import kit.route.a.lot.controller.State;


public abstract class Projection {

    public abstract Coordinates geoCoordinatesToLocalCoordinates(Coordinates geoCoordinates);
    
    public abstract Coordinates localCoordinatesToGeoCoordinates(Coordinates localCoordinates);
    
    public static int getZoomFactor(int detail) {
        return (int) Math.pow(2, detail);
    }

}
