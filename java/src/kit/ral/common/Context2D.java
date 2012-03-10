package kit.ral.common;

import java.awt.Graphics;

public class Context2D extends Context {
 
    private Graphics graphics;   
    private Bounds bounds;
    
    public Context2D(Bounds bounds, int detailLevel, Graphics graphics) {
        super(detailLevel);
        this.bounds = bounds;
        this.graphics = graphics;
    }

    public Bounds getBounds() {
        return bounds;
    }
    
    public Graphics getGraphics() {
        return this.graphics;
    }

}
