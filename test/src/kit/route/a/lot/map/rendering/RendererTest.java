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
        new RendererTest().testDrawCircle();
    }
    
    public void testDrawAreaAndNodes() {
        renderer = new Renderer();
        topLeft = new Coordinates(12f, 12.f);
        bottomRight = new Coordinates(12.2f, 12.2f);

        StateMock state = new StateMock();
        MapInfoMock mapInfo = (MapInfoMock) state.getLoadedMapInfo();

        renderer.state = state;

        WayInfo wayInfo = new WayInfo();
        wayInfo.setStreet(true);
        Node node1 = new Node(new Coordinates(12.032f, 12.0333f));
        Node node2 = new Node(new Coordinates(12.1728f, 12.16663f));
        Node node3 = new Node(new Coordinates(12.1234f, 12.1458f));
        Node node4 = new Node(new Coordinates(12.0788f, 12.1234f));
        node1.initID(0);
        node2.initID(1);
        node3.initID(2);
        node4.initID(3);
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
        
        mapInfo.addMapElement(new Node(topLeft));
        mapInfo.addMapElement(new Node(bottomRight));

        TestGUI gui = new TestGUI(this);
        gui.setVisible(true);
        context = new ContextSW(200, 200, topLeft, bottomRight, gui.getGraphicsForRenderedContent());

    }

    public void testDrawCircle() {
        renderer = new Renderer();
        topLeft = new Coordinates(49.019887f, 8.394492f);
        bottomRight = new Coordinates(49.008375f, 8.414061f);

        StateMock state = new StateMock();
        MapInfoMock mapInfo = (MapInfoMock) state.getLoadedMapInfo();

        renderer.state = state;

        WayInfo wayInfo = new WayInfo();
        wayInfo.setBuilding(true);
        Node[] nodes = new Node[8];
        nodes[0] = new Node(49.017945f, 8.404362f, 0);
        nodes[1] = new Node(49.016397f, 8.408911f, 1);
        nodes[2] = new Node(49.01423f, 8.410413f, 2);
        nodes[3] = new Node(49.011866f, 8.409383f, 3);
        nodes[4] = new Node(49.010036f, 8.404748f, 4);
        nodes[5] = new Node(49.011584f, 8.399727f, 5);
        nodes[6] = new Node(49.014118f, 8.398311f, 6);
        nodes[7] = new Node(49.01696f, 8.400285f, 7);
//        nodes[0] = new Node(0.01f, 0.0f, 0);
//        nodes[1] = new Node(0.007f, 0.003f, 1);
//        nodes[2] = new Node(0.0f, 0.01f, 2);
//        nodes[3] = new Node(-0.007f, -0.003f, 3);
//        nodes[4] = new Node(-0.01f, -0.01f, 4);
//        nodes[5] = new Node(-0.007f, -0.003f, 5);
//        nodes[6] = new Node(0.0f, 0.0f, 6);
//        nodes[7] = new Node(0.007f, 0.003f, 7);
        Street street = new Street("", wayInfo);
        street.setNodes(nodes);
        mapInfo.addMapElement(street);

        TestGUI gui = new TestGUI(this);
        gui.setVisible(true);
        context = new ContextSW(500, 200, topLeft, bottomRight, gui.getGraphicsForRenderedContent());

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
        nodes[0] = new Node(new Coordinates(12.032f, 12.0333f));
        nodes[1] = new Node(new Coordinates(12.0788f, 12.1234f));
        nodes[2] = new Node(new Coordinates(12.1234f, 12.1458f));
        nodes[3] = new Node(new Coordinates(12.1728f, 12.16663f));
        nodes[0].initID(0);
        nodes[1].initID(1);
        nodes[2].initID(2);
        nodes[3].initID(3);
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
