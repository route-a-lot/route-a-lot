package kit.route.a.lot.map.rendering;

import kit.route.a.lot.common.Coordinates;


public class MercatorProjection extends Projection {

    private Coordinates mTopLeft;
    private Coordinates topLeft;
    float scale;

    public MercatorProjection(Coordinates topLeft, Coordinates bottomRight, int width) {
        this(topLeft, calculateScaleFromWidth(topLeft, bottomRight, width));
    }


    public MercatorProjection(Coordinates topLeft, float scale) {
        this.mTopLeft = mercatorCoordinates(topLeft);
        this.topLeft = topLeft;
        this.scale = scale;
    }

    @Override
    public Coordinates geoCoordinatesToLocalCoordinates(Coordinates geoCoordinates) {
        mTopLeft = mercatorCoordinates(topLeft);
        Coordinates mercatorCoordinates = mercatorCoordinates(geoCoordinates);
        Coordinates localCoordinates = new Coordinates();
        localCoordinates.setLongitude((mercatorCoordinates.getLongitude() - mTopLeft.getLongitude()) / scale);
        localCoordinates.setLatitude((mTopLeft.getLatitude() - mercatorCoordinates.getLatitude()) / scale);
        return localCoordinates;
    }

    private static Coordinates mercatorCoordinates(Coordinates geoCoordinates) {
        Coordinates mercatorCoordinates = new Coordinates();
        mercatorCoordinates.setLongitude((geoCoordinates.getLongitude()));
        mercatorCoordinates.setLatitude((float) (arsinh(Math
                .tan(geoCoordinates.getLatitude() * Math.PI / 180)) * 180 / Math.PI));
        return mercatorCoordinates;
    }

    private static double arsinh(double x) {
        return Math.log(x + Math.sqrt(x * x + 1));
    }

    @Override
    public float getScale() {
        return scale;
    }
    
    public static float calculateScaleFromWidth(Coordinates topLeft, Coordinates bottomRight, int width) {
        return Math.abs(mercatorCoordinates(bottomRight).getLongitude()
                - mercatorCoordinates(topLeft).getLongitude()) / width;
    }
    
    public static float calculateScaleFromHeight(Coordinates topLeft, Coordinates bottomRight, int height) {
        return Math.abs(mercatorCoordinates(bottomRight).getLatitude()
                - mercatorCoordinates(topLeft).getLatitude()) / height;
    }

}
