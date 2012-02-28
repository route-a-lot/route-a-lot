package kit.route.a.lot.map;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.map.MapElement;
import kit.route.a.lot.map.infosupply.*;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import kit.route.a.lot.common.Address;
import kit.route.a.lot.common.POIDescription;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.common.WayInfo;

import static org.junit.Assert.*;

public class MapInfoTest {

    public MapInfo info;
    Coordinates topLeft;
    Coordinates bottomRight;

    @BeforeClass
    public static void setUpClass() {
        PropertyConfigurator.configure("config/log4j.conf");
    }

    @Before
    public void setUp() throws Exception {
        topLeft = new Coordinates(0, 0);
        bottomRight = new Coordinates(90, 180);
        info = new MapInfo(topLeft, bottomRight);
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

        Collection<MapElement> a =
                info.getBaseLayer(0, new Coordinates(5.0f, 1.0f), new Coordinates(1.0f, 5.0f), false);

        assertEquals(2, a.size());
        POINode favorit1 = new POINode(new Coordinates(10.0f, 15.0f), new POIDescription("derp", 0, "home"));
        POINode favorit2 = new POINode(new Coordinates(20.0f, 25.0f), new POIDescription("derpina", 0, "home"));
        info.addFavorite(new Coordinates(10.0f, 15.0f), new POIDescription("derp", 0, "home"));
        info.addFavorite(new Coordinates(20.0f, 25.0f), new POIDescription("derpina", 0, "home"));
        info.addFavorite(new Coordinates(0.0f, 0.0f), new POIDescription("foo", 0, "deletable"));
        info.deleteFavorite(new Coordinates(3.0f, 3.0f), 0, 1);
        assertTrue(2 == info.getOverlay(0, new Coordinates(0.0f, 0.0f), new Coordinates(25.0f, 30.0f), true).size());
        info.compactifyDatastructures();
        assertTrue(2 == info.getOverlay(0, new Coordinates(0.0f, 0.0f), new Coordinates(25.0f, 30.0f), true).size());
        assertTrue(info.getFavoriteDescription(new Coordinates(10.3f, 15.3f), 0, 1).getName().equals("derp"));
        info.printQuadTree();
        info.addPOI(new Coordinates(7.0f, 6.7f), 12, new POIDescription("nirvana", 0, "ja das gibt es wirklich"), new Address());
        info.printQuadTree();
        assertTrue(info.getPOIDescription(new Coordinates(7.0f, 6.7f), 1.0f, 0).getName().equals("nirvana"));
        assertTrue(info.select(new Coordinates(2.1f, 2.1f)).getName().equals("Hubert Straße"));
        assertTrue(info.getMapElement(1).getName().equals("Fußballplatz"));
        assertTrue(info.getNodePosition(1).getLatitude() == 10.0f);
        info.swapNodeIds(0, 1);
        assertTrue(info.getNodePosition(1).getLatitude() == 2.0f);
        assertTrue(info.getBaseLayerForPositionAndRadius(new Coordinates(1.0f, 1.0f), 0.5f, true).size() == 2);
        info.setGeoTopLeft(new Coordinates(0.1f,0.0f));
        info.setGeoBottomRight(new Coordinates(50.0f, 75.0f));
        assertTrue(info.getGeoBottomRight().getLongitude() == 75.0f);
        assertTrue(info.getGeoTopLeft().getLatitude() == 0.1f);
        info.setBounds(new Coordinates(0.0f, 1.0f), new Coordinates(12.3f, 45.6f));
        Coordinates coord1 = new Coordinates(0.0f, 1.0f);
        Coordinates coord2 = new Coordinates(12.3f, 45.6f);
        info.getBounds(coord1, coord2);
        assertTrue(coord1.getLongitude() == 1.0f);
    }
}
