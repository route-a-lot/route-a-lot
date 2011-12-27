package kit.route.a.lot.routing;

import java.util.LinkedList;

public class Route {
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
