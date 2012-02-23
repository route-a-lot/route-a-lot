package kit.route.a.lot.routing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

import kit.route.a.lot.common.Progress;
import kit.route.a.lot.common.Selection;
import static kit.route.a.lot.common.Util.*;
import kit.route.a.lot.controller.State;
import org.apache.log4j.Logger;


public class Router {

    private static Logger logger = Logger.getLogger(Router.class);

    public static List<Integer> calculateRoute(List<Selection> navigationNodes) {
        return (navigationNodes.size() < 2) ? new ArrayList<Integer>(0) : simpleRoute(navigationNodes);
    }
    
    public static void optimizeRoute(List<Selection> navigationNodes, Progress p) {
        int size = navigationNodes.size();
        if (size < 4) {
            return;
        }
        int[][] routes = new int[size][size];   // Matrix containing the length of the shortest routes
        for (int j = 0; j < size; j++) {
            for (int i = 0; i < size; i++) {
                p.add(0.8 / (size * size));
                // Fill the Matrix
                Route route = fromAToB(navigationNodes.get(j), navigationNodes.get(i));
                if (route == null) {
                    logger.warn("Ignoring route ...");
                    routes[j][i] = -1;
                } else {
                    routes[j][i] = route.getLength();
                }  
            }  
        }
        
        int[] result = null;  // The shortest permutation (so far)
        int resultLength = -1;  
        int faculty = fak(size - 1);
        for (int f = 0; f < faculty; f++) {
            p.add(0.2 / faculty);
            // Iterate over all permutations
            boolean isRouteable = true;
            int[] current = permutation(size - 2, f);
            int currentLength = 0;
            for (int i = 0; i < current.length - 1; i++) {
                int routeLength = routes[current[i]][current[i+1]];
                if (routeLength >= 0) {
                    currentLength += routeLength;
                } else {
                    logger.warn("Unroutable permutation: " + printPermutation(current));
                    isRouteable = false;
                    break;
                }
            }
            if (isRouteable) {
                // We're still missing the length from the start to the permutation as well 
                // as from the permutation to  the target.
                currentLength += routes[0][current[0]] + routes[current[current.length - 1]][size - 1];
                if ((currentLength < resultLength) || (resultLength == -1)) {
                    // We got a shorter permutation!
                    logger.debug("Length of shortest permutation (so far) "
                            + printPermutation(current) + " :" + currentLength);
                    result = current;
                    resultLength = currentLength;
                }
            }
        }
        setSelection(navigationNodes, result);
        p.finish();
    }
    
    private static String printPermutation(int[] permutation) {
        String result = "";
        for (int node: permutation) {
            result += node + " ";
        }
        return result;
    }

    private static void setSelection(List<Selection> navigationNodes, int[] mapping) {
        // Reorders the navigationNodes
        if (mapping == null) {
            logger.warn("Got empty mapping, something failed.");
            return;
        }
        logger.info("remapping NavNodes: " + printPermutation(mapping));
        Selection[] oldNodes = navigationNodes.toArray(new Selection[navigationNodes.size()]);
        navigationNodes.clear();
        navigationNodes.add(oldNodes[0]);
        for (int i = 0; i < mapping.length; i++) {
            navigationNodes.add(oldNodes[mapping[i]]);
        }
        navigationNodes.add(oldNodes[oldNodes.length - 1]);
        
        logger.debug("Old ordering: " + Arrays.toString(oldNodes));
        logger.debug("New ordering: " + navigationNodes.toString());
    }

    private static List<Integer> simpleRoute(List<Selection> navigationNodes){
        // Calculates a route via several navNodes
        List<Integer> result = new ArrayList<Integer>();
        Route route;
        Selection prev = navigationNodes.get(0);
        for (Selection navPoint : navigationNodes) {
            if (prev == navPoint) {
                continue;
            }
            logger.debug("Calculating route from " + prev.toString() + " to " + navPoint.toString() + ".");
            route = fromAToB(prev, navPoint);
            if (route != null) {    //TODO is this the best way to handle this situation (I dont think so)
                result.addAll(route.toList());
                prev = navPoint;
            } else {
                logger.warn("Ignoring " + navPoint + " for routing." + " (no path found).");
                return new ArrayList<Integer>();    //TODO l.a.
            }
        }
        // // System.out.println(route.size());
        return result;
    }

    private static Route fromAToB(Selection a, Selection b) {
        // ToDo: rename?
        // Renderer renderer = State.getInstance().getController.getRender();
        RoutingGraph graph = State.getInstance().getLoadedGraph();
        PriorityQueue<Route> heap = new PriorityQueue<Route>(2, new RouteComparator<Route>());
        Route currentPath = null;
        if (a == null || b == null) {
            logger.warn("Can't calculate route for one Selection only");
            return null;
        }
        // This helps us to reduce redundancy at a low cost.
        boolean[] seen = new boolean[graph.getIDCount()];
        Arrays.fill(seen, false); // Is this necessary?
        // Initialize heap
        int weight1 = graph.getWeight(a.getFrom(), a.getTo());
        int weight2 = graph.getWeight(a.getTo(), a.getFrom());
        if (weight1 != -1 && weight2 != -1) {
            int weightFrom = (int) (graph.getWeight(a.getFrom(), a.getTo()) * a.getRatio());
            int weightTo = (int) (graph.getWeight(a.getFrom(), a.getTo()) * (1 - a.getRatio()));
            if (weightFrom == weight1) {
                weightTo++; //to avoid same weights of different routes
            } else if (weightTo == weight1){
                weightFrom++;
            }
            heap.add(new Route(a.getFrom(), weightFrom));
            heap.add(new Route(a.getTo(), weightTo));
        } else if (weight1 != -1) { //one way "to two"
            heap.add(new Route(a.getTo(), (int) (weight1 * (1 - a.getRatio()))));
        } else {    //one way in direction to -> from
            heap.add(new Route(a.getFrom(), (int) (weight2 * (1 - a.getRatio()))));
        }
        
        // start the calculation.
        int currentNode;
        int selectionWeight;
        
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
                selectionWeight = graph.getWeight(b.getTo(), b.getFrom());
                if (selectionWeight > 0 || b.getRatio() > 0.99) {   //0.99 cause usually we don't have to go in this direction
                    heap.add(new Route(-1, (int) (1 - b.getRatio()) * selectionWeight, currentPath));
                }
            } else if (currentNode == b.getFrom()) {
                selectionWeight = graph.getWeight(b.getFrom(), b.getTo());
                if (selectionWeight > 0 || b.getRatio() < 0.01) {
                    heap.add(new Route(-1, ((int) b.getRatio() * selectionWeight), currentPath));
                }
            } else if (currentNode == -1) {
                // This is the shortest path.
                logger.debug("Found route from " + a.toString() + " to " + b.toString() + ": " + currentPath);
                return currentPath.getRoute();
            }
            for (Integer to : graph.getRelevantNeighbors(currentNode, new byte[] { graph.getAreaID(b.getFrom()), graph.getAreaID(b.getTo()) })) {
                // Here we add the new paths.
                selectionWeight = graph.getWeight(currentNode, to);
                if (selectionWeight > 0) {
                    heap.add(new Route(to, selectionWeight, currentPath));
                } else {
                    logger.fatal("Got negative weight for a route; please fix");
                }
            }
        }
        // No path was found, maybe raise an error?
        logger.debug("Couldn't find any route at all from " + a.toString() + " to " + b.toString()
                + ". Are you sure it is even possible?");
        return null;
    }
}
