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
import kit.route.a.lot.map.Node;
import kit.route.a.lot.map.Street;


public class RendererTest {

    Context context;
    Renderer renderer;

    Coordinates topLeft;
    Coordinates bottomRight;

    public static void main(String[] args) {
        new RendererTest().testDrawAreaAndNodes();
    }

    public void testDrawAreaAndNodes() {
        renderer = new Renderer();
        topLeft = new Coordinates(12f, 12.f);
        bottomRight = new Coordinates(12.2f, 12.2f);

        StateMock state = new StateMock();
        MapInfoMock mapInfo = (MapInfoMock) state.getLoadedMapInfo();

        renderer.state = state;

        WayInfo wayInfo = new WayInfo();
        wayInfo.setBuilding(true);
        Node node1 = new Node(0, new Coordinates(12.032f, 12.0333f));
        Node node2 = new Node(1, new Coordinates(12.1728f, 12.16663f));
        Node node3 = new Node(2, new Coordinates(12.1234f, 12.1458f));
        Node node4 = new Node(3, new Coordinates(12.0788f, 12.1234f));
        Area area = new Area("", wayInfo);
        Node[] nodes = new Node[4];
        nodes[0] = node1;
        nodes[1] = node2;
        nodes[2] = node3;
        nodes[3] = node4;
        area.setNodes(nodes);
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
        topLeft = new Coordinates(12.f, 12.f);
        bottomRight = new Coordinates(12.2f, 12.2f);

        StateMock state = new StateMock();
        MapInfoMock mapInfo = (MapInfoMock) state.getLoadedMapInfo();

        renderer.state = state;

        WayInfo wayInfo = new WayInfo();
        wayInfo.setStreet(true);
        Node[] nodes = new Node[4];
        nodes[0] = new Node(0, new Coordinates(12.032f, 12.0333f));
        nodes[1] = new Node(1, new Coordinates(12.0788f, 12.1234f));
        nodes[2] = new Node(2, new Coordinates(12.1234f, 12.1458f));
        nodes[3] = new Node(3, new Coordinates(12.1728f, 12.16663f));
        Street street = new Street("", wayInfo);
        street.setNodes(nodes);
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
