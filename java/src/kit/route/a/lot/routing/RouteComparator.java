package kit.route.a.lot.routing;

import java.util.Comparator;

import kit.route.a.lot.routing.Route;




class RouteComparator<T> implements Comparator<T> {
    // Comperator used in Heap
    @Override
    public int compare(T a, T b) {
        return ((Route) a).length() - ((Route) b).length();
    }
}
