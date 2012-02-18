package kit.route.a.lot.routing;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;

import kit.route.a.lot.common.Selection;
import kit.route.a.lot.controller.State;
import kit.route.a.lot.io.OSMLoader;

import org.junit.Test;
import static org.junit.Assert.*;


public class RoutingAndGraphTest {

    static OSMLoader loaderForSimpleGraph;
    static OSMLoader loader;
    static RoutingStateMock simpleRoutingState;
    static RoutingGraph graph;
    static final int SIMPLE_ROUTES_NUMBER = 10000;
    static final int PER_ADVANCED = 5;  //nuber of tests for each optimized number
    static final int ADVANCED_ROUTES_TILL = 10;  //targets without optimizing
    static final int OPTIMIZED_ROUTES_NUMBER = 10;  
    static final int OPTIMIZED_ROUTES_Till_THIS_NUMBER = 10;
    
    
    @BeforeClass
    public static void initialize() {
        loader = new OSMLoader();
        loaderForSimpleGraph = new OSMLoader();
        simpleRoutingState = new RoutingStateMock();
        loaderForSimpleGraph.setState(simpleRoutingState);
        loader.importMap(new File("./test/resources/karlsruhe_small_current.osm"));
        loaderForSimpleGraph.importMap(new File("./test/resources/karlsruhe_small_current.osm"));
        graph = State.getInstance().getLoadedGraph();
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
    
    @Test
    public void simpleRoutingTest() throws Exception{
        File testRoutes = new File("SimpleRoutingTestFile.bin");
        if (!testRoutes.exists()) {
            createTestFile(testRoutes);
        }
        int failure = 0;
        DataInputStream stream = new DataInputStream(new FileInputStream(testRoutes));
        ArrayList<Selection> selections;
        List<Integer> route;
        stream.readInt();   //necessary in moment, will be used later
        stream.readInt();
        stream.readInt();
        for (int i = 0; i < SIMPLE_ROUTES_NUMBER + (ADVANCED_ROUTES_TILL - 2) * PER_ADVANCED; i++) {
            selections = new ArrayList<Selection>();
            int size = stream.readInt();
            for (int j = 0; j < size; j++) {
                int start = stream.readInt();
                int target = stream.readInt();
                float ratio = stream.readFloat();
                selections.add(new Selection(start, target, ratio, null));
            }
            route = Router.calculateRoute(selections);
            int length = getRouteLength(route);
            assertEquals(stream.readInt(), length);
            
        }
    }
    
   
    
    private void createTestFile(File file) throws Exception {
        if (file == null) {
            return;
        }
        
        DataOutputStream stream = new DataOutputStream(new FileOutputStream(file));
        
        stream.writeInt(SIMPLE_ROUTES_NUMBER);
        stream.writeInt(ADVANCED_ROUTES_TILL);
        stream.writeInt(PER_ADVANCED);
        
        Selection start;
        Selection target;
        
        ArrayList<Selection> selections = new ArrayList<Selection>();
        
        for (int i = 0; i < SIMPLE_ROUTES_NUMBER; i++) {
            start = SelectMock.getRandomSelection();
            target = SelectMock.getRandomSelection();

            selections = new ArrayList<Selection>();
            
            selections.add(start);
            selections.add(target);
            
            List<Integer> route = SimpleRouter.calculateRoute(selections);
            
            int length = getRouteLength(route);
            
            stream.writeInt(2);
            stream.writeInt(start.getFrom());
            stream.writeInt(start.getTo());
            stream.writeFloat(start.getRatio());
            
            stream.writeInt(target.getFrom());
            stream.writeInt(target.getTo());
            stream.writeFloat(target.getRatio());
            
            stream.writeInt(length);
        }
        
        
        
        for (int i= 2; i <= ADVANCED_ROUTES_TILL; i++) {
            for (int j = 0; j < PER_ADVANCED; j++) {
                
                stream.writeInt(i);
                selections = new ArrayList<Selection>();
                
                for (int k = 0; k < i; k++) {
                    Selection temp = SelectMock.getRandomSelection();
                    selections.add(temp);
                    stream.writeInt(temp.getFrom());
                    stream.writeInt(temp.getTo());
                    stream.writeFloat(temp.getRatio());
                    
                }
                
                List<Integer> route = SimpleRouter.calculateRoute(selections);
                
                int length = getRouteLength(route);
                
                stream.writeInt(length);
            }
        }
        
    }
    
    private int getRouteLength(List<Integer> route) {
        int length = 0;
        for (int i = 1; i < route.size(); i++) {
            length += graph.getWeight(route.get(i - 1), route.get(i));
        }
        return length;
    }
    
}
