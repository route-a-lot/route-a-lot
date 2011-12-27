package kit.route.a.lot.routing;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import kit.route.a.lot.common.IntTuple;
import kit.route.a.lot.common.Selection;
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
        RoutingGraph graph = State.getRoutingGraph();
        PriorityQueue<Route> heap = new PriorityQueue<Route>(2, new RouteComparator());
        Route currentPath = null;
        if (a == null || b == null) {
            return (List<Integer>) null;
        }
        // intialize heap
        heap.add(new Route(a.getFrom(), (int) (graph.getWeight(a.getFrom(), a.getTo()) * a.getRatio())));
        heap.add(new Route(a.getTo(), (int) (graph.getWeight(a.getTo(), a.getFrom()) * (1 / a.getRatio()))));
        // start the lame calculating.
        while (heap.peek() != null) {
            currentPath = heap.poll();
            if (currentPath.getNode() == b.getTo() || currentPath.getNode() == b.getFrom()) {
                break;
            }
            for (Integer to: graph.getRelevantNeighbors(currentPath.getNode(), graph.getAreaID(currentPath.getNode()))) {
                heap.add(new Route(to, graph.getWeight(currentPath.getNode(), to), currentPath));
            }
        }
        return currentPath.toList();
    }
}

class RouteComparator<T> implements Comparator<T> {
    @Override
    public int compare(T a, T b) {
        return ((Route) a).length() - ((Route) b).length();
    }
}
