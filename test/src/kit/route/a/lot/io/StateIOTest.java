package kit.route.a.lot.io;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Progress;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.controller.State;
import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class StateIOTest extends StateIO {
    
    OSMLoader loader;
    State state;
    
    @BeforeClass
    public static void setUpClass() {
        PropertyConfigurator.configure("config/log4j.conf");
    }
    
    @Before
    public void setUp() throws Exception {
        state = new State();
        loader = new OSMLoader(state);
        loader.weightCalculator = new WeightCalculatorMock();
    }
    
    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void saveAndLoad() throws Exception {
        File file = new File("mock.state");
        file.deleteOnExit();
        loader.importMap(new File("test/resources/karlsruhe_small.osm"), new Progress());
        State original = state;
        
        // test deterministic importing
        setUp();
        loader.importMap(new File("test/resources/karlsruhe_small.osm"), new Progress());
        assertTrue(original.equals(state));
        
        // Test proper saving/loading
        saveState(file);
        setUp();
        loadState(file);
        assertTrue(original.equals(state));
    }
}
