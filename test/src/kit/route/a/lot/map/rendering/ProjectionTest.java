package kit.route.a.lot.map.rendering;

import static org.junit.Assert.*;
import kit.route.a.lot.common.Coordinates;

import org.junit.Test;


public class ProjectionTest {

    @Test
    public void testMercatorProjection() {
        testProjection(new MercatorProjection(new Coordinates(49.23f, 8.2f), 2.9E-5f));
    }
    
    @Test
    public void testSimpleProjection() {
        testProjection(new SimpleProjection(new Coordinates(49.23f, 8.2f), new Coordinates(49.1f, 8.4f),
                200, 200));
    }
    
    private void testProjection(Projection projection) {
        Coordinates testCoordinates = new Coordinates(49.2f, 8.3f);
        Coordinates local = projection.geoCoordinatesToLocalCoordinates(testCoordinates);
        Coordinates geo = projection.localCoordinatesToGeoCoordinates(local);
        assertEquals(testCoordinates, geo);
    }
}
