package kit.route.a.lot.routing;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import kit.route.a.lot.common.IntTuple;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.common.WeightCalculator;
import kit.route.a.lot.controller.State;
import kit.route.a.lot.routing.Route;


public class Router {

    /**
     * Operation calculateRoute
     * 
     * @return List<int>
     */
    public static List<Integer> calculateRoute() {
        List<Integer> route = new LinkedList<Integer>();
        Selection prev = null;
        for (Selection navPoint: State.getInstance().getNavigationNodes()) {
            route.addAll(fromAToB(prev, navPoint));
        }
        return null;
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
        Route bestPath = null;
        RoutingGraph graph = State.getInstance().getRoutingGraph();
        PriorityQueue<Route> heap = new PriorityQueue<Route>(2, new RouteComparator());
        Route currentPath = null;
        if (a == null || b == null) {
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
                return currentPath.toList();
            }
            for (Integer to: graph.getRelevantNeighbors(currentNode, new byte[] {graph.getAreaID(b.getFrom()), graph.getAreaID(b.getTo())})) {
                // Here we add the new paths.
                heap.add(new Route(to, graph.getWeight(currentNode, to), currentPath));
            }
        }
        // No path was found, maybe raise an error?
        return (List<Integer>) null;
    }
}

class RouteComparator<T> implements Comparator<T> {
    // Comperator used in Heap
    @Override
    public int compare(T a, T b) {
        return ((Route) a).length() - ((Route) b).length();
    }
}
