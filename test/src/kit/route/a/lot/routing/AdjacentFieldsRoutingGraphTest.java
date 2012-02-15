package kit.route.a.lot.routing;

import java.io.File;
import org.junit.BeforeClass;

import kit.route.a.lot.controller.State;
import kit.route.a.lot.io.OSMLoader;

import org.junit.Test;
import static org.junit.Assert.*;


public class AdjacentFieldsRoutingGraphTest {

    static OSMLoader loaderForSimpleGraph;
    static OSMLoader loader;
    static RoutingStateMock simpleRoutingState;
    static State state;
    
    @BeforeClass
    public static void initialize() {
        state = State.getInstance();
        loader = new OSMLoader();
        loaderForSimpleGraph = new OSMLoader();
        simpleRoutingState = new RoutingStateMock();
        loaderForSimpleGraph.setState(simpleRoutingState);
        loader.importMap(new File("./test/resources/karlsruhe_small_current.osm"));
        loaderForSimpleGraph.importMap(new File("./test/resources/karlsruhe_small_current.osm"));
    }
    
    @Test
    public void buildGraphTest() {
        assertArrayEquals(simpleRoutingState.getLoadedGraph().getStartIDArray(), state.getLoadedGraph().getStartIDArray());
        assertArrayEquals(simpleRoutingState.getLoadedGraph().getWeightsArray(), state.getLoadedGraph().getWeightsArray());
        assertArrayEquals(simpleRoutingState.getLoadedGraph().getEdgesArray(), state.getLoadedGraph().getEdgesArray());
    }
    
    
}
