package kit.route.a.lot.map.rendering;

import javax.media.opengl.GL;
import org.apache.log4j.Logger;

import kit.route.a.lot.common.Context3D;
import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Context;
import kit.route.a.lot.common.Frustum;
import kit.route.a.lot.common.Projection;
import kit.route.a.lot.common.ProjectionFactory;
import kit.route.a.lot.common.Util;
import kit.route.a.lot.controller.State;
import kit.route.a.lot.heightinfo.IHeightmap;
import kit.route.a.lot.map.rendering.Renderer;

public class Renderer3D extends Renderer {

    private static final float HEIGHT_SCALE_FACTOR = 1f;
    private static final float VIEW_HEIGHT_ADAPTION = 0.2f;
    private static Logger logger = Logger.getLogger(Renderer3D.class);
    private float viewHeight = Float.NEGATIVE_INFINITY;
    
    /**
     * Renders a map viewing rectangle in three dimensional form,
     * using height data and perspective projection in the process.
     * 
     * @param detail level of detail of the map view
     * @param topLeft north western corner of the viewing rectangle
     * @param bottomRight south eastern corner of the viewing rectangle
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
        int lat = (int) Math.floor(center.getLatitude() / tileDim) - 1;
        int lon = (int) Math.floor(center.getLongitude() / tileDim);
        
        GL gl = context3D.getGL();
        gl.glScalef(1, 1, HEIGHT_SCALE_FACTOR * (detail + 1));
        Projection projection = ProjectionFactory.getProjectionForCurrentMap();
        IHeightmap heightmap = State.getInstance().getLoadedHeightmap();
        float centerHeight = heightmap.getHeight(projection.localCoordinatesToGeoCoordinates(center));
        viewHeight = (viewHeight == Float.NEGATIVE_INFINITY) ? centerHeight
                : Util.interpolate(viewHeight, centerHeight, VIEW_HEIGHT_ADAPTION);
        gl.glTranslatef(0, 0, -viewHeight);      
        Frustum frustum = new Frustum(gl);
        renderTile(gl, frustum, lon, lat, tileDim, detail);
        int radius = 1;
        boolean found = true;
        while (found) {
            found = false;
            int y1 = lat-radius;
            int y2 = lat+radius;
            int x1 = lon-radius;
            int x2 = lon+radius;
            for (int x = x1; x <= x2; x++) {
                if (renderTile(gl, frustum, x, y1, tileDim, detail)) {
                    found = true;
                }
                if (renderTile(gl, frustum, x, y2, tileDim, detail)) {
                    found = true;
                }     
            }
            for (int y = y1+1; y < y2; y++) {
                if (renderTile(gl, frustum, x1, y, tileDim, detail)) {
                    found = true;
                }
                if (renderTile(gl, frustum, x2, y, tileDim, detail)) {
                    found = true;
                }       
            }
            radius++;
        }      
    }
       
    private boolean renderTile(GL gl, Frustum frustum, int x, int y, int tileDim, int detail) {
        Coordinates topLeft = new Coordinates(y * tileDim, x * tileDim);
        Tile3D tile = (Tile3D) cache.queryCache(topLeft, detail);
        if (tile == null) {
            tile = new Tile3D(topLeft, tileDim, detail);
            if (tile.isInFrustum(frustum)) {
                tile.prerender();
                Tile3D deletedTile = (Tile3D) cache.addToCache(tile);
                if (deletedTile != null) {
                    deletedTile.freeResources(gl);
                }
            } else {
                return false;
            }
        }
        if (tile.isInFrustum(frustum)) {
            tile.render(gl);  
            return true;
        }
        return false;
    }
       
}
