package kit.route.a.lot.routing;

import java.util.LinkedList;

public class Route {
    /*
     * 
     * Basically, a "Route" is an inverted tree of nodes and weights with the start-id as the root.
     * This greatly reduces Heap-size as a lot of routes share a great part of their way
     * 
     */
    private int to;
    private int weight;
    private Route from;
    
    public Route(int to, int weight) {
        this.to = to;
        this.weight = weight;
    }

    public Route(int to, int weight, Route route) {
        this.to = to;
        this.weight = route.weight + weight;
        from = route;
    }

    public int getNode() {
        return to;
    }

    public LinkedList<Integer> toList() {
        LinkedList<Integer> result = new LinkedList<Integer>();
        result.add((Integer) to);
        if (from == null) {
            return result;
        }
        else {
            LinkedList<Integer> tmp = from.toList();
            tmp.addAll(result);
            return tmp;
        }
    }

    public int length() {
        if (from == null) {
            return weight;
        } else {
            return weight + from.length();
        }
    }
}