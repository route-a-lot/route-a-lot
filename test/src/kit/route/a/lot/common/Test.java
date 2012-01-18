package kit.route.a.lot.common;

import javax.swing.JFrame;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.Graphics;

class Test{ 

public static void main(String[]args){

BufferedImage image = new BufferedImage(200,200,BufferedImage.TYPE_INT_RGB);
Graphics g = image.getGraphics();
g.setColor(Color.ORANGE);
g.fillRect(0,0,200,200);
JFrame fenster = new JFrame();
fenster.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
fenster.setVisible(true);
/*Context context = new Context(500,500,null,null);
context.fillBackground(Color.GREEN);
context.drawImage(0,0,image);
fenster.add(context);
*/
fenster.pack();


}//end main

	

}//end class
