package kit.ral.map;

import static org.junit.Assert.*;
import kit.ral.common.Bounds;
import kit.ral.common.Coordinates;
import kit.ral.common.description.POIDescription;

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
        assertTrue(node.isInBounds(new Bounds(0, 10, 0, 10)));
        assertTrue(poiNode.isInBounds(new Bounds(0, 10, 0, 10)));
        assertEquals(new POIDescription("homer", 0, "simpson"), poiNode.getInfo());
        assertFalse(node.isInBounds(new Bounds(0, 10, 0, 1)));
        assertFalse(poiNode.isInBounds(new Bounds(0, 1, 0, 9)));
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
    
}
