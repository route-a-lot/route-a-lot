package kit.route.a.lot.io;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import kit.route.a.lot.common.Address;
import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.WayInfo;
import kit.route.a.lot.map.infosupply.MapInfo;

public class PerformanceTest {
    
    MapInfo mapInfo;
    
    @BeforeClass
    public static void setUpClass() {
        PropertyConfigurator.configure("config/log4j.conf");
    }
    
    @Before
    public void setUp() {
        mapInfo = new MapInfo();
    }
    
    //@Test
    public void testMemoryPerformanceWithNodes() {
        mapInfo.setBounds(new Coordinates(100, 10), new Coordinates(10, 100));
        Random rnd = new Random();
        for (int i = 0; i < 33000000; i++) {
            mapInfo.addNode(new Coordinates(10 + rnd.nextFloat()*90, 10 + rnd.nextFloat()*90), i, new Address());
            if (i > 297800 && i % 10 == 0) { 
                System.gc();
                // System.out.println("current node = " + i);
            }
            if (i % 1000 == 0) {
                // System.out.println("current node = " + i);
            }
        }
        // System.out.println("Fertig");
    }
    
//    @Test
    public void testMemoryPerformanceWithWays() {
        mapInfo.setBounds(new Coordinates(100, 10), new Coordinates(10, 100));
        Random rnd = new Random();

        List<Integer> ids;
        WayInfo wayInfo;
        int type = 0;
        int nodeId = 0;

        for (int i = 0; i < 33000000; i++) {
            int size = 2 + rnd.nextInt(5);
            ids = new ArrayList<Integer>(size);
            float startLon = 10 + rnd.nextFloat()*80;
            float startLat = 10 + rnd.nextFloat()*80;
            for (int j = 0; j < size; j++) {
                mapInfo.addNode(new Coordinates(startLon, startLat), nodeId, null);
                startLon += rnd.nextFloat()*2;
                startLat += rnd.nextFloat()*2;
                ids.add(nodeId);
                nodeId++;
            }

            wayInfo = new WayInfo();
            switch (type) {
                case 0:
                    wayInfo.setStreet(true);
                    type = 1;
                    break;
                case 1:
                    wayInfo.setArea(true);
                    type = 0;
                    break;
            }
            mapInfo.addWay(ids, "", wayInfo);

            if (i > 33000 && i % 10 == 0) { 
                System.gc();
                // System.out.println("current way = " + i);
            }
            if (i % 1000 == 0) {
                // System.out.println("current way = " + i);
            }
        }
        // System.out.println("Fertig");
    }
    
    //@Test
    public void testStackOverflow() {
        mapInfo.setBounds(new Coordinates(100, 10), new Coordinates(10, 100));
        try {
            for(int i = 0; i < 65; i++) {
                mapInfo.addNode(new Coordinates(40, 40), i, null);
            }
        } catch (Error e) {
            mapInfo.printQuadTree();
        }
    }
}