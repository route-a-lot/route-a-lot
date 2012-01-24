package kit.route.a.lot.map.rendering;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.controller.State;


public abstract class Projection {

    public abstract Coordinates geoCoordinatesToLocalCoordinates(Coordinates geoCoordinates);
    
    public abstract Coordinates localCoordinatesToGeoCoordinates(Coordinates localCoordinates);
    
    public static int getZoomFactor(int detail) {
        return (int) Math.pow(2, detail);
    }
    
    public static Projection getNewProjection(Coordinates topLeft) {
        float scale = 5E-6f;
        return new MercatorProjection(topLeft, scale);
    }
    
    public static Projection getProjectionForCurrentMap() {
        Coordinates topLeft = new Coordinates();
        State.getInstance().getLoadedMapInfo().getBounds(topLeft, null);
        return getNewProjection(topLeft);
    }

}
