package kit.route.a.lot.routing;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
    static State state;
    static RoutingGraph graph;
    static final int SIMPLE_ROUTES_NUMBER = 10000;
    static final int SIMPLE_ROUTES_WITH_MORE_S_NUMBER = 5;
    static final int SIMPLE_ROUTES_Till_THIS_NUMBER = 10;
    static final int OPTIMIZED_ROUTES_NUMBER = 10;
    static final int OPTIMIZED_ROUTES_Till_THIS_NUMBER = 10;
    
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
    
    @Test
    public void simpleRoutingTest() throws Exception{
        File testRoutes = new File("SimpleRoutingTestFile.bin");
        if (!testRoutes.exists()) {
            createTestFile(testRoutes);
        }
        
        DataInputStream stream = new DataInputStream(new FileInputStream(testRoutes));
        ArrayList<Selection> selections;
        List<Integer> route;
        stream.readInt();   //necessery in moment, will be used later
        stream.readInt();
        stream.readInt();
        for (int i = 0; i < SIMPLE_ROUTES_NUMBER; i++) {
            selections = new ArrayList<Selection>();
            int size = stream.readInt();
            for (int j = 0; j < size; j++) {
                int start = stream.readInt();
                int target = stream.readInt();
                float ratio = stream.readFloat();
                selections.add(new Selection(start, target, ratio, null));
            }
            State.getInstance().setNavigationNodes(selections);
            route = Router.calculateRoute();
            int length = getRouteLenght(route);
            assertEquals(stream.readInt(), length);
            
        }
        
    }
    
    private void createTestFile(File file) throws Exception {
        if (file == null) {
            return;
        }
        
        DataOutputStream stream = new DataOutputStream(new FileOutputStream(file));
        
        stream.writeInt(SIMPLE_ROUTES_NUMBER);
        stream.writeInt(SIMPLE_ROUTES_WITH_MORE_S_NUMBER);
        stream.writeInt(SIMPLE_ROUTES_Till_THIS_NUMBER);
        
        Selection start;
        Selection target;
        
        ArrayList<Selection> selections = new ArrayList<Selection>();
        
        for (int i = 0; i < SIMPLE_ROUTES_NUMBER; i++) {
            start = SelectMock.getRandomSelection();
            target = SelectMock.getRandomSelection();

            selections = new ArrayList<Selection>();
            
            selections.add(start);
            selections.add(target);
            
            State.getInstance().setNavigationNodes(selections);
            
            List<Integer> route = SimpleRouter.calculateRoute();
            
            int length = getRouteLenght(SimpleRouter.calculateRoute());
            
            stream.writeInt(2);
            stream.writeInt(start.getFrom());
            stream.writeInt(start.getTo());
            stream.writeFloat(start.getRatio());
            
            stream.writeInt(target.getFrom());
            stream.writeInt(target.getTo());
            stream.writeFloat(target.getRatio());
            
            stream.writeInt(length);
        }
        
        
        
        for (int i= 3; i < SIMPLE_ROUTES_Till_THIS_NUMBER; i++) {
            for (int j = 0; j < SIMPLE_ROUTES_WITH_MORE_S_NUMBER; j++) {
                
                stream.writeInt(i);
                selections = new ArrayList<Selection>();
                
                for (int k = 0; k < i; k++) {
                    Selection temp = SelectMock.getRandomSelection();
                    selections.add(temp);
                    stream.writeInt(temp.getFrom());
                    stream.writeInt(temp.getTo());
                    stream.writeFloat(temp.getRatio());
                    
                }
                
                
                State.getInstance().setNavigationNodes(selections);
                
                List<Integer> route = SimpleRouter.calculateRoute();
                
                int length = getRouteLenght(SimpleRouter.calculateRoute());
                
                stream.writeInt(length);
            }
        }
        
    }
    
    private int getRouteLenght(List<Integer> route) {
        int lenght = 0;
        for (int i = 1; i < route.size(); i++) {
            lenght += State.getInstance().getLoadedGraph().getWeight(route.get(i - 1), route.get(i - 1));
        }
        return lenght;
    }
    
}
