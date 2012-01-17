package kit.route.a.lot.routing;

import java.util.ArrayList;
import java.util.List;

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

    public Route() {
        // Dummy
        to = 0;
        weight = 0;
        from = this;
    }

    public int getNode() {
        return to;
    }
    
    public Route getRoute() {
        if (from == null) {
            // Quick 'n' dirty
            return this;
        }
        return from;
    }

    public List<Integer> toList() {
        List<Integer> result = new ArrayList<Integer>();
        result.add((Integer) to);
        if (from == null) {
            return result;
        }
        else {
            List<Integer> tmp = from.toList();
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
    
    public Route join(Route route) {
        // joins two routes
        Route result = route.copy();
        Route pos;
        for (pos = result; pos != null; pos = pos.from) {
            pos.weight += weight;
        }
        pos.from = this;
        return result;
    }

    public Route copy() {
        // creates a deep copy
        if (from != null) {
            return new Route (to, weight, from.copy());
        } else {
            return new Route (to, weight, null);
        }
    }
}
