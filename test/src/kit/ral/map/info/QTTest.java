package kit.ral.map.info;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import kit.ral.common.Bounds;
import kit.ral.common.Coordinates;
import kit.ral.common.description.POIDescription;
import kit.ral.common.description.WayInfo;
import kit.ral.map.Area;
import kit.ral.map.Node;
import kit.ral.map.POINode;
import kit.ral.map.Street;
import kit.ral.map.info.ArrayElementDB;
import kit.ral.map.info.geo.QTGeographicalOperator;

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
        ArrayElementDB elementDB = new ArrayElementDB();
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
            elementDB.addMapElement(area);
            elementDB.addMapElement(favorite);
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
        elementDB.addMapElement(street1);
        elementDB.addMapElement(street2);
        
        operator = new QTGeographicalOperator();
        operator.setBounds(new Bounds(0, 50, 0, 50));
        operator.fill(elementDB);
    }

    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void testGetLayers() {
        assertEquals(4, operator.queryElements(new Bounds(3, 8, 3, 8), 0, true).size());
        assertEquals(1, operator.queryElements(new Bounds(4.5f, 5.8f, 3.3f, 6.7f), 0, true).size());
        operator.compactify();
        assertEquals(7, operator.queryElements(new Bounds(3.5f, 7.5f, 3.5f, 7.5f), 0, false).size());
        assertEquals(1, operator.queryElements(new Bounds(4.5f, 5.8f, 3.3f, 6.7f), 0, true).size());
        String s = "" + 2;
        assertEquals(s, operator.getPOIDescription(new Coordinates(2.0f, 2.0f), 0.3f, 0).getName());
        assertTrue(22 == operator.select(new Coordinates(25.5f, 2.9f)).getFrom());
    }
    
    @Test
    public void testGetBounds() {
        Bounds bounds = operator.getBounds();
        assertTrue(50.0f == bounds.getRight());
    }
}