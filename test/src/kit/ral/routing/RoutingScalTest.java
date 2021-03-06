
/**
Copyright (c) 2012, Daniel Krauß, Matthias Grundmann, Jan Jacob, Josua Stabenow
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

import kit.ral.common.Coordinates;
import kit.ral.common.Progress;
import kit.ral.common.Selection;
import kit.ral.common.WeightCalculator;
import kit.ral.common.projection.Projection;
import kit.ral.common.projection.ProjectionFactory;
import kit.ral.controller.State;
import kit.ral.io.OSMLoader;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;


public class RoutingScalTest {
    static OSMLoader loader;
    static RoutingGraph graph;
    
    @BeforeClass
    public static void setUp() {
        PropertyConfigurator.configure("config/log4j.conf");
    }
    
    @Before
    public void initialize() throws Exception {
        System.out.println("starte import");
        State state = State.getInstance();
        State.getInstance().resetMap();
        loader = new OSMLoader(state, new WeightCalculator(state));
        loader.importMap(new File("./test/resources/karlsruhe_big.osm"), new Progress());
        Precalculator.precalculate(new Progress());
        graph = state.getLoadedGraph();
        System.out.println("import abgeschlossen");
    }
    
    @Ignore
    public void routingFromAToBSeizuireTest() throws Exception {
        int tries = 10000;
        double startTime;
        double duration;
        double WA_resTime = 0;
        double NA_resTime = 0;
        double WA_t50 = 0;
        double NA_t50 = 0;
        int s50 = 0;
        double WA_t100 = 0;
        double NA_t100 = 0;
        int s100 = 0;
        double WA_t150 = 0;
        double NA_t150 = 0;
        int s150 = 0;
        double WA_t200 = 0;
        double NA_t200 = 0;
        int s200 = 0;
        double WA_t250 = 0;
        double NA_t250 = 0;
        int s250 = 0;
        double WA_t300 = 0;
        double NA_t300 = 0;
        int s300 = 0;
        int over300 = 0;
        List<Selection> selections;
        List<Integer> route = null;
        for(int i = 0; i < tries; i++) {
            selections = new ArrayList<Selection>();
            selections.add(SelectMock.getRandomSelection());
            selections.add(SelectMock.getRandomSelection());
            startTime = System.currentTimeMillis();
            for (int j = 0; j < 100; j++) {  //avoidint 0
                route = Router.calculateRoute(selections);
            }
            duration = System.currentTimeMillis() - startTime;   //we don't want to waste time
            duration /= 100;
            WA_resTime += duration; 
            
            if (duration < 0.001) {    //for testing if we got 0
                System.err.println("WA daration is: " + duration);
            }
            
           if (route.size() < 50) {
               WA_t50 += duration;
               s50++;
           } else if (route.size() < 100) {
               WA_t100 += duration;
               s100++;
           } else if (route.size() < 150) {
               WA_t150 += duration;
               s150++;
           } else if (route.size() < 200){
               WA_t200 += duration;
               s200++;
           } else if (route.size() < 250) {
               WA_t250 += duration;
               s250++;
           } else if (route.size() < 300) {
               WA_t300 += duration;
               s300++;
           } else {
               over300++;
           }
           
           
           
           startTime = System.currentTimeMillis();
           for (int j = 0; j < 10; j++) {
               SimpleRouter.calculateRoute(selections);
           }
           duration = System.currentTimeMillis() - startTime;   //we don't want to waste time
           duration /= 10;
           NA_resTime += duration; 
           
           if (duration < 0.001) {    //for testing if we got 0
               System.err.println("NA duration is "+duration);
           }
           
          if (route.size() < 50) {
              NA_t50 += duration;
          } else if (route.size() < 100) {
              NA_t100 += duration;
          } else if (route.size() < 150) {
              NA_t150 += duration;
          } else if (route.size() < 200){
              NA_t200 += duration;
          } else if (route.size() < 250) {
              NA_t250 += duration;
          } else if (route.size() < 300) {
              NA_t300 += duration;
          }
          if (i % 100 == 0) {
              System.out.println(i / 100 + "% completed");
          }
        }
        System.out.println("number of vert.: " + graph.getIDCount());
        System.out.println("number of edges: " + graph.getEdgesArray().length);
        System.out.println("Routing with one start und target (WA = with Arc-Flags, NA = without Arc-Flags): ");
        System.out.println("Number of routes: 10000" );
        System.out.println("average WA time: " + WA_resTime/10000);
        System.out.println("average NA time: " + NA_resTime/10000);
        System.out.println("\nnumber of routes with length < 50: " + s50);
        System.out.println("average time for calculating routes with length < 50: ");
        System.out.println("WA: " + WA_t50/s50 + "ms");
        System.out.println("NA: " + NA_t50/s50 + "ms");
        System.out.println("\nnumber of routes with 49 < length < 100: " + s100);
        System.out.println("average time for calculating routes with 49 < length < 100: ");
        System.out.println("WA: " + WA_t100/s100 + "ms");
        System.out.println("NA: " + NA_t100/s100 + "ms");
        System.out.println("\nnumber of routes with 99 < length < 150: " + s150);
        System.out.println("average time for calculating routes with 99 < length < 149: ");
        System.out.println("WA: " + WA_t150/s150 + "ms");
        System.out.println("NA: " + NA_t150/s150 + "ms");
        System.out.println("\nnumber of routes with 149 < length < 200: " + s200);
        System.out.println("average time for calculating routes with 149 < length < 200: ");
        System.out.println("WA: " + WA_t200/s200 + "ms");
        System.out.println("NA: " + NA_t200/s200 + "ms");
        System.out.println("\nnumber of routes with 199 < length < 250: " + s250);
        System.out.println("average time for calculating routes with 199 < length < 250: ");
        System.out.println("WA: " + WA_t250/s250 + "ms");
        System.out.println("NA: " + NA_t250/s250 + "ms");
        System.out.println("\nnumber of routes with 249 < length < 300: " + s300);
        System.out.println("average time for calculating routes with 249 < length < 300: ");
        System.out.println("WA: " + WA_t300/s300 + "ms");
        System.out.println("NA: " + NA_t300/s300 + "ms");
        System.out.println("\nroutes with length > 299 :" + over300);
        FileWriter writer = new FileWriter(new File("RoutingScalTest0-300result.txt"), false);
        writer.write("number of vert.: " + graph.getIDCount());
        writer.write(", number of edges: " + graph.getEdgesArray().length);
        writer.write(System.getProperty("line.separator"));
        writer.write("Routing with one start und target (WA = with Arc-Flags, NA = without Arc-Flags): ");
        writer.write(System.getProperty("line.separator"));
        writer.write("Number of routes: 10000" );
        writer.write(System.getProperty("line.separator"));
        writer.write("average WA time: " + WA_resTime/10000);
        writer.write(System.getProperty("line.separator"));
        writer.write("average NA time: " + NA_resTime/10000);
        writer.write(System.getProperty("line.separator"));
        writer.write(System.getProperty("line.separator"));
        writer.write("\nnumber of routes with length < 50: " + s50);
        writer.write(System.getProperty("line.separator"));
        writer.write("average time for calculating routes with length < 50: ");
        writer.write(System.getProperty("line.separator"));
        writer.write("WA: " + WA_t50/s50 + "ms");
        writer.write(System.getProperty("line.separator"));
        writer.write("NA: " + NA_t50/s50 + "ms");
        writer.write(System.getProperty("line.separator"));
        writer.write(System.getProperty("line.separator"));
        writer.write("\nnumber of routes with 49 < length < 100: " + s100);
        writer.write(System.getProperty("line.separator"));
        writer.write("average time for calculating routes with 49 < length < 100: ");
        writer.write(System.getProperty("line.separator"));
        writer.write("WA: " + WA_t100/s100 + "ms");
        writer.write(System.getProperty("line.separator"));
        writer.write("NA: " + NA_t100/s100 + "ms");
        writer.write(System.getProperty("line.separator"));
        writer.write(System.getProperty("line.separator"));
        writer.write("\nnumber of routes with 99 < length < 149: " + s150);
        writer.write(System.getProperty("line.separator"));
        writer.write("average time for calculating routes with 99 < length < 149: ");
        writer.write(System.getProperty("line.separator"));
        writer.write("WA: " + WA_t150/s150 + "ms");
        writer.write(System.getProperty("line.separator"));
        writer.write("NA: " + NA_t150/s150 + "ms");
        writer.write(System.getProperty("line.separator"));
        writer.write(System.getProperty("line.separator"));
        writer.write("\nnumber of routes with 149 < length < 200: " + s200);
        writer.write(System.getProperty("line.separator"));
        writer.write("average time for calculating routes with 149 < length < 200: ");
        writer.write(System.getProperty("line.separator"));
        writer.write("WA: " + WA_t200/s200 + "ms");
        writer.write(System.getProperty("line.separator"));
        writer.write("NA: " + NA_t200/s200 + "ms");
        writer.write(System.getProperty("line.separator"));
        writer.write(System.getProperty("line.separator"));
        writer.write("\nnumber of routes with 199 < length < 250: " + s250);
        writer.write(System.getProperty("line.separator"));
        writer.write("average time for calculating routes with 199 < length < 250: ");
        writer.write(System.getProperty("line.separator"));
        writer.write("WA: " + WA_t250/s250 + "ms");
        writer.write(System.getProperty("line.separator"));
        writer.write("NA: " + NA_t250/s250 + "ms");
        writer.write(System.getProperty("line.separator"));
        writer.write(System.getProperty("line.separator"));
        writer.write("\nnumber of routes with 249 < length < 300: " + s300);
        writer.write(System.getProperty("line.separator"));
        writer.write("average time for calculating routes with 249 < length < 300: ");
        writer.write(System.getProperty("line.separator"));
        writer.write("WA: " + WA_t300/s300 + "ms");
        writer.write(System.getProperty("line.separator"));
        writer.write("NA: " + NA_t300/s300 + "ms");
        writer.write(System.getProperty("line.separator"));
        writer.write("\nroutes with length > 299 :" + over300);
        writer.flush();
        writer.close();
    }
    
    @Ignore
    public void fromAtoBScalTestInFile() throws Exception{
        int maxLength = 0;
        double maxValue = Double.MIN_VALUE;
        double startTime;
        double duration;
        FileWriter writer = new FileWriter(new File("RoutingScalTEstResult.txt"), false);

        List<Selection> selections = new ArrayList<Selection>();
        List<Integer> route = null;
        
        writer.write("number of vert.: " + graph.getIDCount());
        writer.write(", number of edges: " + graph.getEdgesArray().length);
        writer.write(System.getProperty("line.separator"));
        writer.write(System.getProperty("line.separator"));
        
        writer.write("from topLeft -> bottomRight");
        Projection projection = ProjectionFactory.getCurrentProjection();
        Coordinates target = projection.getLocalCoordinates(State.getInstance().getMapInfo().getGeoBottomRight());
        selections.add(State.getInstance().getMapInfo().select(target));
        target = projection.getLocalCoordinates(State.getInstance().getMapInfo().getGeoTopLeft());
        selections.add(State.getInstance().getMapInfo().select(target));
        startTime = System.currentTimeMillis();
        for (int j = 0; j < 100; j++) {  //avoidint 0
            route = Router.calculateRoute(selections);
        }
        duration = System.currentTimeMillis() - startTime;   //we don't want to waste time
        duration /= 100;
        writer.write("size: " + route.size());
        writer.write(System.getProperty("line.separator"));
        writer.write("with Arc-Flags: " + duration);
        writer.write(System.getProperty("line.separator"));
        startTime = System.currentTimeMillis();
        for (int j = 0; j < 100; j++) {  //avoidint 0
            route = SimpleRouter.calculateRoute(selections);
        }
        duration = System.currentTimeMillis() - startTime;   //we don't want to waste time
        duration /= 100;
        writer.write("without Arc-Flags: " + duration);
        writer.write(System.getProperty("line.separator"));
        
        
        for (int i = 0; i < 1000; i++) {    //modify number of tests
            selections = new ArrayList<Selection>();
            selections.add(SelectMock.getRandomSelection());
            selections.add(SelectMock.getRandomSelection());
            if (Router.calculateRoute(selections).size() != 0 && Router.calculateRoute(selections).size() < 200) {
                continue;
            }
            startTime = System.currentTimeMillis();
            for (int j = 0; j < 10; j++) {  //avoiding 0
                route = Router.calculateRoute(selections);
            }
            duration = System.currentTimeMillis() - startTime;   //we don't want to waste time
            duration /= 10;
            if (duration > maxValue) {
                maxValue = duration;
                maxLength = route.size();
            }
            writer.write(System.getProperty("line.separator"));
            if (route.size() == 0) {
                writer.write("!");
            }
            writer.write("category: " + route.size() / 100);    //faster searching in file
            writer.write(", size: " + route.size());
            writer.write(System.getProperty("line.separator"));
            writer.write("with Arc-Flags: " + duration);
            writer.write(System.getProperty("line.separator"));
            startTime = System.currentTimeMillis();
            for (int j = 0; j < 10; j++) {  //avoidint 0
                route = SimpleRouter.calculateRoute(selections);
            }
            duration = System.currentTimeMillis() - startTime;   //we don't want to waste time
            duration /= 10;
            writer.write("without Arc-Flags: " + duration);
            writer.flush();
        }
        writer.write(System.getProperty("line.separator"));
        writer.write(System.getProperty("line.separator"));
        writer.write("MaxTime: "+ maxValue + ", length: " + maxLength);
        writer.close();
    }
    
    
    @Ignore
    public void optimizedRoutingScalTest() throws Exception{
        FileWriter writer = new FileWriter(new File("OptimizedRoutingScalTEstResult.txt"), false);
        List<Selection> selections;
        double startTime;
        double duration;
        int i = 4;
        double tempdur;
        while(i < 14) {
            duration = 0;
            int k = (i < 13) ? 20 : 5;
            for (int l = 0; l < k; l++) {   // if we get a too fast, or slow route
                selections = new ArrayList<Selection>();
                for (int j = 0; j < i; j++) {
                    selections.add(SelectMock.getRandomSelection());
                }
                startTime = System.currentTimeMillis();
                Router.optimizeRoute(selections, new Progress());
                tempdur = System.currentTimeMillis() - startTime;
                duration += tempdur;
            }
            duration /= k;    //two steps for better overview
            writer.write("number of targets: " + i + ", time: " + duration + "ms");
            writer.write(System.getProperty("line.separator"));
            i++;
            writer.flush();
        }
        writer.close();
    }
}
