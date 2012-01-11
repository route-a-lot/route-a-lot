package kit.route.a.lot.map;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.map.MapElement;
import kit.route.a.lot.map.infosupply.*;
import java.util.ArrayList; 

import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import kit.route.a.lot.common.Address; 
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.common.WayInfo;

import static org.junit.Assert.*;

public class MapInfoTest {

    public MapInfo info;
    Coordinates topLeft;
    Coordinates bottomRight;
    
    @Before
    public void setUp() throws Exception {
        topLeft = new Coordinates();
        topLeft.setLatitude(90);
        topLeft.setLongitude(0);
        bottomRight = new Coordinates();
        bottomRight.setLatitude(0);
        bottomRight.setLongitude(180);
        info = new MapInfo(topLeft, bottomRight);
    }
    
    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void test() {
        info.addNode(new Coordinates(2f, 2f), 0, new Address());
        info.addNode(new Coordinates(10.0f, 10.0f), 1, new Address());
        info.addNode(new Coordinates(4f, 2.5f), 2, new Address());
        info.addNode(new Coordinates(4f, 3f), 3, new Address());
        info.addNode(new Coordinates(2.4f, 1f), 4, new Address());
        info.addNode(new Coordinates(5f, 2f), 5, new Address());
        info.addNode(new Coordinates(0.0f, 0.0f), 6, new Address());
        info.addNode(new Coordinates(10.0f, 11.0f), 7, new Address());
        info.addNode(new Coordinates(11.0f, 10.0f), 8, new Address());
        info.addNode(new Coordinates(11.0f, 11.0f), 9, new Address());
        info.addNode(new Coordinates(10.0f, 12.0f), 10, new Address());
        info.addNode(new Coordinates(12.0f, 20.0f), 11, new Address());
        
        
       
        
        ArrayList<Integer> ids = new ArrayList<Integer>();
        ids.add(0);
        ids.add(1);
        ids.add(2);
        ids.add(3);
        ids.add(4);
        ArrayList<Integer> aids = new ArrayList<Integer>();
        aids.add(6);
        aids.add(7);
        aids.add(8);
        aids.add(9);
        aids.add(10);
        aids.add(11);
        
        WayInfo street = new WayInfo();
        street.setStreet(true);
        info.addWay(ids, "Hubert Straße", street);
        
        info.addWay(aids, "Fußballplatz", new WayInfo());
        
        ArrayList<MapElement> a =
                (ArrayList<MapElement>) info.getBaseLayer(0, new Coordinates(
                        5.0f, 1.0f), new Coordinates(1.0f, 5.0f));
        System.out.println(a.size());
        for (MapElement ele : a) {
            System.out.println(ele.toString());
        }
        Selection sel = info.select(bottomRight);
        if (sel != null) {
            System.out.println(sel.toString());
        } else {
            System.err.println("select funktioniert noch nicht :(");
        }
        
        assertEquals(7, a.size());
    }

}
