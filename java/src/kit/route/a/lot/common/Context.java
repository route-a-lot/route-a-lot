package kit.route.a.lot.common;

import java.awt.Image;

public abstract class Context {

    protected Coordinates topLeft;
    protected Coordinates bottomRight;

    public Context(Coordinates topLeft, Coordinates bottomRight) {
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
    }
    
    public abstract void drawImage(Coordinates position, Image image, int detail);
       
    public float getScale() {
        return 1;
    };

    public Coordinates getTopLeft() {
        return this.topLeft;
    }
    
    public Coordinates getBottomRight() {
        return this.bottomRight;
    } 
    
}
