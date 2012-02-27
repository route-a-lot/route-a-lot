package kit.route.a.lot.map.infosupply;

import static org.junit.Assert.*;
import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.POIDescription;
import kit.route.a.lot.controller.State;
import kit.route.a.lot.map.MapElement;
import kit.route.a.lot.map.Node;
import kit.route.a.lot.map.POINode;
import kit.route.a.lot.map.rendering.MapElementGenerator;

import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;


public class TestElementDB {
    
    ElementDB elements;
    
    @BeforeClass
    public static void setUpClass() {
        State.getInstance().resetMap();
        PropertyConfigurator.configure("config/log4j.conf");
    }

    @Before
    public void setUp() throws Exception {
        elements = new ArrayElementDB();
        for(int i = 0; i < 10; i++) {
            Node node = new Node(new Coordinates(0.0f + i, 0.0f +i));
            MapElement element1 = new MapElementGenerator().generateStreet();
            MapElement element2 = new MapElementGenerator().generateArea();
            String s = "" + i;
            POINode favorite = new POINode(new Coordinates(0.0f + i, 0.0f + i), new POIDescription(s, 0 ,s));
            elements.addFavorite(favorite);
            elements.addMapElement(element1);
            elements.addMapElement(element2);
            elements.addNode(i, node);
        }
    }

    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void testAdGet() {
        assertTrue(elements.getNode(1).getPos().getLatitude() == 1.0f && elements.getNode(1).getPos().getLongitude() == 1.0f);
        assertEquals(1,elements.getFavorites().get(1).getID());
        assertEquals(6 ,elements.getMapElement(6).getID());
        assertEquals(5 ,elements.getMapElement(5).getID());
        elements.deleteFavorite(new Coordinates(3.0f, 3.0f), 0, 0);
        assertEquals(5, elements.getFavorites().get(5).getID());
    }
    
    @Test
    public void testSwapFavDescription() {
        elements.swapNodeIDs(5, 3);
        assertTrue(5.0f == elements.getNode(3).getPos().getLatitude());
        String s = "" + 3;
        assertTrue(s.equals(elements.getFavoriteDescription(new Coordinates(3.2f, 3.2f), 0, 1).getDescription()));
    }
    
}
