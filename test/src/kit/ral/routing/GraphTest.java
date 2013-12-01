
/**
Copyright (c) 2012, Matthias Grundmann, Jan Jacob, Daniel Krauß, Josua Stabenow
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
        * Redistributions of source code must retain the above copyright
          notice, this list of conditions and the following disclaimer.
        * Redistributions in binary form must reproduce the above copyright
          notice, this list of conditions and the following disclaimer in the
          documentation and/or other materials provided with the distribution.
        * The names of the contributors may not be used to endorse or promote products
          derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
**/

package kit.ral.routing;

import kit.ral.common.Progress;
import kit.ral.common.WeightCalculator;
import kit.ral.controller.State;
import kit.ral.io.OSMLoader;
import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;


public class GraphTest {

    private static OSMLoader loaderForSimpleGraph;
    private static OSMLoader loader;
    private static RoutingStateMock simpleRoutingState;
    private static RoutingGraph graph;


    @BeforeClass
    public static void initialize() {
        System.out.println("starte import");
        State state = State.getInstance();
        state.resetMap();
        PropertyConfigurator.configure("config/log4j.conf");
        loader = new OSMLoader(state, new WeightCalculator(state));
        simpleRoutingState = new RoutingStateMock();
        loaderForSimpleGraph = new OSMLoader(simpleRoutingState, new WeightCalculator(state));
        loader.importMap(new File("./test/resources/karlsruhe_small_current.osm"), new Progress());
        loaderForSimpleGraph.importMap(new File("./test/resources/karlsruhe_small_current.osm"), new Progress());
        Precalculator.precalculate(new Progress());
        graph = state.getLoadedGraph();
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
        RoutingGraph reInvertedGraph = graph.getInverted().getInverted();

        int[] reInvertedGraphStartEdges = reInvertedGraph.getStartIDArray();
        int[] normalGraphStartEdges = graph.getStartIDArray();

        assertArrayEquals(normalGraphStartEdges, reInvertedGraphStartEdges);

        for (int i = -1; i < normalGraphStartEdges.length; i++) { // -1 and getstart . . ..length, for error
                                                                  // handling test
            assertTrue(graph.getAllNeighbors(i).containsAll(reInvertedGraph.getAllNeighbors(i)));
            assertTrue(reInvertedGraph.getAllNeighbors(i).containsAll(graph.getAllNeighbors(i)));
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

}
