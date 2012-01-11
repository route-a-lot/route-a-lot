package kit.route.a.lot.common;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;


public class ContextSW extends Context {

    private Graphics2D output;
    
    public ContextSW(int width, int height, Coordinates topLeft, Coordinates bottomRight, Graphics2D surface) {
        super(width, height, topLeft, bottomRight);
        this.output = surface;
    }
    
    @Override
    public void fillBackground(Color color) {
        output.setColor(color);
        output.fillRect(0, 0, getWidth(), getHeight()); 
    }

    @Override
    public void drawImage(Coordinates position, Image image) {
            // TODO: Transformation Coordinates -> Pixelwerte
            output.drawImage(image, 0/*x*/, 0/*y*/, null);
    }

}
