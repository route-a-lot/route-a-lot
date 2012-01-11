package kit.route.a.lot.map.rendering;

import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.JFrame;

import kit.route.a.lot.common.Context;
import kit.route.a.lot.common.ContextSW;
import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.WayInfo;
import kit.route.a.lot.map.Area;
import kit.route.a.lot.map.Edge;
import kit.route.a.lot.map.Node;
import kit.route.a.lot.map.Street;

import org.junit.Test;


public class RendererTest {

    public static void main(String[] args) {
        new TestGUI();
    }
    
    public static void testDrawEdgeAndNodes(TestGUI gui) {
        Renderer renderer = new Renderer();
        Coordinates topLeft = new Coordinates(12., 12.);
        Coordinates bottomRight = new Coordinates(12.2, 12.2);

        StateMock state = new StateMock();
        MapInfoMock mapInfo = (MapInfoMock) state.getLoadedMapInfo();

        renderer.state = state;
                
        mapInfo.addMapElement(new Node(0, new Coordinates(12.032, 12.0333)));
        mapInfo.addMapElement(new Node(0, new Coordinates(12.1728, 12.16663)));
        mapInfo.addMapElement(new Edge(new Node(0, new Coordinates(12.0788, 12.1234)), new Node(0, new Coordinates(12.1234, 12.1458)), new Street(0, null, null)));
 
        gui.setVisible(true); 
        Context context = new ContextSW(100, 100, topLeft, bottomRight, (Graphics2D) gui.getGraphics());      
        renderer.render(context, 0);
    }
    
    public static void testDrawAreaAndNodes(TestGUI gui) {
        Renderer renderer = new Renderer();
        Coordinates topLeft = new Coordinates(12., 12.);
        Coordinates bottomRight = new Coordinates(12.2, 12.2);

        StateMock state = new StateMock();
        MapInfoMock mapInfo = (MapInfoMock) state.getLoadedMapInfo();

        renderer.state = state;
                
        WayInfo wayInfo = new WayInfo();
        wayInfo.setBuilding(true);
        Node node1 = new Node(0, new Coordinates(12.032, 12.0333));
        Node node2 = new Node(1, new Coordinates(12.1728, 12.16663));
        Node node3 = new Node(2, new Coordinates(12.1234, 12.1458));
        Node node4 = new Node(3, new Coordinates(12.0788, 12.1234));
        Area area = new Area(0, "", wayInfo);
        area.addNode(node1);
        area.addNode(node2);
        area.addNode(node3);
        area.addNode(node4);
        mapInfo.addMapElement(node1);
        mapInfo.addMapElement(node2);
        mapInfo.addMapElement(node3);
        mapInfo.addMapElement(node4);
        mapInfo.addMapElement(area);
        
        gui.setVisible(true);  
        Context context = new ContextSW(100, 100, topLeft, bottomRight, (Graphics2D) gui.getGraphics());         
        renderer.render(context, 0);
    }
    
    public static void testDrawStreet(TestGUI gui) {
        Renderer renderer = new Renderer();
        Coordinates topLeft = new Coordinates(12., 12.);
        Coordinates bottomRight = new Coordinates(12.2, 12.2);

        StateMock state = new StateMock();
        MapInfoMock mapInfo = (MapInfoMock) state.getLoadedMapInfo();

        renderer.state = state;
                
        WayInfo wayInfo = new WayInfo();
        wayInfo.setStreet(true);
        Node node1 = new Node(0, new Coordinates(12.032, 12.0333));
        Node node2 = new Node(1, new Coordinates(12.0788, 12.1234));
        Node node3 = new Node(2, new Coordinates(12.1234, 12.1458));
        Node node4 = new Node(3, new Coordinates(12.1728, 12.16663));
        Street street = new Street(0, "", wayInfo);
        street.addEdge(new Edge(node1, node2, street));
        street.addEdge(new Edge(node2, node3, street));
        street.addEdge(new Edge(node3, node4, street));
        mapInfo.addMapElement(street);

        gui.setVisible(true); // must be done before calling gui.getGraphics()
        Context context = new ContextSW(200, 200, topLeft, bottomRight, (Graphics2D) gui.getGraphics());        
        renderer.render(context, 0);
    }
    
    static class TestGUI extends JFrame {

        private static final long serialVersionUID = 1L;

        public TestGUI() {
            super("Test");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setSize(250, 250);
            this.setLocation(new Point(500, 500));  
            repaint();
            testDrawStreet(this);
        }
        
    }

}
