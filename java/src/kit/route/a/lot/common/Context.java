package kit.route.a.lot.common;

import java.awt.Color;
import java.awt.Image;

public abstract class Context {

    private int width; // in pixels
    private int height; // in pixels
    private Coordinates topLeft;
    private Coordinates bottomRight;

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
    
}
