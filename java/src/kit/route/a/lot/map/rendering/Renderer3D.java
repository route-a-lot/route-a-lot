package kit.route.a.lot.map.rendering;

import javax.media.opengl.GL;
import org.apache.log4j.Logger;

import kit.route.a.lot.common.Context3D;
import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Context;
import kit.route.a.lot.common.Frustum;
import kit.route.a.lot.common.Projection;
import kit.route.a.lot.common.ProjectionFactory;
import kit.route.a.lot.controller.State;
import kit.route.a.lot.map.rendering.Renderer;

public class Renderer3D extends Renderer {

    //private float lastFloor = 0, lastCeil = 100;

    private static final float HEIGHT_SCALE_FACTOR = 1f;
    private static Logger logger = Logger.getLogger(Renderer3D.class);
    
    /**
     * Renders a map viewing rectangle in three dimensional form,
     * using height data and perspective projection in the process.
     * 
     * @param detail level of detail of the map view
     * @param topLeft northwestern corner of the viewing rectangle
     * @param bottomRight southeastern corner of the viewing rectangle
     * @param renderingContext an OpenGL rendering context
     */
    @Override
    public void render(Context context, int detail) {
        int tileDim = (int) (BASE_TILEDIM * Projection.getZoomFactor(detail));
        if (tileDim < 0) {
            logger.error("tileDim < 0 => seems like an overflow");
        }
        Context3D context3D = (Context3D) context;
        Coordinates center = context.getBottomRight().clone().add(context.getTopLeft()).scale(0.5f);
        int lat = (int) Math.floor(center.getLatitude() / tileDim);
        int lon = (int) Math.floor(center.getLongitude() / tileDim);
        
        GL gl = context3D.getGL();
        gl.glScalef(1, 1, HEIGHT_SCALE_FACTOR * (detail + 1));
        Projection projection = ProjectionFactory.getProjectionForCurrentMap();
        float centerHeight = State.getInstance().getLoadedHeightmap().getHeight(projection.localCoordinatesToGeoCoordinates(center));
        gl.glTranslatef(0, 0, -centerHeight);      
        Frustum frustum = new Frustum(gl);
        
        int n = 1;
        renderTile(context3D, frustum, lon, lat, tileDim, detail);
        int radius = 1;
        boolean found = true;
        while (found) {
            found = false;
            int y1 = lat-radius;
            int y2 = lat+radius;
            int x1 = lon-radius;
            int x2 = lon+radius;
            for (int x = x1; x <= x2; x++) {
                if (renderTile(context3D, frustum, x, y1, tileDim, detail)) {
                    found = true; n++;
                }
                if (renderTile(context3D, frustum, x, y2, tileDim, detail)) {
                    found = true; n++;
                }     
            }
            for (int y = y1+1; y < y2; y++) {
                if (renderTile(context3D, frustum, x1, y, tileDim, detail)) {
                    found = true; n++;
                }
                if (renderTile(context3D, frustum, x2, y, tileDim, detail)) {
                    found = true; n++;
                }       
            }
            radius++;
            if (n > 35) return; // TODO remove this
        }
        //logger.info("Render output: " + n + " Tiles.");
              
    }
       
    private boolean renderTile(Context3D context, Frustum frustum, int x, int y, int tileDim, int detail) {
        Coordinates topLeft = new Coordinates(y * tileDim, x * tileDim);
        Tile3D currentTile = (Tile3D) prerenderTile(topLeft, tileDim, detail);
        return (currentTile != null) && currentTile.render(context.getGL(), frustum);
    }
    
    @Override
    protected Tile createTile(Coordinates topLeft, float tileDim, int detail) {
        return new Tile3D(topLeft, tileDim, detail);
    }
       
}
