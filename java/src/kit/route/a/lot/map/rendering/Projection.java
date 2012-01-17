package kit.route.a.lot.map.rendering;

import kit.route.a.lot.common.Coordinates;


public abstract class Projection {

    public abstract Coordinates geoCoordinatesToLocalCoordinates(Coordinates geoCoordinates);
    
    public abstract Coordinates localCoordinatesToGeoCoordinates(Coordinates localCoordinates);
    
    public abstract float getScale();

}
