package kit.route.a.lot.common;

import kit.route.a.lot.controller.State;


public class ProjectionFactory {
    
    public static Projection getNewProjection(Coordinates topLeft, Coordinates bottomRight) {
        float scale = 5E-6f;
        return new MercatorProjection(topLeft, scale);
//        return new SimpleProjection(topLeft, bottomRight, 21000, 12000);
    }
    
    public static Projection getProjectionForCurrentMap() {
        Coordinates topLeft = State.getInstance().getLoadedMapInfo().getGeoTopLeft();
        return getNewProjection(topLeft, null);
//        Coordinates bottomRight = State.getInstance().getLoadedMapInfo().getGeoBottomRight();
//        return getNewProjection(topLeft, bottomRight);
    }
    
}
