package kit.route.a.lot.map.infosupply;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.POIDescription;
import kit.route.a.lot.common.WayInfo;
import kit.route.a.lot.map.Area;
import kit.route.a.lot.map.Node;
import kit.route.a.lot.map.POINode;
import kit.route.a.lot.map.Street;

import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class QTTest {

    QTGeographicalOperator operator;
    
    @BeforeClass
    public static void setUpClass() {
        PropertyConfigurator.configure("config/log4j.conf");
    }

    @Before
    public void setUp() throws Exception {
        operator = new QTGeographicalOperator();
        operator.setBounds(new Coordinates(0.0f, 0.0f), new Coordinates(50.0f, 50.0f));
        for(int i = 0; i < 10; i++) {
            Area area = new Area(null, new WayInfo());
            Node[] areaNode = new Node[4];
            areaNode[0] = new Node(new Coordinates(1.0f + 4*i + 1, 1.0f + 4*i + 1));
            areaNode[1] = new Node(new Coordinates(4.0f + 4*i + 1, 1.0f + 4*i + 1));
            areaNode[2] = new Node(new Coordinates(1.0f + 4*i + 1, 4.0f + 4*i + 1));
            areaNode[3] = new Node(new Coordinates(4.0f + 4*i + 1, 4.0f + 4*i + 1));
            area.setNodes(areaNode);
            String s = "" + i;
            POINode favorite = new POINode(new Coordinates(0.0f+i, 0.0f+i), new POIDescription(s, 0, s));
            operator.addToBaseLayer(area);
            operator.addToOverlay(favorite);
        }
        WayInfo wayinfo1 = new WayInfo();
        WayInfo wayinfo2 = new WayInfo();
        wayinfo1.setBicycle(WayInfo.BICYCLE_YES);
        wayinfo2.setBicycle(WayInfo.BICYCLE_YES);
        wayinfo1.setStreet(true);
        wayinfo2.setStreet(true);
        Street street1 = new Street("Parkstraße", wayinfo1);
        Street street2 = new Street("Schloßallee", wayinfo2);
        Node[] node1 = new Node[4];
        Node[] node2 = new Node[4];
        node1[0] = new Node(new Coordinates(0.0f, 0.0f));
        node1[1] = new Node(new Coordinates(0.0f, 1.0f));
        node1[2] = new Node(new Coordinates(0.0f, 2.0f));
        node1[3] = new Node(new Coordinates(0.0f, 3.0f));
        node2[0] = new Node(new Coordinates(40.0f, 0.0f));
        node2[1] = new Node(new Coordinates(40.0f, 1.0f));
        node2[2] = new Node(new Coordinates(40.0f, 2.0f));
        node2[2].setID(22);
        node2[3] = new Node(new Coordinates(40.0f, 3.0f));
        node2[3].setID(23);
        street1.setNodes(node1);
        street2.setNodes(node2);
        operator.addToBaseLayer(street1);
        operator.addToBaseLayer(street2);
    }

    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void testGetLayers() {
        assertEquals(3, operator.getBaseLayer(0, new Coordinates(3.0f, 3.0f), new Coordinates(8.0f, 8.0f), true).size());
        assertEquals(1, operator.getOverlay(0, new Coordinates(3.3f, 4.5f), new Coordinates(6.7f, 5.8f), true).size());
        operator.compactifyDatastructures();
        assertEquals(7, operator.getBaseLayer(new Coordinates(5.5f, 5.5f), 2, false).size());
        assertEquals(1, operator.getOverlay(0, new Coordinates(3.3f, 4.5f), new Coordinates(6.7f, 5.8f), true).size());
        String s = "" + 2;
        assertEquals(s, operator.getPOIDescription(new Coordinates(2.0f, 2.0f), 0.3f, 0).getName());
        assertTrue(22 == operator.select(new Coordinates(25.5f, 2.9f)).getFrom());
    }
    
    @Test
    public void testGetBounds() {
        Coordinates coord1 = new Coordinates();
        Coordinates coord2 = new Coordinates();
        operator.getBounds(coord1, coord2);
        assertTrue(50.0f == coord2.getLongitude());
    }
}
