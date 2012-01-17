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
        this.output = surface;
        projection = new MercatorProjection(topLeft, bottomRight, width);
    }

    @Override
    public void fillBackground(Color color) {
        output.setColor(color);
        output.fillRect(0, 0, getWidth(), getHeight());
    }

    @Override
    public void drawImage(Coordinates position, Image image) {
        Coordinates localCoordinates = projection.geoCoordinatesToLocalCoordinates(position);
        output.drawImage(image, (int) localCoordinates.getLongitude(), (int) localCoordinates.getLatitude(), null);
    }
    
    @Override
    public float getScale() {
        return projection.getScale();
    }
    
}

