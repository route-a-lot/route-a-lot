package kit.route.a.lot.map.rendering;

import java.awt.Component;
import java.awt.Point;

import javax.swing.JFrame;

import kit.route.a.lot.common.Context;
import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.map.Edge;
import kit.route.a.lot.map.Node;
import kit.route.a.lot.map.Street;

import org.junit.Test;


public class RendererTest {

    public static void main(String[] args) {
        new RendererTest().test();
    }
    
    public void test() {
        Renderer renderer = new Renderer();
        Coordinates topLeft = new Coordinates(12., 12.);
        Coordinates bottomRight = new Coordinates(64., 64.);

        StateMock state = new StateMock();
        MapInfoMock mapInfo = (MapInfoMock) state.getLoadedMapInfo();

        renderer.state = state;

        mapInfo.addMapElement(new Node(0, new Coordinates(40., 40.)));
        mapInfo.addMapElement(new Node(0, new Coordinates(30., 40.)));
        mapInfo.addMapElement(new Edge(new Node(0, new Coordinates(20., 20.)), new Node(0, new Coordinates(20., 30.)), new Street(0, null, null)));

        Context context = new Context(100, 100, topLeft, bottomRight);

        TestGUI gui = new TestGUI(context);
        gui.setVisible(true);
        
        renderer.render(context, 0);
    }
    
    class TestGUI extends JFrame {
        
        Component compContext;
        
        public TestGUI(Context context) {
            super("Test");
            compContext = this.add(context);
            this.setSize(200, 200);
            this.setLocation(new Point(500, 500));
            repaint();
        }
        
    }

}
