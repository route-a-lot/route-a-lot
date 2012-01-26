package kit.route.a.lot.common;

import kit.route.a.lot.controller.State;


public class ProjectionFactory {
    
    public static Projection getNewProjection(Coordinates topLeft) {
        float scale = 5E-6f;
        return new MercatorProjection(topLeft, scale);
    }
    
    public static Projection getProjectionForCurrentMap() {
        Coordinates topLeft = State.getInstance().getLoadedMapInfo().getGeoTopLeft();
        return getNewProjection(topLeft);
    }
    
}
