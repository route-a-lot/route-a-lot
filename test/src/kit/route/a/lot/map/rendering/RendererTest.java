package kit.route.a.lot.map.rendering;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

import kit.route.a.lot.common.Context;
import kit.route.a.lot.common.Context2D;
import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Projection;
import kit.route.a.lot.common.ProjectionFactory;
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
        node1.assignID(0);
        node2.assignID(1);
        node3.assignID(2);
        node4.assignID(3);
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
        context = new Context2D(topLeft, bottomRight, gui.getGraphicsForRenderedContent());

    }

    public void testDrawCircle() {
        renderer = new Renderer();
        topLeft = new Coordinates(49.019887f, 8.394492f);
        topLeft = new Coordinates(0.012f, -0.012f);
        bottomRight = new Coordinates(49.008375f, 8.414061f);
        bottomRight = new Coordinates(-0.012f, 0.012f);
        Projection projection = ProjectionFactory.getNewProjection(topLeft, bottomRight); //2.9E-5f
        topLeft = projection.geoCoordinatesToLocalCoordinates(new Coordinates(49.019887f, 8.394492f));
        bottomRight = new Coordinates(550, 550);

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
        nodes[0] = new Node(0.01f, 0.0f, 0);
        nodes[1] = new Node(0.007f, 0.003f, 1);
        nodes[2] = new Node(0.0f, 0.01f, 2);
        nodes[3] = new Node(-0.007f, -0.003f, 3);
        nodes[4] = new Node(-0.01f, -0.01f, 4);
        nodes[5] = new Node(-0.007f, -0.003f, 5);
        nodes[6] = new Node(0.0f, 0.0f, 6);
        nodes[7] = new Node(0.007f, 0.003f, 7);
        Street street = new Street("", wayInfo);
        street.setNodes(nodes);
//        mapInfo.addMapElement(street);
        mapInfo.addMapElement(new Node(projection.geoCoordinatesToLocalCoordinates(new Coordinates(49.017945f, 8.404362f))));
        mapInfo.addMapElement(new Node(topLeft));
        
        TestGUI gui = new TestGUI(this);
        gui.setVisible(true);
        bottomRight = new Coordinates();
        bottomRight.setLatitude(gui.getVisibleRectangleOfContent().height);
        bottomRight.setLongitude(gui.getVisibleRectangleOfContent().width);
        mapInfo.addMapElement(new Node(bottomRight));
        context = new Context2D(topLeft, bottomRight, gui.getGraphicsForRenderedContent());

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
        nodes[0].assignID(0);
        nodes[1].assignID(1);
        nodes[2].assignID(2);
        nodes[3].assignID(3);
        Street street = new Street("", wayInfo);
        street.setNodes(nodes);
        mapInfo.addMapElement(street);

        TestGUI gui = new TestGUI(this);
        gui.setVisible(true);
        context = new Context2D(topLeft, bottomRight, gui.getGraphicsForRenderedContent());

    }

    public void repaint() {
        renderer.render(context, 0);
    }

    public class TestGUI extends JFrame {

        private static final long serialVersionUID = 1L;
        
        JPanel rendererdContent;
        RendererTest rendererTest;

        public TestGUI(RendererTest rendererTest) {
            super("Test");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setSize(650, 650);
            this.setLocation(new Point(500, 500));
            
            rendererdContent = new JPanel();
            this.add(rendererdContent);
            rendererdContent.setVisible(true);
            rendererdContent.setPreferredSize(getSize());
            
            this.rendererTest = rendererTest;
            
            this.addKeyListener(new KeyListener() {

                @Override
                public void keyPressed(KeyEvent arg0) {
                    
//                    // System.out.println("Key pressed");
                    
                    float diffLat = 0;
                    float diffLon = 0;
                    switch (arg0.getKeyCode()) {
                        case KeyEvent.VK_LEFT:
                            diffLon = -5f;
                            break;
                        case KeyEvent.VK_RIGHT:
                            diffLon = 5f;
                            break;
                        case KeyEvent.VK_UP:
                            diffLat = -5f;
                            break;
                        case KeyEvent.VK_DOWN:
                            diffLat = 5f;
                            break;
                    }
                    topLeft.setLatitude(topLeft.getLatitude() + diffLat);
                    topLeft.setLongitude(topLeft.getLongitude() + diffLon);
                    bottomRight.setLatitude(bottomRight.getLatitude() + diffLat);
                    bottomRight.setLongitude(bottomRight.getLongitude() + diffLon);
                    
                    repaint();
                }

                @Override
                public void keyReleased(KeyEvent arg0) {
//                    // System.out.println("Key released");
                }

                @Override
                public void keyTyped(KeyEvent arg0) {
//                    // System.out.println("Key typed");
                }
                
            });
        }
        
        public Graphics getGraphicsForRenderedContent() {
            return rendererdContent.getGraphics();
        }
        
        public void paint(Graphics g) {
            super.paint(g);
            rendererTest.repaint();
        }
        
        public Rectangle getVisibleRectangleOfContent() {
            return rendererdContent.getVisibleRect();
        }

    }

}
