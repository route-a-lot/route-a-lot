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
    static RoutingGraph graph;
    
    @BeforeClass
    public static void initialize() {
        state = State.getInstance();
        loader = new OSMLoader();
        loaderForSimpleGraph = new OSMLoader();
        simpleRoutingState = new RoutingStateMock();
        loaderForSimpleGraph.setState(simpleRoutingState);
        loader.importMap(new File("./test/resources/karlsruhe_small_current.osm"));
        loaderForSimpleGraph.importMap(new File("./test/resources/karlsruhe_small_current.osm"));
        graph = state.getLoadedGraph();
    }
    
    @Test
    public void buildGraphTest() {
        int[] simpleGraphStartEdges = simpleRoutingState.getLoadedGraph().getStartIDArray();
        int[] normalGraphStartEdges = graph.getStartIDArray();
        
        assertArrayEquals(simpleGraphStartEdges, normalGraphStartEdges);
        
        for (int i = - 1; i <  normalGraphStartEdges.length; i++){  //-1 and getstart . . ..length, for error handling test
            assertTrue(graph.getAllNeighbors(i).containsAll(simpleRoutingState.getLoadedGraph().getAllNeighbors(i)));
        }
        
        for (int i = 0; i <  normalGraphStartEdges.length - 1; i++){
            for (Integer inti : graph.getAllNeighbors(i)) {
                assertEquals(graph.getWeight(i, inti), simpleRoutingState.getLoadedGraph().getWeight(i, inti)); 
            }
        }
    }
    
    @Test
    public void getInverted() {
        RoutingGraph reInvertedGraph = graph.getInverted();
        reInvertedGraph = reInvertedGraph.getInverted();
        
        int[] reInvertedGraphStartEdges = reInvertedGraph.getStartIDArray();
        int[] normalGraphStartEdges = graph.getStartIDArray();
        
        assertArrayEquals(normalGraphStartEdges, reInvertedGraphStartEdges);
        
        for (int i = - 1; i <  normalGraphStartEdges.length; i++){  //-1 and getstart . . ..length, for error handling test
            assertTrue(graph.getAllNeighbors(i).containsAll(reInvertedGraph.getAllNeighbors(i)));
        }
    }
    
    //following Tests are for testing erroHandling
    @Test
    public void getRelevantNeighboursTest(){
        int startEdgesNumber = graph.getStartIDArray().length;
        byte[] area = new byte[100];
        for (int i = 0; i < area.length; i++) {
            area[i] = 1;
        }
        graph.getRelevantNeighbors(startEdgesNumber / 2, new byte[0]);
        graph.getRelevantNeighbors(startEdgesNumber / 2, new byte[1]);
        graph.getRelevantNeighbors(startEdgesNumber / 2, new byte[100000]);
        graph.getRelevantNeighbors(startEdgesNumber / 2, area);
        graph.getRelevantNeighbors(startEdgesNumber * 2, new byte[1]);
        graph.getRelevantNeighbors(-5, new byte[1]);
    }
    
    @Test
    public void getWeight() {
        graph.getWeight(-5, -5);
        graph.getWeight(graph.getStartIDArray().length * 2, graph.getStartIDArray().length * 2);
        graph.getWeight(1, 1);
        graph.getWeight(0, 0);
    }
    
    
}
