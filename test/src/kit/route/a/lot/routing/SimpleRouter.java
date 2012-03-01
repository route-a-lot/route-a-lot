package kit.route.a.lot.routing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

import kit.route.a.lot.common.Selection;
import kit.route.a.lot.controller.State;


public class SimpleRouter {
    

    public static List<Integer> calculateRoute(List<Selection> navigationNodes) {
        List<Integer> result = new ArrayList<Integer>();
        
        Selection prev = navigationNodes.get(0);
        for (Selection navPoint : navigationNodes) {
            if (prev == navPoint) {
                continue;
            }
            Route route = fromAToB(prev, navPoint);
            if (route != null) {    // cause Router does this too, otherwise we would get different results
                result.addAll(route.toList());
                result.add(-1);
                prev = navPoint;
            } else {
                return new ArrayList<Integer>();
            }
        }
        
        return result;
    }
    
    private static Route fromAToB(Selection a, Selection b) {
        if (a == null || b == null) {
            return null;
        }
        List<Integer> newRoute = new ArrayList<Integer>();
        
        RoutingGraph graph = State.getInstance().getLoadedGraph();
        
        PriorityQueue<Route> heap = new PriorityQueue<Route>(2, new RouteComparator<Route>());
        
        if (a == null || b == null) {
            return null;
        }
        
        boolean[] seen = new boolean[graph.getIDCount()];
        Arrays.fill(seen, false);
        
        int weight1 = graph.getWeight(a.getFrom(), a.getTo());
        int weight2 = graph.getWeight(a.getTo(), a.getFrom());
        if (weight1 != -1 && weight2 != -1) {
            int weightFrom = (int) (graph.getWeight(a.getFrom(), a.getTo()) * a.getRatio());
            int weightTo = (int) (graph.getWeight(a.getFrom(), a.getTo()) * (1 - a.getRatio()));
            if (weightFrom == weight1) {
                weightTo++;
            } else if (weightTo == weight1){
                weightFrom++;
            }
            heap.add(new Route(a.getFrom(), weightFrom));
            heap.add(new Route(a.getTo(), weightTo));
        } else if (weight1 != -1) {
            heap.add(new Route(a.getTo(), (int) (weight1 * (1 - a.getRatio()))));
        } else {
            heap.add(new Route(a.getFrom(), (int) (weight2 * (a.getRatio()))));
        }
        
        Route currentPath;
        int currentNode;
        int selectionWeight;
        
        while ((currentPath = heap.poll()) != null) {
            currentNode = currentPath.getNode();
            
            if (currentNode > 0 && seen[currentNode]) {
                continue;   //we have found a shorter way to this node
            }
            
            if (currentNode != -1) {
                seen[currentNode] = true;
            }
            
            if (currentNode == b.getTo()) {
                selectionWeight = graph.getWeight(b.getTo(), b.getFrom());
                if (selectionWeight > 0 || b.getRatio() > 0.99) {
                    heap.add(new Route(-1, (int) (1 - b.getRatio()) * selectionWeight, currentPath));
                }
            } else if (currentNode == b.getFrom()) {
                selectionWeight = graph.getWeight(b.getFrom(), b.getTo());
                if (selectionWeight > 0 || b.getRatio() > 0.01) {
                    heap.add(new Route(-1, ((int) b.getRatio() * selectionWeight), currentPath));
                }
            } else if (currentNode == -1) {
                // This is the shortest path.
                newRoute.addAll(currentPath.getRoute().toList());
                return currentPath.getRoute();
            }
            
            for (Integer to : graph.getAllNeighbors(currentNode)) {
                selectionWeight = graph.getWeight(currentNode, to);
                if (selectionWeight > 0) {
                    heap.add(new Route(to, selectionWeight, currentPath));
                }
            }
            
        }
        
        return  null;
    }
    
    public static List<Selection> optimizeRouteWith4Targets(List<Selection> navigationNodes) {
        //int weight1 = getRouteLength(Router.calculateRoute(navigationNodes), navigationNodes);
        List<Selection> secSol = new ArrayList<Selection>();
        secSol.add(navigationNodes.get(0));
        secSol.add(navigationNodes.get(2));
        secSol.add(navigationNodes.get(1));
        secSol.add(navigationNodes.get(3));
        
        Route r01 = fromAToB(navigationNodes.get(0), navigationNodes.get(1));
        Route r02 = fromAToB(navigationNodes.get(0), navigationNodes.get(2));
        Route r12 = fromAToB(navigationNodes.get(1), navigationNodes.get(2));
        Route r21 = fromAToB(navigationNodes.get(2), navigationNodes.get(1));
        Route r13 = fromAToB(navigationNodes.get(1), navigationNodes.get(3));
        Route r23 = fromAToB(navigationNodes.get(2), navigationNodes.get(3));
        
        if (r02 == null || r21 == null || r13 == null) {
            return navigationNodes;
        } else if (r12 == null || r01 == null || r23 == null) {
            return secSol;
        }
        
        int a01 = r01.getLength();
        int a02 = r02.getLength();
        int a12 = r12.getLength();
        int a21 = r21.getLength();
        int a13 = r13.getLength();
        int a23 = r23.getLength();
        
        int weight1 = a01+a12+a23;
        int weight2 = a02+a21+a13;

        return (weight2 < weight1) ? secSol : navigationNodes;
    }
}
