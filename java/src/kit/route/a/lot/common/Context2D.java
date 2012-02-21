package kit.route.a.lot.common;

import java.awt.Graphics;

public class Context2D extends Context {
 
    private Graphics graphics;   
    private Coordinates topLeft, bottomRight;
    
    public Context2D(Coordinates topLeft, Coordinates bottomRight, int detailLevel, Graphics graphics) {
        super(detailLevel);
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
        this.graphics = graphics;
    }

    public Coordinates getTopLeft() {
        return topLeft;
    }
    
    public Coordinates getBottomRight() {
        return bottomRight;
    }
    
    public Graphics getGraphics() {
        return this.graphics;
    }

}
