package kit.route.a.lot.routing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

import kit.route.a.lot.common.Selection;
import static kit.route.a.lot.common.Util.*;
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
            return simpleRoute();
    }
    
    public static List<Selection> optimizeRoute() {
        List<Selection> navigationNodes = State.getInstance().getNavigationNodes();
        Route route;
        int size = navigationNodes.size();
        if (size < 4) {
            return navigationNodes;
        }
        int[][] routes = new int[size][size];   // Matrix containing the length of the shortest routes
        for (int j = 0; j < size; j++) {
            for (int i = 0; i < size; i++) {
                // Fill the Matrix
                route = fromAToB(navigationNodes.get(j), navigationNodes.get(i));
                if (route == null) {
                    logger.warn("Ignoring route ...");
                    routes[j][i] = -1;
                } else {
                    routes[j][i] = route.length();
                }
            }
        }
        int[] shortest = null;  // The shortest permutation (so far)
        int shortestLength = -1;    // The length of the shortest permutation (so far)
        int length, i, routeLength; // The length of the current permutation / current Route /
                                    // a counter for the elements of the permutation
        int count = 0;  // saves at which permutation we are
        int[] permutation;  // the permutation
        boolean skip;
        while (count < fak(size)) {
            // Iterate over all permutations
            skip = false;
            permutation = permutation(size - 1, count++);
            length = i = 0;
            while(i + 1 < permutation.length) {
                routeLength = routes[permutation[i]][permutation[++i]];
                if (routeLength == -1) {
                    // permutation is not routable
                    skip = true;
                    break;
                } else {
                    length += routeLength;
                }
            }
            if (skip) {
                logger.warn("Skipping route");
                continue;
            }
            // We're still missing the length from the start to the permutation as well 
            // as from the permutation to  the target.
            if (length + routes[permutation[i-1]][size - 1] +  routes[0][permutation[0]] < shortestLength
                    || shortestLength == -1) {
                // We got a shorter permutation!
                shortest = permutation;
            }
        }
        return setSelection(shortest);
    }
    
    private static List<Selection> setSelection(int[] mapping) {
        // Reorders the navigationNodes
        List<Selection> navigationNodes = State.getInstance().getNavigationNodes();
        if (mapping == null) {
            logger.warn("Got empty mapping, something failed.");
            return navigationNodes;
        }
        List<Selection> newNavigationNodes = new ArrayList<Selection>();
        newNavigationNodes.add(navigationNodes.get(0));
        for (int i = 0; i < mapping.length; i++) {
            newNavigationNodes.add(navigationNodes.get(mapping[i]));
        }
        newNavigationNodes.add(navigationNodes.get(navigationNodes.size() - 1));
        return newNavigationNodes;
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
