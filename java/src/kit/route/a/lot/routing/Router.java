package kit.route.a.lot.routing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

import kit.route.a.lot.common.Selection;
import kit.route.a.lot.controller.State;
import kit.route.a.lot.io.WeightCalculatorMock;

import org.apache.log4j.Logger;


public class Router {

    private static WeightCalculatorMock weightCalculator = new WeightCalculatorMock();

    /**
     * Operation calculateRoute
     * 
     * @return List<int>
     */

    private static Logger logger = Logger.getLogger(Router.class);

    public static List<Integer> calculateRoute() {
        boolean optimised = false;
        if (optimised) {
            return optimisedRoute();
        } else {
            return simpleRoute();
        }
    }
    
    private static List<Integer> optimisedRoute() {
        Route shortest = null;
        List<Selection> navigationNodes = State.getInstance().getNavigationNodes();
        int size = navigationNodes.size();
        Route[][] routes = new Route[size][size];   // Matrix containing the shortest routes
        for (int j = 0; j < size; j++) {
            for (int i = 0; i < size; i++) {
                // Fill the Matrix
                routes[j][i] = fromAToB(navigationNodes.get(i), navigationNodes.get(j));
            }
        }
        for (List<Integer> permutation: permutations(size)) {
            Route tempRoute = new Route();
            int prev = permutation.get(0);
            for (Integer path: parmutation) {
                if (prev == path) {
                    continue;
                }
                tempRoute.join(routes[prev][path]);
                prev = path;
            }
            if (shortest == null || shortest.length() > tempRoute.length()) {
                shortest = tempRoute;
            }
        }
        return shortest.toList();
    }

    private static List<Integer> simpleRoute(){
        Route route = new Route();
        Route tempRoute;
        List<Selection> navigationNodes = State.getInstance().getNavigationNodes();
        Selection prev = navigationNodes.get(0);
        for (Selection navPoint : navigationNodes) {
            if (prev == navPoint) {
                continue;
            }
            logger.info("Calculating route from " + prev.toString() + " to " + navPoint.toString());
            tempRoute = fromAToB(prev, navPoint);
            if (tempRoute == null) {
                logger.warn("Failed to find route, returning null");
                return null;
            }
            prev = navPoint;
            route = route.join(tempRoute);
        }
        // System.out.println(route.size());
        return route.toList();
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

    private static Route fromAToB(Selection a, Selection b) {
        // ToDo: rename?
        RoutingGraph graph = State.getInstance().getLoadedGraph();
        PriorityQueue<Route> heap = new PriorityQueue<Route>(2, new RouteComparator<Route>());
        Route currentPath = null;
        if (a == null || b == null) {
            logger.warn("Can't calculate route for one Selection only");
            return new Route();
        }
        // This helps us to reduce redundancy at a low cost.
        boolean[] seen = new boolean[graph.getIDCount()];
        Arrays.fill(seen, false); // Is this necessary?
        // Initialize heap
        heap.add(new Route(a.getFrom(), (int) (graph.getWeight(a.getFrom(), a.getTo()) * a.getRatio())));
        heap.add(new Route(a.getTo(), (int) (graph.getWeight(a.getTo(), a.getFrom()) * (1 / a.getRatio()))));
        // start the calculation.
        int currentNode;

        while (heap.peek() != null) {
            currentPath = heap.poll();
            currentNode = currentPath.getNode();
            if (currentNode > 0 && seen[currentNode]) {
                // We already know a (shorter) path to that node, so ignore it.
                continue;
            }
            // At this point currentPath ALWAYS contains the shortest path to currentPath.getNode() (with
            // regards to Arc-Flags).
            if (currentNode != -1) {
                seen[currentNode] = true;
            }
            // the target requires a special node, since both paths to it have to be put on the heap.
            // (everything else would be a pain in the ass)
            if (currentNode == b.getTo()) {
                // We can't return now, as there might be a shorter way via b.getFrom().
                heap.add(new Route(-1, (int) (1 / b.getRatio()) * weightCalculator.calcWeight(b), currentPath));
            } else if (currentNode == b.getFrom()) {
                heap.add(new Route(-1, (int) b.getRatio() * weightCalculator.calcWeight(b), currentPath));
            } else if (currentNode == -1) {
                // This is the shortest path.
                logger.info("Found route from " + a.toString() + " to " + b.toString());
                return currentPath;
            }
            for (Integer to : graph.getRelevantNeighbors(currentNode,
                    new byte[] { graph.getAreaID(b.getFrom()), graph.getAreaID(b.getTo()) })) {
                // Here we add the new paths.
                heap.add(new Route(to, graph.getWeight(currentNode, to), currentPath));
            }
        }
        // No path was found, maybe raise an error?
        logger.error("Couldn't find any route at all from " + a.toString() + " to " + b.toString()
                + ". Are you sure it is even possible?");
        return new Route();
    }
}
