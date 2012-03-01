package kit.route.a.lot.map.rendering;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import kit.route.a.lot.common.Context2D;
import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.OSMType;
import kit.route.a.lot.common.WayInfo;
import kit.route.a.lot.map.Node;
import kit.route.a.lot.map.Street;


public class RendererTestTester {

    JFrame fenster;
    JPanel panel;
    JLabel label;
    ImageIcon imageB;
    BufferedImage image;
    Container c;
    Graphics g;
    RendererMock renderer;
    MapInfoMock mapInfo;
    Coordinates topLeft;
    Coordinates bottomRight;
    int zoomLevel;
    Context2D context;
    
    public RendererTestTester(){
        
        fenster = new JFrame("RendererPerformance");
        panel = new JPanel();
        panel.setSize(300,200);
        c = fenster.getContentPane();
        image = new BufferedImage(300,200,BufferedImage.TYPE_INT_RGB);
        g = image.getGraphics();
        imageB = new ImageIcon(image);
        label = new JLabel(imageB);
        panel.add(label);
        c.add(panel);
        fenster.setSize(300,200);
        fenster.setVisible(true);
        fenster.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        renderer = new RendererMock();
        mapInfo = (MapInfoMock)renderer.state.getMapInfo();
        topLeft = new Coordinates(0f,0f);
        bottomRight = new Coordinates(40f,40f);
        zoomLevel = 0;
        context = new Context2D(topLeft, bottomRight, zoomLevel, g);
        /*--------------------*/
        WayInfo wayInfo = new WayInfo();
        wayInfo.setStreet(true);
        //wayInfo.setType(OSMType.HIGHWAY_MOTORWAY);
        wayInfo.setType(OSMType.HIGHWAY_SECONDARY);
        Node node1 = new Node(new Coordinates(0.0f, 0.0f));
        Node node2 = new Node(new Coordinates(10.0f, 10.0f));
        Node node3 = new Node(new Coordinates(20.0f, 20.0f));
        Node node4 = new Node(new Coordinates(30.0f, 30.0f));
      
        Street street = new Street("", wayInfo);
        Node[] nodes = new Node[4];
        nodes[0] = node1;
        nodes[1] = node2;
        nodes[2] = node3;
        nodes[3] = node4;
        street.setNodes(nodes);
        mapInfo.addToBaseLayer(street);
        renderer.render(context);
       
        label.repaint();
    }
    
    
    
    
    public static void main(String[] args) {
        //RendererTestTester tester = 
        new RendererTestTester();
    }

}
