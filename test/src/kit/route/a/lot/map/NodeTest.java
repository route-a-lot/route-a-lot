package kit.route.a.lot.map;

import static org.junit.Assert.*;
import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.POIDescription;

import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class NodeTest {
    
    Node node;
    POINode poiNode;
    
    @BeforeClass
    public static void setUpClass() {
        PropertyConfigurator.configure("config/log4j.conf");
    }

    @Before
    public void setUp() throws Exception {
        node = new Node(new Coordinates(5.0f, 5.0f));
        poiNode = new POINode(new Coordinates(3.0f, 7.0f), new POIDescription("homer", 0, "simpson"));
        node.setID(-1);
        poiNode.setID(-2);
    }

    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void testIsInBounce() {
        assertTrue(node.isInBounds(new Coordinates(0.0f,0.0f), new Coordinates(10.0f, 10.0f)));
        assertTrue(poiNode.isInBounds(new Coordinates(0.0f, 0.0f), new Coordinates(10.0f, 10.0f)));
        assertEquals(new POIDescription("homer", 0, "simpson"), poiNode.getInfo());
        assertFalse(node.isInBounds(new Coordinates(0.0f,0.0f), new Coordinates(1.0f, 10.0f)));
        assertFalse(poiNode.isInBounds(new Coordinates(0.0f, 0.0f), new Coordinates(9.0f, 1.0f)));
    }
    
    @Test
    public void testGetReduce() {
        assertEquals(node, node.getReduced(1, 0));
        assertEquals(null, node.getReduced(5, 0));
        assertEquals(poiNode, poiNode.getReduced(0, 0));
        assertEquals(null, poiNode.getReduced(10, 0));
    }
    
    @Test
    public void testEquals() {
        assertTrue(node.equals(node));
        assertTrue(node.equals(new Node(new Coordinates(5.0f, 5.0f))));
        assertFalse(node.equals(new Node(new Coordinates(0.3f, 0.4f))));
        assertFalse(node.equals("Hi"));
        assertTrue(poiNode.equals(poiNode));
        assertTrue(poiNode.equals(new POINode(new Coordinates(3.0f,7.0f), new POIDescription("homer", 0, "simpson"))));
        assertFalse(poiNode.equals(new Node(new Coordinates(10.3f, 10.4f))));
        assertFalse(poiNode.equals("boo"));
    }
    
    @Test
    public void testAssignID() {
        //Falls node.id < 0 und id < 0 wird kein fehler geworfen!
        assertTrue(node.assignID(1));
        assertTrue(poiNode.assignID(2));
        assertEquals(2, poiNode.getID());
        assertEquals(1, node.getID());
    }
}
