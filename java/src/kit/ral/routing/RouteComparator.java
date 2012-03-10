package kit.ral.routing;

import java.util.Comparator;

import kit.ral.routing.Route;


class RouteComparator<T> implements Comparator<T> {

    // Comperator used in Heap
    @Override
    public int compare(T a, T b) {
        return ((Route) a).getLength() - ((Route) b).getLength();
    }
}
