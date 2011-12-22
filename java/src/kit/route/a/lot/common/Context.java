package kit.route.a.lot.common;

import java.awt.Color;
import java.awt.Image;


public class Context {

    private int width;
    private int height;
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
    public void fillBackground(Color color) {
    }

    /**
     * Operation drawImage
     * 
     * @param x
     * @param y
     * @param image
     */
    public void drawImage(int x, int y, Image image) {
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public Coordinates getTopLeft() {
        return topLeft;
    }
    
    public Coordinates getBottomRight() {
        return bottomRight;
    }
    
}
