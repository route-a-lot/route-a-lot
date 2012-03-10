package kit.route.a.lot.common.projection;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.controller.State;


public class ProjectionFactory {
    private static final float SCALE = 5E-6f;
    
    public static Projection getNewProjection(Coordinates topLeft, Coordinates bottomRight) {
        return new MercatorProjection(topLeft, SCALE);
        // return new SimpleProjection(topLeft, bottomRight, 21000, 12000);
    }
    
    /**
     * Returns the projection for the map that is currently loaded in
     * State.getInstance().getLoadedMapInfo().
     * @return the projection for the current map
     */
    public static Projection getCurrentProjection() {
        Coordinates topLeft = State.getInstance().getMapInfo().getGeoTopLeft();
        return getNewProjection(topLeft, null);
        // Coordinates bottomRight = State.getInstance().getLoadedMapInfo().getGeoBottomRight();
        // return getNewProjection(topLeft, bottomRight);
    }
    
}
