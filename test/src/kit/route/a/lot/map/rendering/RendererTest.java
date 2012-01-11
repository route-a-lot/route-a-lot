package kit.route.a.lot.map.rendering;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JFrame;
import javax.swing.JPanel;

import kit.route.a.lot.common.Context;
import kit.route.a.lot.common.ContextSW;
import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.WayInfo;
import kit.route.a.lot.map.Area;
import kit.route.a.lot.map.Edge;
import kit.route.a.lot.map.Node;
import kit.route.a.lot.map.Street;


public class RendererTest {

    Context context;
    Renderer renderer;

    Coordinates topLeft;
    Coordinates bottomRight;

    public static void main(String[] args) {
        new RendererTest().testDrawStreet();
    }

    public void testDrawEdgeAndNodes() {
        renderer = new Renderer();
        topLeft = new Coordinates(12., 12.);
        bottomRight = new Coordinates(12.2, 12.2);

        StateMock state = new StateMock();
        MapInfoMock mapInfo = (MapInfoMock) state.getLoadedMapInfo();

        renderer.state = state;

        mapInfo.addMapElement(new Node(0, new Coordinates(12.032, 12.0333)));
        mapInfo.addMapElement(new Node(0, new Coordinates(12.1728, 12.16663)));
        mapInfo.addMapElement(new Edge(new Node(0, new Coordinates(12.0788, 12.1234)), new Node(0,
                new Coordinates(12.1234, 12.1458)), new Street(0, null, null)));

        TestGUI gui = new TestGUI(this);
        gui.setVisible(true);
        context = new ContextSW(200, 200, topLeft, bottomRight, gui.getGraphicsForRenderedContent());

    }

    public void testDrawAreaAndNodes() {
        renderer = new Renderer();
        topLeft = new Coordinates(12., 12.);
        bottomRight = new Coordinates(12.2, 12.2);

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

        TestGUI gui = new TestGUI(this);
        gui.setVisible(true);
        context = new ContextSW(200, 200, topLeft, bottomRight, gui.getGraphicsForRenderedContent());

    }

    public void testDrawStreet() {
        renderer = new Renderer();
        topLeft = new Coordinates(12., 12.);
        bottomRight = new Coordinates(12.2, 12.2);

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

        TestGUI gui = new TestGUI(this);
        gui.setVisible(true);
        context = new ContextSW(200, 200, topLeft, bottomRight, gui.getGraphicsForRenderedContent());

    }

    public void repaint() {
        renderer.render(context, 0);
    }

    public class TestGUI extends JFrame {

        private static final long serialVersionUID = 1L;
        
        Component rendererdContent;
        RendererTest rendererTest;

        public TestGUI(RendererTest rendererTest) {
            super("Test");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setSize(250, 250);
            this.setLocation(new Point(500, 500));
            
            rendererdContent = this.add(new JPanel());
            rendererdContent.setVisible(true);
            rendererdContent.setSize(250, 250);
            
            this.rendererTest = rendererTest;
        }
        
        public Graphics getGraphicsForRenderedContent() {
            return rendererdContent.getGraphics();
        }
        
        public void paint(Graphics g) {
            super.paint(g);
            rendererTest.repaint();
        }

    }

}
