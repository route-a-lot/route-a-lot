package kit.route.a.lot.map.rendering;

import java.awt.Component;
import java.awt.Point;

import javax.swing.JFrame;

import kit.route.a.lot.common.Context;
import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.map.Area;
import kit.route.a.lot.map.Edge;
import kit.route.a.lot.map.Node;
import kit.route.a.lot.map.Street;

import org.junit.Test;


public class RendererTest {

    public static void main(String[] args) {
        new RendererTest().testDrawAreaAndNodes();
    }
    
    public void testDrawEdgeAndNodes() {
        Renderer renderer = new Renderer();
        Coordinates topLeft = new Coordinates(12., 12.);
        Coordinates bottomRight = new Coordinates(12.2, 12.2);

        StateMock state = new StateMock();
        MapInfoMock mapInfo = (MapInfoMock) state.getLoadedMapInfo();

        renderer.state = state;
                
        mapInfo.addMapElement(new Node(0, new Coordinates(12.032, 12.0333)));
        mapInfo.addMapElement(new Node(0, new Coordinates(12.1728, 12.16663)));
        mapInfo.addMapElement(new Edge(new Node(0, new Coordinates(12.0788, 12.1234)), new Node(0, new Coordinates(12.1234, 12.1458)), new Street(0, null, null)));

        Context context = new Context(100, 100, topLeft, bottomRight);

        TestGUI gui = new TestGUI(context); 
        gui.setVisible(true);
        
        renderer.render(context, 0);
    }
    
    public void testDrawAreaAndNodes() {
        Renderer renderer = new Renderer();
        Coordinates topLeft = new Coordinates(12., 12.);
        Coordinates bottomRight = new Coordinates(12.2, 12.2);

        StateMock state = new StateMock();
        MapInfoMock mapInfo = (MapInfoMock) state.getLoadedMapInfo();

        renderer.state = state;
                
        Node node1 = new Node(0, new Coordinates(12.032, 12.0333));
        Node node2 = new Node(1, new Coordinates(12.1728, 12.16663));
        Node node3 = new Node(2, new Coordinates(12.1234, 12.1458));
        Node node4 = new Node(3, new Coordinates(12.0788, 12.1234));
        Area area = new Area(0, "", null);
        area.addNode(node1);
        area.addNode(node2);
        area.addNode(node3);
        area.addNode(node4);
        mapInfo.addMapElement(node1);
        mapInfo.addMapElement(node2);
        mapInfo.addMapElement(node3);
        mapInfo.addMapElement(node4);
        mapInfo.addMapElement(area);

        Context context = new Context(100, 100, topLeft, bottomRight);

        TestGUI gui = new TestGUI(context); 
        gui.setVisible(true);
        
        renderer.render(context, 0);
    }
    
    class TestGUI extends JFrame {
        
        Component compContext;
        
        public TestGUI(Context context) {
            super("Test");

            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            compContext = this.add(context);
            this.setSize(200, 200);
            this.setLocation(new Point(500, 500));
            repaint();
        }
        
    }

}
