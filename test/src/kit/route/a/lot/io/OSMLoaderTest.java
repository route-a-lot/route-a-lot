package kit.route.a.lot.io;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class OSMLoaderTest {
    
    OSMLoader loader;
    StateMock state;

    @Before
    public void setUp() throws Exception {
        loader = new OSMLoader();
        state = new StateMock();
        loader.state = state;
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

}
