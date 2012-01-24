package kit.route.a.lot.common;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import kit.route.a.lot.map.rendering.Projection;


public class Context2D extends Context {
    
    private static Logger logger = Logger.getLogger(Context2D.class);
    static {
        logger.setLevel(Level.INFO);
    }
    
    private Graphics output;
    //private Projection projection;

    // only used for tests, keep?
    public Context2D(int width, int height, Coordinates topLeft, Coordinates bottomRight, Graphics surface) {
        super(width, height, topLeft, bottomRight);
        if (surface == null) {
            throw new IllegalArgumentException();
        }
        output = surface;
    }

    /*public Context2D(Coordinates topLeft, int width, int height, float scale, Graphics surface) {
        super(width, height, topLeft, null);
        output = surface;
        projection = Projection.getNewProjection(topLeft);
        Coordinates localTopLeft = projection.geoCoordinatesToLocalCoordinates(topLeft);
        Coordinates localBottomRight =
                new Coordinates(localTopLeft.getLatitude() - height, localTopLeft.getLongitude() + width);
        bottomRight = projection.localCoordinatesToGeoCoordinates(localBottomRight);
    }*/

    public Context2D(Coordinates topLeft, Coordinates bottomRight, Graphics surface) {
        super(0, 0, topLeft, bottomRight);
        if (surface == null) {
            throw new IllegalArgumentException("Graphics not yet initialized.");
        }
        output = surface;
        //calculateSize(); // TODO what does it do?
    }

    @Override
    public void fillBackground(Color color) {
        output.setColor(color);
        output.fillRect(0, 0, width, height);
    }

    @Override
    public void drawImage(Coordinates position, Image image, int detail) {
        int x = (int) ((position.getLongitude() - topLeft.getLongitude()) / Projection.getZoomFactor(detail));
        int y = (int) ((position.getLatitude() - topLeft.getLatitude()) / Projection.getZoomFactor(detail));
        output.drawImage(image, x, y, null);
    }

    @Override
    public float getScale() {
        return 1;
    }
    
    @Override
    public void calculateSize() {
        width = (int) Math.abs(bottomRight.getLongitude() - topLeft.getLongitude());
        height = (int) Math.abs(bottomRight.getLatitude() - topLeft.getLatitude());
    }

}
