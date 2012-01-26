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

    public Coordinates getTopLeft() {
        return topLeft;
    }
    
    public Coordinates getBottomRight() {
        return bottomRight;
    } 
    
    public float getWidth() {
        return bottomRight.getLongitude() - topLeft.getLongitude();
    }
    
    public float getHeight() {
        return bottomRight.getLatitude() - topLeft.getLatitude();
    }
}
