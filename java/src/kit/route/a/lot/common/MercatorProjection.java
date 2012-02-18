package kit.route.a.lot.common;



public class MercatorProjection extends Projection {

    private Coordinates mTopLeft;
    private Coordinates topLeft;
    float scale;

    protected MercatorProjection(Coordinates topLeft, Coordinates bottomRight, int width) {
        this(topLeft, calculateScaleFromWidth(topLeft, bottomRight, width));
    }


    protected MercatorProjection(Coordinates topLeft, float scale) {
        this.mTopLeft = mercatorCoordinates(topLeft);
        this.topLeft = topLeft;
        this.scale = scale;
    }

    @Override
    public Coordinates getLocalCoordinates(Coordinates geoCoordinates) {
        mTopLeft = mercatorCoordinates(topLeft);
        Coordinates mercatorCoordinates = mercatorCoordinates(geoCoordinates);
        Coordinates localCoordinates = new Coordinates();
        localCoordinates.setLongitude((mercatorCoordinates.getLongitude() - mTopLeft.getLongitude()) / scale);
        localCoordinates.setLatitude((mTopLeft.getLatitude() - mercatorCoordinates.getLatitude()) / scale);
        return localCoordinates;
    }

    @Override
    public Coordinates getGeoCoordinates(Coordinates localCoordinates) {
        mTopLeft = mercatorCoordinates(topLeft);
        Coordinates newLocalCoordinates = new Coordinates();
        newLocalCoordinates.setLatitude(mTopLeft.getLatitude() - (scale * localCoordinates.getLatitude()));
        newLocalCoordinates.setLongitude((scale * localCoordinates.getLongitude()) + mTopLeft.getLongitude());
        Coordinates reverseMercatorCoordinates = reverseMercatorCoordinates(newLocalCoordinates);
        return reverseMercatorCoordinates;
    }

    private static Coordinates mercatorCoordinates(Coordinates geoCoordinates) {
        Coordinates mercatorCoordinates = new Coordinates();
        mercatorCoordinates.setLongitude((geoCoordinates.getLongitude()));
        mercatorCoordinates.setLatitude((float) (arsinh(Math
                .tan(geoCoordinates.getLatitude() * Math.PI / 180)) * 180 / Math.PI));
        return mercatorCoordinates;
    }
    
    private static Coordinates reverseMercatorCoordinates(Coordinates localCoordinates) {
        Coordinates reverseMercatorCoordinates = new Coordinates();
        reverseMercatorCoordinates.setLongitude(localCoordinates.getLongitude());
        reverseMercatorCoordinates.setLatitude((float) (Math.atan(Math.sinh(localCoordinates.getLatitude() * Math.PI
                / 180)) * 180 / Math.PI));
        return reverseMercatorCoordinates;
    }

    private static double arsinh(double x) {
        return Math.log(x + Math.sqrt(x * x + 1));
    }

    public static float calculateScaleFromWidth(Coordinates topLeft, Coordinates bottomRight, int width) {
        return Math.abs(mercatorCoordinates(bottomRight).getLongitude()
                - mercatorCoordinates(topLeft).getLongitude())
                / width;
    }

    public static float calculateScaleFromHeight(Coordinates topLeft, Coordinates bottomRight, int height) {
        return Math.abs(mercatorCoordinates(bottomRight).getLatitude()
                - mercatorCoordinates(topLeft).getLatitude())
                / height;
    }

}
