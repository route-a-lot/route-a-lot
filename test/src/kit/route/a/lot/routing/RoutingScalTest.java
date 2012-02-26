package kit.route.a.lot.routing;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import kit.route.a.lot.common.Progress;
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
    
    @Test
    public void routingFromAToB() {
        int tries = 10000;
        double startTime;
        double duration;
        double resTime = 0;
        double t50 = 0;
        int s50 = 0;
        double t100 = 0;
        int s100 = 0;
        double t150 = 0;
        int s150 = 0;
        double t200 = 0;
        int s200 = 0;
        double t250 = 0;
        int s250 = 0;
        double t300 = 0;
        int s300 = 0;
        int over300 = 0;
        List<Selection> selections;
        List<Integer> route;
        for(int i = 0; i < tries; i++) {
            selections = new ArrayList<Selection>();
            selections.add(SelectMock.getRandomSelection());
            selections.add(SelectMock.getRandomSelection());
            startTime = System.currentTimeMillis();
            route = Router.calculateRoute(selections);
            duration = System.currentTimeMillis() - startTime;   //we don't want to waste time
            resTime += duration; 
            
            
            
           if (route.size() < 50 && !(duration < 0.00001)) {
               t50 += duration;
               s50++;
           } else if (route.size() < 100 && !(duration < 0.00001)) {
               t100 += duration;
               s100++;
           } else if (route.size() < 150 && !(duration < 0.00001)) {
               t150 += duration;
               s150++;
           } else if (route.size() < 200 && !(duration < 0.00001)){
               t200 += duration;
               s200++;
           } else if (route.size() < 250 && !(duration < 0.00001)) {
               t250 += duration;
               s250++;
           } else if (route.size() < 300 && !(duration < 0.00001)) {
               t300 += duration;
               s300++;
           } else {
               over300++;
           }
        }
        System.out.println("Routing with one start und target (WA = with Arc-Flags, NA = without Arc-Flags): ");
        System.out.println("\nnumber of routes with length < 50: " + s50);
        System.out.println("average time for calculating routes with length < 50: ");
        System.out.println("WA: " + t50/s50 + "ms");
        System.out.println("\nnumber of routes with 49 < length < 100: " + s100);
        System.out.println("average time for calculating routes with 49 < length < 100: ");
        System.out.println("WA: " + t100/s100 + "ms");
        System.out.println("\nnumber of routes with 99 < length < 150: " + s150);
        System.out.println("average time for calculating routes with 99 < length < 149: ");
        System.out.println("WA: " + t150/s150 + "ms");
        System.out.println("\nnumber of routes with 149 < length < 200: " + s200);
        System.out.println("average time for calculating routes with 149 < length < 200: ");
        System.out.println("WA: " + t200/s200 + "ms");
        System.out.println("\nnumber of routes with 199 < length < 250: " + s250);
        System.out.println("average time for calculating routes with 199 < length < 250: ");
        System.out.println("WA: " + t250/s250 + "ms");
        System.out.println("\nnumber of routes with 249 < length < 300: " + s300);
        System.out.println("average time for calculating routes with 249 < length < 300: ");
        System.out.println("WA: " + t300/s300 + "ms");
        System.out.println("\nroutes with length > 299 :" + over300);
    }
}
