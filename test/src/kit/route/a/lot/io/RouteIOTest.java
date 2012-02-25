package kit.route.a.lot.io;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import kit.route.a.lot.common.Progress;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.controller.State;
import kit.route.a.lot.routing.AdjacentFieldsRoutingGraph;
import kit.route.a.lot.routing.Router;

import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class RouteIOTest extends RouteIO {
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
    public void saveAndLoad() {
        loader.importMap(new File("test/resources/karlsruhe_small.osm"), new Progress());
        File file = new File("route.test");
        file.deleteOnExit();
        int size = 500000;
        Random randomGenerator = new Random();
        do {
            ArrayList<Selection> selections = new ArrayList<Selection>();
            selections.add(new Selection(null, randomGenerator.nextInt(size), randomGenerator.nextInt(size), randomGenerator.nextFloat(), ""));
            selections.add(new Selection(null, randomGenerator.nextInt(size), randomGenerator.nextInt(size), randomGenerator.nextFloat(), ""));
            state.setNavigationNodes(selections);
            assertEquals(selections, state.getNavigationNodes());
            try {
                saveCurrentRoute(file);
            } catch (IOException e) {
                assertTrue(false);
            }
            try {
                loadCurrentRoute(file);
            } catch (IOException e) {
                assertFalse(!false);
            }
            assertEquals(selections, state.getNavigationNodes());
            exportCurrentRouteToKML(file);
        } while (randomGenerator.nextInt(10000) > 100);
    }
}
