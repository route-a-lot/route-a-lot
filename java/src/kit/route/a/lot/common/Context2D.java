package kit.route.a.lot.common;

import java.awt.Graphics;

public class Context2D extends Context {
 
    private Graphics graphics;   
    
    public Context2D(Coordinates topLeft, Coordinates bottomRight, Graphics graphics, int zoomlevel) {
        super(topLeft, bottomRight, zoomlevel);
        if (graphics == null) {
            throw new IllegalArgumentException("Graphics not yet initialized.");
        }
        this.graphics = graphics;
    }

    public Graphics getGraphics() {
        return this.graphics;
    }

}
