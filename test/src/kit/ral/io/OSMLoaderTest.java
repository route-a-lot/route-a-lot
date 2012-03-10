package kit.ral.io;

import static org.junit.Assert.*;

import java.io.File;

import kit.ral.common.Progress;

import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
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
        state = new StateMock();
        loader = new OSMLoader(state, new WeightCalculatorMock(state));
    }

    @After
    public void tearDown() throws Exception {
    }

    @Ignore
    // another testfile
    public void testImportSimple() {
        loader.importMap(new File("test/resources/simple.osm"), new Progress());

        assertEquals(3, state.getNodeCount());
        assertEquals(2, state.getGraphStartIDsSize());
        assertEquals(2, state.getGraphEndIDsSize());
        assertEquals(2, state.getGraphWeightsSize());
    }

    @Test
    public void testImportKarlsruheSmall() {
        loader.importMap(new File("test/resources/karlsruhe_small.osm"), new Progress());

        assertEquals(11093, state.getNodeCount());
        assertTrue(0 < state.getWayCount());
    }

    @Test
    public void testImportKarlsruheBig() {
        loader.importMap(new File("test/resources/karlsruhe_big.osm"), new Progress());

        assertEquals(36678, state.getNodeCount());
    }

    @Test
    public void testPerformanceImportKarlsruheBig() {
        long start = System.currentTimeMillis();
        loader.importMap(new File("test/resources/karlsruhe_big.osm"), new Progress());
        long duration = System.currentTimeMillis() - start;

        // System.out.println("Duration: " + duration + " ms");

        assertTrue(duration < 3000);
    }

    @Ignore
    // test file will not be committed to the repository because it is too big ~19G
    public void testImportGermany() {
        loader.importMap(new File("test/resources/germany.osm"), new Progress());

        assertEquals(89049038, state.getNodeCount());
    }

    @Ignore
    // test file will not be committed to the repository because it is too big ~2.8G
    public void testImportBadenWuerttemberg() {
        loader.importMap(new File("test/resources/baden-wuerttemberg.osm"), new Progress());

        assertEquals(13410554, state.getNodeCount());
    }
    
    @Test
    public void testMinimal() {
        loader.importMap(new File("test/resources/testmaps/minimal.osm"), new Progress());
        assertEquals(0, state.getNodeCount());
        assertEquals(0, state.getWayCount());
    }
    
    @Test
    public void testGibberish() {
        loader.importMap(new File("test/resources/testmaps/gibberish.osm"), new Progress());
        assertEquals(0, state.getNodeCount());
        assertEquals(0, state.getWayCount());
    }
    
    @Test
    public void testMissingNode() {
        loader.importMap(new File("test/resources/testmaps/missing-node.osm"), new Progress());
        assertEquals(1, state.getNodeCount());
        assertEquals(0, state.getWayCount());
        
    }
    
    @Test
    public void testNonContinuous() {
        loader.importMap(new File("test/resources/testmaps/non-continuous.osm"), new Progress());
        assertEquals(2, state.getNodeCount());
        assertEquals(0, state.getWayCount());
    }
    
    @Test
    public void testNot() {
        loader.importMap(new File("test/resources/testmaps/not.osm"), new Progress());
        assertEquals(0, state.getNodeCount());
        assertEquals(0, state.getWayCount());
    }
    
    @Test
    public void testNoNodes() {
        loader.importMap(new File("test/resources/testmaps/no-nodes.osm"), new Progress());
        assertEquals(0, state.getNodeCount());
        assertEquals(0, state.getWayCount());
    }

}
