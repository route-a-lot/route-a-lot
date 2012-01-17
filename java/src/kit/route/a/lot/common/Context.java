package kit.route.a.lot.common;

import java.awt.Color;
import java.awt.Image;

public abstract class Context {

    protected int width; // in pixels
    protected int height; // in pixels
    protected Coordinates topLeft;
    protected Coordinates bottomRight;

    public Context(int width, int height, Coordinates topLeft, Coordinates bottomRight) {
        this.width = width;
        this.height = height;
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
    }
    
    /**
     * Operation fillBackground
     * 
     * @param color
     */
    public abstract void fillBackground(Color color);
    
    /**
     * Operation drawImage
     * 
     * @param x
     * @param y
     * @param image
     */
    public abstract void drawImage(Coordinates position, Image image);
    
    public abstract float getScale();

    public int getWidth() {
        return this.width;
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public Coordinates getTopLeft() {
        return this.topLeft;
    }
    
    public Coordinates getBottomRight() {
        return this.bottomRight;
    }

    
    public void setWidth(int width) {
        this.width = width;
    }

    
    public void setHeight(int height) {
        this.height = height;
    }

    
    public void setTopLeft(Coordinates topLeft) {
        this.topLeft = topLeft;
    }

    
    public void setBottomRight(Coordinates bottomRight) {
        this.bottomRight = bottomRight;
    }
    
    
    
}
