package kit.ral.io;

import static org.junit.Assert.assertTrue;

import java.io.File;

import kit.ral.common.Progress;
import kit.ral.controller.State;

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
