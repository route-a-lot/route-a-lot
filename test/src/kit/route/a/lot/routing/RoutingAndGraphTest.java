package kit.route.a.lot.routing;

import java.io.DataInputStream;import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;
import org.junit.Ignore;

import kit.route.a.lot.common.Progress;
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
    static final int PER_ADVANCED = 5; // number of tests for each optimized number
    static final int ADVANCED_ROUTES_TILL = 10; // targets without optimizing

    static final int TARGETS_OPT = 1000; // test per target


    @BeforeClass
    public static void initialize() {
        System.out.println("starte import");
        State.getInstance().resetMap();
        PropertyConfigurator.configure("config/log4j.conf");
        loader = new OSMLoader(State.getInstance());
        simpleRoutingState = new RoutingStateMock();
        loaderForSimpleGraph = new OSMLoader(simpleRoutingState);
        loader.importMap(new File("./test/resources/karlsruhe_small_current.osm"), new Progress());
        loaderForSimpleGraph.importMap(new File("./test/resources/karlsruhe_small_current.osm"), new Progress());
        Precalculator.precalculate(new Progress());
        graph = State.getInstance().getLoadedGraph();
        System.out.println("import fertig");
    }

    @Test
    public void buildGraphTest() { // tests the graph datastructures
        int[] simpleGraphStartEdges = simpleRoutingState.getLoadedGraph().getStartIDArray();
        int[] normalGraphStartEdges = graph.getStartIDArray();

        assertArrayEquals(simpleGraphStartEdges, normalGraphStartEdges);

        for (int i = -1; i < normalGraphStartEdges.length; i++) { // -1 and getstart . . ..length, for error
                                                                  // handling test
            assertTrue(graph.getAllNeighbors(i).containsAll(
                    simpleRoutingState.getLoadedGraph().getAllNeighbors(i)));
        }

        for (int i = 0; i < normalGraphStartEdges.length - 1; i++) {
            for (Integer inti : graph.getAllNeighbors(i)) {
                assertEquals(graph.getWeight(i, inti), simpleRoutingState.getLoadedGraph().getWeight(i, inti));
            }
        }
    }

    @Test
    public void getInvertedTest() { // graphTest
        RoutingGraph reInvertedGraph = graph.getInverted();
        reInvertedGraph = reInvertedGraph.getInverted();

        int[] reInvertedGraphStartEdges = reInvertedGraph.getStartIDArray();
        int[] normalGraphStartEdges = graph.getStartIDArray();

        assertArrayEquals(normalGraphStartEdges, reInvertedGraphStartEdges);

        for (int i = -1; i < normalGraphStartEdges.length; i++) { // -1 and getstart . . ..length, for error
                                                                  // handling test
            assertTrue(graph.getAllNeighbors(i).containsAll(reInvertedGraph.getAllNeighbors(i)));
        }
    }


    @Test
    // graph error handling test
    public void getRelevantNeighboursTest() {
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
    public void getWeightTest() { // graph error handling test
        graph.getWeight(-5, -5);
        graph.getWeight(graph.getStartIDArray().length * 2, graph.getStartIDArray().length * 2);
        graph.getWeight(1, 1);
        graph.getWeight(0, 0);
    }

    @Test
    public void simpleRoutingTest() throws Exception { // simple routing test
        File testRoutes = new File("SimpleRoutingTestFile.bin");
        if (!testRoutes.exists()) {
            createSimpleRouteTestFile(testRoutes);
        }

        DataInputStream stream = new DataInputStream(new FileInputStream(testRoutes));

        if (stream.readInt() != SIMPLE_ROUTES_NUMBER || stream.readInt() != ADVANCED_ROUTES_TILL // test
                                                                                                 // conditions
                                                                                                 // changed
                || stream.readInt() != PER_ADVANCED) {
            stream.close();
            testRoutes.delete();
            createSimpleRouteTestFile(testRoutes);
            stream = new DataInputStream(new FileInputStream(testRoutes));
            stream.readInt();
            stream.readInt();
            stream.readInt();
        }

        ArrayList<Selection> selections;
        List<Integer> route;
        for (int i = 0; i < SIMPLE_ROUTES_NUMBER + (ADVANCED_ROUTES_TILL - 2) * PER_ADVANCED; i++) {
            selections = new ArrayList<Selection>();
            int size = stream.readInt();
            for (int j = 0; j < size; j++) {
                int start = stream.readInt();
                int target = stream.readInt();
                float ratio = stream.readFloat();
                selections.add(new Selection(null, start, target, ratio, ""));
            }
            route = Router.calculateRoute(selections);
            int length = getRouteLength(route, selections);

            if (stream.readInt() != length) {
                System.out.println("wrong route length");
            }

        }
    }

    @Test
    public void optimizedRoutingTest() throws Exception {
        File testRoutes = new File("OptimizedRoutingTestFile.bin");
        if (!testRoutes.exists()) {
            createOptimizedRouteTestFile(testRoutes);
        }

        DataInputStream stream = new DataInputStream(new FileInputStream(testRoutes));

        if (stream.readInt() != TARGETS_OPT) {
            stream.close();
            testRoutes.delete();
            createOptimizedRouteTestFile(testRoutes);
            stream = new DataInputStream(new FileInputStream(testRoutes));
            stream.readInt();
        }

        ArrayList<Selection> selections;
        for (int i = 0; i < TARGETS_OPT; i++) { // test all saved routes
            selections = new ArrayList<Selection>();
            int size = stream.readInt();
            for (int j = 0; j < size; j++) {
                int start = stream.readInt();
                int target = stream.readInt();
                float ratio = stream.readFloat();
                selections.add(new Selection(null, start, target, ratio, ""));
            }
            State.getInstance().setNavigationNodes(selections); // safety
            Router.optimizeRoute(selections, new Progress());
            List<Selection> sol = State.getInstance().getNavigationNodes();
            assertEquals(stream.readInt(), getRouteLength(Router.calculateRoute(sol), sol));
        }
    }


    private void createOptimizedRouteTestFile(File file) throws Exception {
        DataOutputStream stream = new DataOutputStream(new FileOutputStream(file));

        stream.writeInt(TARGETS_OPT);

        ArrayList<Selection> selections = new ArrayList<Selection>();

        for (int i = 0; i < TARGETS_OPT; i++) {
            stream.writeInt(4);
            selections = new ArrayList<Selection>();
            for (int j = 0; j < 4; j++) {
                Selection temp = SelectMock.getRandomSelection();
                selections.add(temp);
                stream.writeInt(temp.getFrom());
                stream.writeInt(temp.getTo());
                stream.writeFloat(temp.getRatio());
            }
            List<Selection> solution = SimpleRouter.optimizeRouteWith4Targets(selections);
            stream.writeInt(getRouteLength(Router.calculateRoute(solution), solution));
        }
    }


    private void createSimpleRouteTestFile(File file) throws Exception {
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

            int length = getRouteLength(route, selections);

            stream.writeInt(2);
            stream.writeInt(start.getFrom());
            stream.writeInt(start.getTo());
            stream.writeFloat(start.getRatio());

            stream.writeInt(target.getFrom());
            stream.writeInt(target.getTo());
            stream.writeFloat(target.getRatio());

            stream.writeInt(length);
        }

        for (int i = 3; i <= ADVANCED_ROUTES_TILL; i++) {
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
                int length = getRouteLength(route, selections);
                stream.writeInt(length);
            }
        }

    }

    private int getRouteLength(List<Integer> route, List<Selection> navNodes) {  
        if (navNodes.size() < 2 || route.size() == 0) {
            return 0;
        }
        int length = 0;
        int navNode = 1;
        if (route.get(0) == navNodes.get(0).getTo()) {
            length += (1 - navNodes.get(0).getRatio()) *
                    State.getInstance().getLoadedGraph().getWeight(navNodes.get(0).getFrom(), navNodes.get(0).getTo());
        } else {
            length += (navNodes.get(0).getRatio()) *
                    State.getInstance().getLoadedGraph().getWeight(navNodes.get(0).getTo(), navNodes.get(0).getFrom());
        }
        if (route.get(route.size() - 1) == navNodes.get(navNodes.size() - 1).getTo()) {
            length += (1 - navNodes.get(navNodes.size() - 1).getRatio()) *
                    State.getInstance().getLoadedGraph().getWeight(navNodes.get(navNodes.size() - 1).getTo(), navNodes.get(navNodes.size() - 1).getFrom());
        } else {
            length += (navNodes.get(navNodes.size() - 1).getRatio()) *
                    State.getInstance().getLoadedGraph().getWeight(navNodes.get(navNodes.size() - 1).getFrom(), navNodes.get(navNodes.size() - 1).getTo());
        }
        
        for (int i = 1; i < route.size() - 1; i++) {
            
            if(route.get(i) == -1) {
                if (route.get(i - 1) == navNodes.get(navNode).getFrom()) {
                    length += (navNodes.get(navNode).getRatio()) *
                            State.getInstance().getLoadedGraph().getWeight(navNodes.get(navNode).getFrom(), navNodes.get(navNode).getTo());
                } else {
                    length += ((1 - navNodes.get(navNode).getRatio())) *
                            State.getInstance().getLoadedGraph().getWeight(navNodes.get(navNode).getTo(), navNodes.get(navNode).getFrom());
                }
                if (route.get(i + 1) == navNodes.get(navNode).getFrom()) {
                    length += (navNodes.get(navNode).getRatio()) *
                            State.getInstance().getLoadedGraph().getWeight(navNodes.get(navNode).getTo(), navNodes.get(navNode).getFrom());
                } else {
                    length += ((1 - navNodes.get(navNode).getRatio())) *
                            State.getInstance().getLoadedGraph().getWeight(navNodes.get(navNode).getFrom(), navNodes.get(navNode).getTo());
                }
                i++;
                navNode++;
            } else {
                length += State.getInstance().getLoadedGraph().getWeight(route.get(i - 1), route.get(i));
            }
        }
        return length;
    }
      
}
