package kit.route.a.lot.common;

import java.awt.Graphics;
import java.awt.Image;



public class Context2D extends Context {
 
    private Graphics output;   
    
    public Context2D(Coordinates topLeft, Coordinates bottomRight, Graphics surface) {
        super(topLeft, bottomRight);
        if (surface == null) {
            throw new IllegalArgumentException("Graphics not yet initialized.");
        }
        output = surface;
    }

    @Override
    public void drawImage(Coordinates position, Image image, int detail) {
        int x = (int) ((position.getLongitude() - topLeft.getLongitude()) / Projection.getZoomFactor(detail));
        int y = (int) ((position.getLatitude() - topLeft.getLatitude()) / Projection.getZoomFactor(detail));
        output.drawImage(image, x, y, null);
    }

}
