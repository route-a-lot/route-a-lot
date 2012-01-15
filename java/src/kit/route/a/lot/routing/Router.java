package kit.route.a.lot.routing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

import kit.route.a.lot.common.Selection;
import kit.route.a.lot.common.WeightCalculator;
import kit.route.a.lot.controller.State;

import org.apache.log4j.Logger;


public class Router {

    /**
     * Operation calculateRoute
     * 
     * @return List<int>
     */
    
    private static Logger logger = Logger.getLogger(Router.class);
    
    public static List<Integer> calculateRoute() {
        List<Integer> route = new ArrayList<Integer>();
        Selection prev = (Selection) null;
        for (Selection navPoint: State.getInstance().getNavigationNodes()) {
            logger.info("Calculating route from " + prev.toString() + " to " + navPoint.toString());
            route.addAll(fromAToB(prev, navPoint));
        }
        return route;
    }

    /**
     * Operation calculateOptimizedRoute
     * 
     * @param -
     * @return List<int>
     */
    public static List<Integer> calculateOptimizedRoute() {
        return calculateRoute();
    }

    private static List<Integer> fromAToB(Selection a, Selection b) {
        // ToDo: rename?
        RoutingGraph graph = State.getInstance().getRoutingGraph();
        PriorityQueue<Route> heap = new PriorityQueue<Route>(2, new RouteComparator<Route>());
        Route currentPath = null;
        if (a == null || b == null) {
            logger.warn("Can't calculate route for one Selection only");
            return (List<Integer>) null;
        }
        // This helps us to reduce redundancy at a low cost.
        boolean[] seen = new boolean[graph.getIDCount()];
        Arrays.fill(seen, false);   // Is this necessary?
        // Initialize heap
        heap.add(new Route(a.getFrom(), (int) (graph.getWeight(a.getFrom(), a.getTo()) * a.getRatio())));
        heap.add(new Route(a.getTo(), (int) (graph.getWeight(a.getTo(), a.getFrom()) * (1 / a.getRatio()))));
        // start the calculation.
        int currentNode;
        
        while (heap.peek() != null) {
            currentPath = heap.poll();
            currentNode = currentPath.getNode();
            if (seen[currentNode]) {
                // We already know a (shorter) path to that node, so ignore it.
                continue;
            }
            // At this point currentPath ALWAYS contains the shortest path to currentPath.getNode() (with regards to Arc-Flags).
            seen[currentNode] = true;
            // the target requires a special node, since both paths to it have to be put on the heap.
            // (everything else would be a pain in the ass)
            if (currentNode == b.getTo()) {
                // We can't return now, as there might be a shorter way via b.getFrom().
                heap.add(new Route(-1, (int) (1/b.getRatio()) * WeightCalculator.getInstance().calcWeight(b), currentPath));
            } else if (currentNode == b.getFrom()) {
                heap.add(new Route(-1, (int) b.getRatio() * WeightCalculator.getInstance().calcWeight(b), currentPath));
            } else if (currentNode == -1) {
                // This is the shortest path.
                logger.info("Found route from " + a.toString() + " to " + b.toString());
                return currentPath.toList();
            }
            for (Integer to: graph.getRelevantNeighbors(currentNode, new byte[] {graph.getAreaID(b.getFrom()), graph.getAreaID(b.getTo())})) {
                // Here we add the new paths.
                heap.add(new Route(to, graph.getWeight(currentNode, to), currentPath));
            }
        }
        // No path was found, maybe raise an error?
        logger.error("Couldn't find any route at all from " + a.toString() + " to " + b.toString() + ". Are you sure it is even possible?");
        return (List<Integer>) null;
    }
}
