package kit.route.a.lot.routing;

public class Route {
    private int to;
    private float weight;
    private Route from;
    
    public Route(int to, float weight) {
        this.to = to;
        this.weight = weight;
    }

    public Route(int to, float weight, Route route) {
        this.to = to;
        this.weight = route.weight + weight;
        from = route;
    }

    public int getNode() {
        return to;
    }

    public List<Integer> toList() {
        LinkedList<Integer> result = new LinkedList();
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

    public float length() {
        if (from == null) {
            return weight;
        } else {
            return weight + from.length();
        }
    }
}
