package kit.route.a.lot.common;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Component;

public class Context extends Component{

    private int width;
    private int height;
    private Coordinates topLeft;
    private Coordinates bottomRight;
    private BufferedImage bgImage;
    private Color bgColor;

    public Context(int width, int height, Coordinates topLeft, Coordinates bottomRight) {
        this.bgImage = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        this.width = width;
        this.height = height;
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
	this.bgColor = Color.WHITE;

    }
    
    /**
     * Operation fillBackground
     * 
     * @param color
     */
    public void fillBackground(Color color) {
		this.bgColor = color;
		Graphics gImage = bgImage.getGraphics();
                gImage.setColor(bgColor);
                gImage.fillRect(0, 0, bgImage.getWidth(), bgImage.getHeight());
		repaint();
    }

    /**
     * Operation drawImage
     * 
     * @param x
     * @param y
     * @param image
     */
    public void drawImage(int x, int y, Image image) {
		Graphics g = bgImage.getGraphics();
		g.drawImage(image,x,y,null);
		repaint();
    }


    public Dimension getPreferredSize(){
	return new Dimension(bgImage.getWidth(), bgImage.getHeight());
    }
    
    
    public void paint(Graphics g){
	//den Hintergrund zeichnen
		g.drawImage(bgImage,0,0,null);
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
