package kit.route.a.lot.common;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import kit.route.a.lot.map.rendering.MercatorProjection;
import kit.route.a.lot.map.rendering.Projection;


public class ContextSW extends Context {

    private Graphics output;
    private Projection projection;

    public ContextSW(int width, int height, Coordinates topLeft, Coordinates bottomRight, Graphics surface) {
        super(width, height, topLeft, bottomRight);
        if (surface == null) {
            throw new IllegalArgumentException();
        }
        output = surface;
    }

    public ContextSW(Coordinates topLeft, int width, int height, float scale, Graphics surface) {
        super(width, height, topLeft, null);
        output = surface;
        projection = new MercatorProjection(topLeft, scale);
        Coordinates localTopLeft = projection.geoCoordinatesToLocalCoordinates(topLeft);
        Coordinates localBottomRight =
                new Coordinates(localTopLeft.getLatitude() - height, localTopLeft.getLongitude() + width);
        bottomRight = projection.localCoordinatesToGeoCoordinates(localBottomRight);
    }

    public ContextSW(Coordinates topLeft, Coordinates bottomRight, Graphics surface) {
        super(0, 0, topLeft, bottomRight);
        output = surface;
        width = (int) Math.abs(bottomRight.getLongitude() - topLeft.getLongitude());
        height = (int) Math.abs(bottomRight.getLatitude() - topLeft.getLatitude());
    }

    @Override
    public void fillBackground(Color color) {
        output.setColor(color);
        output.fillRect(0, 0, width, height);
    }

    @Override
    public void drawImage(Coordinates position, Image image, int detail) {
        int x = (int) (position.getLongitude() - topLeft.getLongitude()) / (detail + 1);
        int y = (int) (position.getLatitude() - topLeft.getLatitude()) / (detail + 1);
        output.drawImage(image, x, y, null);
    }

    @Override
    public float getScale() {
        return 1;
    }
    
    @Override
    public void setBottomRight(Coordinates bottomRight) {
        super.setBottomRight(bottomRight);
        recalculateSize();
    }
    
    @Override
    public void recalculateSize() {
        width = (int) Math.abs(bottomRight.getLongitude() - topLeft.getLongitude());
        height = (int) Math.abs(bottomRight.getLatitude() - topLeft.getLatitude());
    }

    public void setSurface(Graphics surface) {
        output = surface;
    }
}
