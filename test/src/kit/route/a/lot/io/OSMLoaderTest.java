package kit.route.a.lot.io;

import static org.junit.Assert.*;

import java.io.File;

import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class OSMLoaderTest {

    OSMLoader loader;
    StateMock state;

    @BeforeClass
    public static void setUpClass() {
        PropertyConfigurator.configure("config/log4j.conf");
    }

    @Before
    public void setUp() throws Exception {
        loader = new OSMLoader();
        state = new StateMock();
        loader.state = state;
        loader.weightCalculator = new WeightCalculatorMock();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testImportSimple() {
        loader.importMap(new File("test/resources/simple.osm"));

        assertEquals(3, state.getNodeCount());
        assertEquals(2, state.getGraphStartIDsSize());
        assertEquals(2, state.getGraphEndIDsSize());
        assertEquals(2, state.getGraphWeightsSize());
    }

    @Test
    public void testImportKarlsruheSmall() {
        loader.importMap(new File("test/resources/karlsruhe_small.osm"));

        assertEquals(11093, state.getNodeCount());
    }

    @Test
    public void testImportKarlsruheBig() {
        loader.importMap(new File("test/resources/karlsruhe_big.osm"));

        assertEquals(36678, state.getNodeCount());
    }

    @Test
    public void testPerformanceImportKarlsruheBig() {
        long start = System.currentTimeMillis();
        loader.importMap(new File("test/resources/karlsruhe_big.osm"));
        long duration = System.currentTimeMillis() - start;

        // System.out.println("Duration: " + duration + " ms");

        assertTrue(duration < 3000);
    }

    @Test
    // test file will not be committed to the repository because it is too big ~19G
    public void testImportGermany() {
        loader.importMap(new File("test/resources/germany.osm"));

        assertEquals(89049038, state.getNodeCount());
    }

    @Test
    // test file will not be committed to the repository because it is too big ~2.8G
    public void testImportBadenWuerttemberg() {
        loader.importMap(new File("test/resources/baden-wuerttemberg.osm"));

        assertEquals(13410554, state.getNodeCount());
    }

}
