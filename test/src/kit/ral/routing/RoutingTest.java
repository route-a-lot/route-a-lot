
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
import kit.ral.common.Selection;
import kit.ral.common.WeightCalculator;
import kit.ral.common.util.StringUtil;
import kit.ral.common.util.Util;
import kit.ral.controller.State;
import kit.ral.io.OSMLoader;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;


public class RoutingTest {

    private static OSMLoader loader;
    private static final int SIMPLE_ROUTES_NUMBER = 10000;
    private static final int PER_ADVANCED = 0; // number of tests for each optimized number
    private static final int ADVANCED_ROUTES_TILL = 10; // targets without optimizing

    private static final int TARGETS_OPT = 1000; // test per target
    
    private static Logger logger = Logger.getLogger(RoutingTest.class);


    @BeforeClass
    public static void initialize() {
        State state = State.getInstance();
        state.resetMap();
        configureLogger();
        loader = new OSMLoader(state, new WeightCalculator(state));
        loader.importMap(new File("./test/resources/karlsruhe_big_current.osm"), new Progress());
        Precalculator.precalculate(new Progress());
        try {
            RoutingGraph graph = state.getLoadedGraph();
            DataInputStream inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(new File("sral/graph1"))));
            graph.loadFromInput(inputStream);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("Import finished. Class set up for test.");
    }
    
    private static void configureLogger() {
        Properties properties = new Properties();
        properties.put("log4j.appender.stdout", "org.apache.log4j.ConsoleAppender");
        properties.put("log4j.appender.stdout.layout", "org.apache.log4j.PatternLayout");
        properties.put("log4j.appender.stdout.layout.ConversionPattern", "%-4r [%t] %-5p %c %x - %m%n");
        properties.put("log4j.appender.logfile", "org.apache.log4j.FileAppender");
        properties.put("log4j.appender.logfile.File", "routing.test.log");
        properties.put("log4j.appender.logfile.layout", "org.apache.log4j.PatternLayout");
        properties.put("log4j.appender.logfile.layout.ConversionPattern", "%-4r [%t] %-5p %c %x - %m%n");
        properties.put("log4j.logger.kit", "INFO, stdout, logfile");
        properties.put("log4j.logger.kit.ral.routing.Router", "ERROR");
        properties.put("log4j.logger.kit.ral.io.OSMLoader", "ERROR");
        properties.put("log4j.additivity.kit.ral.routing.RoutingTest", "false");
        properties.put("log4j.logger.kit.ral.routing.RoutingTest", "DEBUG, stdout");
        PropertyConfigurator.configure(properties);
    }
    
    @Test
    public void simpleRoutingTest() throws Exception { // simple routing test
        logger.debug("Starting simple routing test.");
        
        File testRoutes = new File("SimpleRoutingTestFile.bin");
        if (!testRoutes.exists()) {
            logger.debug("File does not exist. Creating new one...");
            Util.startTimer();
            createSimpleRouteTestFile(testRoutes);
            logger.debug("Created new file in " + Util.stopTimer());
        }

        DataInputStream stream = new DataInputStream(new BufferedInputStream(new FileInputStream(testRoutes)));

        if (stream.readInt() != SIMPLE_ROUTES_NUMBER || stream.readInt() != ADVANCED_ROUTES_TILL // test
                                                                                                 // conditions
                                                                                                 // changed
                || stream.readInt() != PER_ADVANCED) {
            logger.debug("File does exist but can't be used. Creating new one...");
            stream.close();
            testRoutes.delete();
            createSimpleRouteTestFile(testRoutes);
            stream = new DataInputStream(new FileInputStream(testRoutes));
            stream.readInt();
            stream.readInt();
            stream.readInt();
            logger.debug("Created new file in " + Util.stopTimer());
        }

        logger.debug("Running tests...");
        Util.startTimer();
        
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
            
            int correctLength = stream.readInt();
            if (correctLength != length) {
                logger.error("Wrong route: " + selections.toString());
            }

            assertEquals(correctLength, length);
            
            if (i % 256 == 1) {
                long elapsedTime = Util.getTimer() / 1000000000;
                logger.debug("Finished " + (i * 100) / SIMPLE_ROUTES_NUMBER + " %.");
                logger.debug("Elapsed time: " + StringUtil.formatSeconds(elapsedTime, true)
                        + " - estimated time remaining: " + StringUtil.formatSeconds(
                        (long)(elapsedTime  * ((SIMPLE_ROUTES_NUMBER / (double) i) - 1)), true));
            }

        }
        
        long totalNs = Util.getTimer();
        long nsPerRoute = totalNs / SIMPLE_ROUTES_NUMBER;
        
        logger.info("total time: " + totalNs + " ns" + " = " + StringUtil.formatSeconds(totalNs / 1000000000, true));
        logger.info("ns per route: " + nsPerRoute);
        
        stream.close();
        
        logger.debug("Tests finished sucessfully in " + Util.stopTimer());
    }

//    @Test
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

        DataOutputStream stream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));

        stream.writeInt(SIMPLE_ROUTES_NUMBER);
        stream.writeInt(ADVANCED_ROUTES_TILL);
        stream.writeInt(PER_ADVANCED);

        Selection start;
        Selection target;

        ArrayList<Selection> selections = new ArrayList<Selection>();

        Util.startTimer();
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
        
        stream.close();

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
