package kit.route.a.lot.routing;

import java.util.Arrays;
import java.util.PriorityQueue;

import kit.route.a.lot.controller.State;


public class Precalculator {

    /**
     * Operation precalculate
     * 
     * @return
     * 
     * @return
     */
    
    private RoutingGraph graph, inverted;
    
    public void precalculate() {
        graph = State.getRoutingGraph();
        inverted = graph.getInverted();
        doAreas();
        for (int i = 0; i < graph.getIDCount(); i++) {
            // I suppose we could run some parallel.
            createFlags(i);
        }
        inverted = null; // Let the garbage-collector take care of it
        return;
    }

    private void createFlags(int node) {
        // On further notes, see Router.fromAToB()
        boolean[] seen = new boolean[graph.getIDCount()];
        Route currentPath = null;
        PriorityQueue<Route> heap = new PriorityQueue<Route>(2, new RouteComparator());
        Arrays.fill(seen, false);
        heap.add(new Route(node, 0));
        byte area = graph.getAreaID(node);
        while (heap.peek() != null) {
            currentPath = heap.poll();
            if (seen[currentPath.getNode()]) {
                // We already know a (shorter) path from that node, so ignore it.
                continue;
            }
            seen[currentPath.getNode()] = true;
            graph.setArcFlag(currentPath.getRoute().getNode(), currentPath.getNode(), area);
            for (Integer to: inverted.getAllNeighbors(currentPath.getNode())) {
                heap.add(new Route(to, graph.getWeight(currentPath.getNode(), to), currentPath));
            }
        }
    }

    private void doAreas() {
        // Divide nodes in areas.
        // TODO Auto-generated method stub
        
    }
}
