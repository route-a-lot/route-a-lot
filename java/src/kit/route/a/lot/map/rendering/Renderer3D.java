package kit.route.a.lot.map.rendering;

import javax.media.opengl.GL;

import kit.route.a.lot.common.Context3D;
import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Context;
import kit.route.a.lot.common.Projection;
import kit.route.a.lot.controller.State;
import kit.route.a.lot.heightinfo.IHeightmap;
import kit.route.a.lot.map.rendering.Renderer;

public class Renderer3D extends Renderer {

    /**
     * Renders a map viewing rectangle in three dimensional form,
     * using height data and perspective projection in the process.
     * 
     * @param detail level of detail of the map view
     * @param topLeft northwestern corner of the viewing rectangle
     * @param bottomRight southeastern corner of the viewing rectangle
     * @param renderingContext an OpenGL rendering context
     */
    public void render(Context context, int detail) {
        IHeightmap heightData = State.getInstance().getLoadedHeightmap();
        Projection projection = Projection.getProjectionForCurrentMap();
        
        Coordinates topLeft = context.getTopLeft();
        Coordinates bottomRight = context.getBottomRight();
        
        GL gl = ((Context3D) context).getGL();
        gl.glDisable(GL.GL_TEXTURE_2D);
        
        
        
        float stepSize = (bottomRight.getLongitude() - topLeft.getLongitude()) / 100;
        
        //gl.glTranslatef(topLeft.getLongitude(), topLeft.getLatitude(), 0);
        //gl.glTranslatef(0,0,-100);
        Coordinates pos = new Coordinates();
        for (int x = 0; x < 100; x++) {
            pos.setLongitude(topLeft.getLongitude() + x * stepSize);
            gl.glBegin(GL.GL_TRIANGLE_STRIP);
            for (int y = 0; y < 100; y++) {
                pos.setLatitude(topLeft.getLatitude() + y * stepSize);
                int h = heightData.getHeight(projection.localCoordinatesToGeoCoordinates(pos));
                gl.glColor3b((byte)(h%256), (byte)(h%256), (byte)(h%256));
                gl.glVertex3f(pos.getLongitude(), pos.getLatitude(), 0);
                pos.setLongitude(pos.getLongitude() + stepSize);
                h = heightData.getHeight(projection.localCoordinatesToGeoCoordinates(pos));
                gl.glColor3b((byte)(h%256), (byte)(h%256), (byte)(h%256));
                gl.glVertex3f(pos.getLongitude(), pos.getLatitude(), 0);
            }
            gl.glEnd();
        }
    }
}
