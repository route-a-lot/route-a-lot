package kit.route.a.lot.map.rendering;

import kit.route.a.lot.common.Coordinates;


public class SimpleProjection extends Projection {

    private Coordinates topLeft;
    private Coordinates bottomRight;
    private int width;
    private int height;

    SimpleProjection(Coordinates topLeft, Coordinates bottomRight, int width, int height) {
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
        this.width = width;
        this.height = height;
    }

    @Override
    public Coordinates geoCoordinatesToLocalCoordinates(Coordinates geoCoordinates) {
        Coordinates localCoordinates = new Coordinates();
        localCoordinates.setLatitude((geoCoordinates.getLatitude() - topLeft.getLatitude()) * width
                / (bottomRight.getLatitude() - topLeft.getLatitude()));
        localCoordinates.setLongitude((geoCoordinates.getLongitude() - topLeft.getLongitude()) * height
                / (bottomRight.getLongitude() - topLeft.getLongitude()));
        return localCoordinates;
    }

    @Override
    public float getScale() {
        return 1;
    }
}
