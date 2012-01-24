package kit.route.a.lot.common;

import java.awt.Graphics;
import java.awt.Image;

import kit.route.a.lot.map.rendering.Projection;


public class Context2D extends Context {
 
    private Graphics output;   

    public Context2D(Coordinates topLeft, Coordinates bottomRight, Graphics surface) {
        super(topLeft, bottomRight);
        if (surface == null) {
            throw new IllegalArgumentException("Graphics not yet initialized.");
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

    @Override
    public void drawImage(Coordinates position, Image image, int detail) {
        int x = (int) ((position.getLongitude() - topLeft.getLongitude()) / Projection.getZoomFactor(detail));
        int y = (int) ((position.getLatitude() - topLeft.getLatitude()) / Projection.getZoomFactor(detail));
        output.drawImage(image, x, y, null);
    }

}
