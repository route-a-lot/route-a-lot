package kit.route.a.lot.map.infosupply;

import static org.junit.Assert.assertEquals;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import kit.route.a.lot.common.Bounds;
import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.OSMType;
import kit.route.a.lot.common.POIDescription;
import kit.route.a.lot.common.WayInfo;
import kit.route.a.lot.controller.State;
import kit.route.a.lot.map.Area;
import kit.route.a.lot.map.Node;
import kit.route.a.lot.map.POINode;
import kit.route.a.lot.map.Street;
import kit.route.a.lot.map.rendering.MapElementGenerator;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class FileQTTest {

    QTGeographicalOperator operator;
    
    @BeforeClass
    public static void setUpClass() {
        PropertyConfigurator.configure("config/log4j.conf");
    }

    Bounds bounds = new Bounds(0, 90, 0, 90);
    
    @Before
    public void setUp() {
        MapInfo mapInfo = new MapInfo();
        mapInfo.elementDB = new ArrayElementDB();
        State.getInstance().setMapInfo(mapInfo);
        MapElementGenerator gen = new MapElementGenerator();
        
        for(int i = 0; i < 1000; i++) {
            Area area = new Area(null, new WayInfo());
            Node[] areaNode = new Node[4];
            
            areaNode[0] = gen.generateNodeInBounds(bounds);
            areaNode[1] = new Node(areaNode[0].getPos().add(0, 4));
            areaNode[2] = new Node(areaNode[0].getPos().add(4, 4));
            areaNode[3] = new Node(areaNode[0].getPos().add(4, 0));
            area.setNodes(areaNode);
            mapInfo.elementDB.addMapElement(area);
            String s = "" + i;
            POINode poi = new POINode(gen.generateNodeInBounds(bounds).getPos(),
                                        new POIDescription(s, OSMType.AMENITY_COLLEGE, s));           
            mapInfo.elementDB.addMapElement(poi);
        }
        
        Node[] node1 = {new Node(new Coordinates(0.0f, 0.0f)),
                        new Node(new Coordinates(0.0f, 1.0f)),
                        new Node(new Coordinates(0.0f, 2.0f)),
                        new Node(new Coordinates(0.0f, 3.0f))};
        
        Node[] node2 = {new Node(new Coordinates(40.0f, 0.0f)),
                        new Node(new Coordinates(40.0f, 1.0f)),
                        new Node(new Coordinates(40.0f, 2.0f)),
                        new Node(new Coordinates(40.0f, 3.0f))};
        
        for (int i = 0; i < 4; i++) {
            mapInfo.elementDB.addNode(2*i, node1[i]);
            mapInfo.elementDB.addNode(2*i+1, node2[i]);
        }
        
        WayInfo wayinfo1 = new WayInfo();
        WayInfo wayinfo2 = new WayInfo();
        wayinfo1.setBicycle(WayInfo.BICYCLE_YES);
        wayinfo2.setBicycle(WayInfo.BICYCLE_YES);
        wayinfo1.setStreet(true);
        wayinfo2.setStreet(true);
        Street street1 = new Street("Parkstraße", wayinfo1);
        Street street2 = new Street("Schloßallee", wayinfo2);
        street1.setNodes(node1);
        street2.setNodes(node2);
        mapInfo.elementDB.addMapElement(street1);
        mapInfo.elementDB.addMapElement(street2);
        
        mapInfo.elementDB.addMapElement(new POINode(new Coordinates(30, 30),
                new POIDescription("poi", OSMType.AMENITY_CAFE, "poi")));
        
        operator = new QTGeographicalOperator();
        operator.setBounds(bounds);
        operator.fill(mapInfo.elementDB);
    }

    @Test
    public void testFillAndSave() throws IOException {
        RandomAccessFile output = new RandomAccessFile(File.createTempFile("xsral", null), "rw");
        operator.saveToOutput(output);
    }
    
    @Test
    public void testGetBounds() {
        Bounds bounds = operator.getBounds();
        assertEquals(new Float(90), new Float(bounds.getRight()));
    }
        
    @Test
    public void testQuery() throws IOException {
        testFillAndSave();
        operator.queryElements(new Bounds(4.5f, 5.8f, 3.3f, 6.7f), 0, false);
        assertEquals(2003, operator.queryElements(operator.getBounds(), 0, true).size());   
        operator.compactify();
        assertEquals(2003, operator.queryElements(operator.getBounds(), 0, false).size());
    }
        
    @Test
    public void testSelectAndDescription() throws IOException {  
        testFillAndSave();
        assertEquals("poi", operator.getPOIDescription(new Coordinates(29.7f, 29.7f), 1, 0).getName());
        assertEquals("Parkstraße", operator.select(new Coordinates(20, 1.6f)).getName());
        assertEquals("Schloßallee", operator.select(new Coordinates(22, 1.7f)).getName());
    }
    

}
