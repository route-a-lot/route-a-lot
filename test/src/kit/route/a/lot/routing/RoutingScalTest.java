package kit.route.a.lot.routing;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Progress;
import kit.route.a.lot.common.Projection;
import kit.route.a.lot.common.ProjectionFactory;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.controller.State;
import kit.route.a.lot.io.OSMLoader;

import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;


public class RoutingScalTest {
    static OSMLoader loader;
    static RoutingGraph graph;
    
    //System.out.println("Calculated route in " + (duration / 1000) + "s");
    
    @BeforeClass
    public static void initialize() throws Exception {
        loader = new OSMLoader(State.getInstance());
        loader.importMap(new File("./test/resources/karlsruhe_big.osm"), new Progress());
        Precalculator.precalculate(new Progress());
        graph = State.getInstance().getLoadedGraph();
    }
    
//    @Test
    public void routingFromAToBSeizuireTest() {
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
              System.out.println(i / 100 + "% of the test completed");
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
    }
    
    //@Test
    public void fromAtoBScalTestInFile() throws Exception{
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
            startTime = System.currentTimeMillis();
            if (Router.calculateRoute(selections).size() < 200) {
                continue;
            }
            for (int j = 0; j < 10; j++) {  //avoidint 0
                route = Router.calculateRoute(selections);
            }
            duration = System.currentTimeMillis() - startTime;   //we don't want to waste time
            duration /= 10;
            writer.write(System.getProperty("line.separator"));
            writer.write("size: " + route.size());
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
        }
        
        writer.flush();
        writer.close();
    }
}
