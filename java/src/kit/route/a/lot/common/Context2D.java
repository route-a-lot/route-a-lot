package kit.route.a.lot.common;

import java.awt.Graphics;

public class Context2D extends Context {
 
    private Graphics output;   
    
    public Context2D(Coordinates topLeft, Coordinates bottomRight, Graphics surface) {
        super(topLeft, bottomRight);
        if (surface == null) {
            throw new IllegalArgumentException("Graphics not yet initialized.");
        }
        output = surface;
    }

    public Graphics getGraphics() {
        return output;
    }

}
