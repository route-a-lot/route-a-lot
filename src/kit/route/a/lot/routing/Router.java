package kit.route.a.lot.routing;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import kit.route.a.lot.common.IntTuple;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.controller.State;


public class Router {

    /**
     * Operation calculateRoute
     * 
     * @return List<int>
     */
    public List<Integer> calculateRoute() {
        List<Integer> route = new LinkedList<Integer>();
        Selection prev;
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
    public List<Integer> calculateOptimizedRoute() {
        return calculateRoute();
    }

    private List<Integer> fromAToB(Selection a, Selection b) {
        PriorityQueue<Path> heap = new PriorityQueue<Path>(2, new RouteComparator());
        Route currentPath;
        if (a == null || b == null) {
            return new List<Integer>;
        }
        // intialize heap
        heap.add(new Route(a.getFrom(), graph.getWeight(a.getFrom(), a.getTo()) * a.getRatio()));
        heap.add(new Route(a.getTo(), graph.getWeight(a.getTo(), a.getFrom()) * (1 / a.getRatio())));
        // start the lame calculating.
        while (heap.peek != null) {
            currentPath = heap.poll();
            if (currentPath.getNode() == b.getTo() || currentPath.getNode() == b.getFrom()) {
                break;
            }
            for (IntTuple edge: graph.getRelevantNeighbors(currentPath.getNode(), graph.getAreaId(currentPath.getNode()))) {
                heap.add(new Route(edge.getLast(), graph.getWeight(edge.getFirst(), edge.getLast()), currentPath));
            }
        }
        return currentPath.toList();
    }
}

class RouteComparator<T> implements Comparator<T> {
    public int compare(Route a, Route b) {
        return a.length() - b.length();
    }
}
