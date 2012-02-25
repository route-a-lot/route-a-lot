package kit.route.a.lot.map;

import static org.junit.Assert.*;
import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.POIDescription;
import kit.route.a.lot.common.WayInfo;

import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class StreetAreaTest {
    
    Area area;
    Street street;
    Street street2;
    
    @BeforeClass
    public static void setUpClass() {
        PropertyConfigurator.configure("config/log4j.conf");
    }

    @Before
    public void setUp() throws Exception {
        area = new Area("See", new WayInfo());
        Node[] areaNode = new Node[4];
        areaNode[0] = new Node(new Coordinates(1.0f, 1.0f));
        areaNode[1] = new Node(new Coordinates(5.0f, 1.0f));
        areaNode[2] = new Node(new Coordinates(5.0f, 5.0f));
        areaNode[3] = new Node(new Coordinates(1.0f, 5.0f));
        area.setNodes(areaNode);
        street = new Street("Parkstraße", new WayInfo());
        Node[] streetNode = new Node[4];
        streetNode[0] = new Node(new Coordinates(1.0f,1.0f));
        streetNode[1] = new Node(new Coordinates(2.0f,2.0f));
        streetNode[2] = new Node(new Coordinates(3.0f,3.0f));
        streetNode[3] = new Node(new Coordinates(4.0f,4.0f));
        street.setNodes(streetNode);
        street2 = new Street("Rundstraße", new WayInfo());
        Node[] street2Node = new Node[4];
        street2Node[0] = new Node(new Coordinates(1.0f,1.0f));
        street2Node[1] = new Node(new Coordinates(1.0f,4.0f));
        street2Node[2] = new Node(new Coordinates(4.0f,1.0f));
        street2Node[3] = new Node(new Coordinates(4.0f,4.0f));
        street2.setNodes(streetNode);
    }

    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void testIsInBounce() {
        assertTrue(street.isEdgeInBounds(street.getNodes()[0].getPos(), street.getNodes()[2].getPos(), new Coordinates(0.0f, 0.0f), new Coordinates(6.0f, 6.0f)));
        assertTrue(street.isInBounds(new Coordinates(0.0f, 0.0f), new Coordinates(6.0f, 6.0f)));
        assertTrue(street.isInBounds(new Coordinates(3.5f, 3.5f), new Coordinates(4.5f, 4.5f)));
        assertTrue(street.isInBounds(new Coordinates(3.5f, 3.5f), new Coordinates(3.7f, 3.7f)));
        assertTrue(street.isInBounds(new Coordinates(3.5f, 3.5f), new Coordinates(4.5f, 4.5f)));
        assertTrue(street2.isInBounds(new Coordinates(2.0f, 2.0f), new Coordinates(3.0f, 3.0f)));
        assertTrue(area.isInBounds(new Coordinates(1.0f, 1.0f), new Coordinates(6.0f, 6.0f)));
        assertTrue(area.isInBounds(new Coordinates(4.0f, 4.0f), new Coordinates(6.0f, 6.0f)));
        assertTrue(area.isInBounds(new Coordinates(5.0f, 3.0f), new Coordinates(5.0f, 4.0f)));
        assertTrue(area.isInBounds(new Coordinates(2.0f, 2.0f), new Coordinates(3.0f, 3.0f)));
    }
    
    @Test
    public void testEqual() {
        assertTrue(street.equals(new Street("Parkstraße", new WayInfo())));
        assertTrue(area.equals(new Area("See", new WayInfo())));
        assertTrue(0>area.compare(area, new Area("Wiese", new WayInfo())));
    }
}
