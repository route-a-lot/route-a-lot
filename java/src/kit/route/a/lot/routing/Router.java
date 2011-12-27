package kit.route.a.lot.routing;

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
        Route bestPath = null;
        RoutingGraph graph = State.getInstance().getRoutingGraph();
        PriorityQueue<Route> heap = new PriorityQueue<Route>(2, new RouteComparator());
        Route currentPath = null;
        if (a == null || b == null) {
            return (List<Integer>) null;
        }
        // Initialize heap
        heap.add(new Route(a.getFrom(), (int) (graph.getWeight(a.getFrom(), a.getTo()) * a.getRatio())));
        heap.add(new Route(a.getTo(), (int) (graph.getWeight(a.getTo(), a.getFrom()) * (1 / a.getRatio()))));
        // start the lame calculating.
        while (heap.peek() != null) {
            // currentPath ALWAYS contains the shortest path to currentPath.getNode() (with regards to Arc-Flags).
            currentPath = heap.poll();
            // the target requires a special node, since both paths to it have to be put on the heap.
            // (everything else would be a pain in the ass)
            if (currentPath.getNode() == b.getTo()) {
                // We can't return now, as there might be a shorter way via b.getFrom().
                heap.add(new Route(-1, (int) (1/b.getRatio()) * WeightCalculator.calcWeight(b), currentPath));
            } else if (currentPath.getNode() == b.getFrom()) {
                heap.add(new Route(-1, (int) b.getRatio() * WeightCalculator.calcWeight(b), currentPath));
            } else if (currentPath.getNode() == -1) {
                return currentPath.toList();
            }
            for (Integer to: graph.getRelevantNeighbors(currentPath.getNode(), graph.getAreaID(currentPath.getNode()))) {
                // Note: we don't check if for the given ID there already exists a (shorter) path to it,
                // maybe we should, as it may greatly reduce the size of the heap.
                // (I don't know how that scales with Arc-Flags)
                heap.add(new Route(to, graph.getWeight(currentPath.getNode(), to), currentPath));
            }
        }
        return null;
    }
}

class RouteComparator<T> implements Comparator<T> {
    @Override
    public int compare(T a, T b) {
        return ((Route) a).length() - ((Route) b).length();
    }
}
