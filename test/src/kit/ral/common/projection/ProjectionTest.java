package kit.ral.common.projection;

import static org.junit.Assert.*;
import kit.ral.common.Coordinates;
import kit.ral.common.projection.MercatorProjection;
import kit.ral.common.projection.Projection;
import kit.ral.common.projection.SimpleProjection;

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
        Coordinates local = projection.getLocalCoordinates(testCoordinates);
        Coordinates geo = projection.getGeoCoordinates(local);
        assertTrue(testCoordinates.equals(geo));
    }
}
