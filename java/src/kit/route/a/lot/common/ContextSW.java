package kit.route.a.lot.common;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;


public class ContextSW extends Context {

    private Graphics output;
    
    public ContextSW(int width, int height, Coordinates topLeft, Coordinates bottomRight, Graphics surface) {
        super(width, height, topLeft, bottomRight);
        if (surface == null) {
            throw new IllegalArgumentException();
        }
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
