package kit.route.a.lot.routing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

import kit.route.a.lot.common.Listener;
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
        ArrayList<Selection> origNodes = new ArrayList<Selection>(navigationNodes);
        if (size < 4) {
            return;
        }

        
        // Greedy
        int[] permutation = new int[size - 2];
        int from = 0;
        int length = Integer.MAX_VALUE;
        boolean[] seen = new boolean[size];
        int tmp = -1;
        int total = 0;
        Route tempRoute;
        for (int i = 0; i < size - 2; i++) {
            tmp = -1;
            int min = -1;
            seen[from] = true;
            length = Integer.MAX_VALUE;
            for (int j = 1; j < size - 1; j++) {
                if (seen[j]) {
                    continue;
                }
                if ((tempRoute = fromAToB(origNodes.get(from), origNodes.get(j))) != null) {
                    length = tempRoute.getLength();
                }
                if (length != Integer.MAX_VALUE && (min == -1 || min > length)) {
                    min = length;
                    tmp = j;
                }
            }
            if (tmp == -1) {
                total = 0;
                logger.fatal("Debug me!");
                break;
            }
            from = tmp;
            total += min;
            permutation[i] = from;
//            permutation[i] = tmp;
//            seen[tmp] = true;
//            total += min;
        }
        int min = Integer.MAX_VALUE;
        if (fromAToB(origNodes.get(permutation[permutation.length - 1]), origNodes.get(origNodes.size() - 1)) == null) {
            total = 0;
        }
        if (total != 0) {
            total += fromAToB(origNodes.get(permutation[permutation.length - 1]), origNodes.get(origNodes.size() - 1)).getLength();
            min = totalLength(navigationNodes);
            if (min == 0 || total < min) {
                setSelection(origNodes, permutation, navigationNodes);
                min = total;
            }   
        }        
        
        long faculty = fak(size - 1);
        if (faculty < 0) {
            logger.warn("To many stopovers, aborting");
            return;
        }
        // perfect
        int[][] routes = new int[size][size];   // Matrix containing the length of the shortest routes
        double progressRatio = ((double) faculty) / (faculty + (size * size * size));
        for (int j = 0; j < size; j++) {
            for (int i = 0; i < size; i++) {
                p.addProgress((1 - progressRatio) / (size * size));
                // Fill the Matrix
                Route route = fromAToB(origNodes.get(j), origNodes.get(i));
                if (route == null) {
                    logger.warn("Ignoring route ...");
                    routes[j][i] = -1;
                } else {
                    routes[j][i] = route.getLength();
                }  
            }  
        }
        for (long f = 0; f < faculty; f++) {
            p.addProgress(progressRatio / faculty);
            // Iterate over all permutations
            boolean isRouteable = true;
            int[] current = permutation(size - 2, f);
            int currentLength = 0;
            for (int i = 0; i < current.length - 1; i++) {
                long routeLength = routes[current[i]][current[i+1]];
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
                if (currentLength < min) {
                    // We got a shorter permutation!
                    logger.debug("Length of shortest permutation (so far) "
                            + printPermutation(current) + " :" + currentLength);
                    setSelection(origNodes, current, navigationNodes);
                    min = currentLength;
                }
            }
        }
        p.finish();
    }
    
    private static String printPermutation(int[] permutation) {
        String result = "";
        for (int node: permutation) {
            result += node + " ";
        }
        return result;
    }

    private static void setSelection(List<Selection> original, int[] mapping, List<Selection> navigationNodes) {
        // Reorders the navigationNodes
        if (mapping == null) {
            logger.warn("Got empty mapping, something failed.");
            return;
        }
        logger.info("remapping NavNodes: " + printPermutation(mapping));
        navigationNodes.clear();
        navigationNodes.add(original.get(0));
        for (int i = 0; i < mapping.length; i++) {
            navigationNodes.add(original.get(mapping[i]));
        }
        navigationNodes.add(original.get(original.size() - 1));
        Listener.fireEvent(Listener.NEW_ROUTE, null);
        logger.debug("Old ordering: " + original.toString());
        logger.debug("New ordering: " + navigationNodes.toString());
        return;
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
            if (route != null) {    //TODO is this the best way to handle this situation (I dont think so) // Why not?
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
    
    private static int totalLength(List<Selection> navigationNodes){
        int length = 0;
        Selection prev = navigationNodes.get(0);
        for (Selection navPoint : navigationNodes) {
            if (prev == navPoint) {
                continue;
            }
            logger.debug("Calculating route from " + prev.toString() + " to " + navPoint.toString() + ".");
            if (fromAToB(prev, navPoint) == null) {
                return 0;
            }
            length += fromAToB(prev, navPoint).getLength();
            prev = navPoint;
        }
        return length;
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
            heap.add(new Route(a.getFrom(), (int) (weight2 *  a.getRatio())));
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
                return currentPath;
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
