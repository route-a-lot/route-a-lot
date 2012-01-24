package kit.route.a.lot.common;

import java.awt.Image;

import kit.route.a.lot.map.rendering.Projection;

import net.java.games.jogl.GL;
import net.java.games.jogl.GLDrawable;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class Context3D extends Context {
    
    private static Logger logger = Logger.getLogger(Context3D.class);
    static {
        logger.setLevel(Level.INFO);
    }
    
    private GLDrawable output;
 
    public Context3D(Coordinates topLeft, Coordinates bottomRight, GLDrawable surface) {
        super(topLeft, bottomRight);
        output = surface;
    }
    
    /*public Context3D(Coordinates topLeft, int width, int height, float scale, GL surface) {
    super(width, height, topLeft, null);
    output = surface;
    projection = Projection.getNewProjection(topLeft);
    Coordinates localTopLeft = projection.geoCoordinatesToLocalCoordinates(topLeft);
    Coordinates localBottomRight = new Coordinates(localTopLeft.getLatitude() - height,
                                                   localTopLeft.getLongitude() + width);
    bottomRight = projection.localCoordinatesToGeoCoordinates(localBottomRight);
    }*/

    @Override
    public void drawImage(Coordinates position, Image image, int detail) {
        
        float x = (position.getLongitude() - topLeft.getLongitude()) / Projection.getZoomFactor(detail);
        float y = (position.getLatitude() - topLeft.getLatitude()) / Projection.getZoomFactor(detail);
        
        GL gl = output.getGL();
        gl.glPushMatrix();
        
        gl.glRotatef(45f, 1f, 0f, 0f);
        
        gl.glScalef(1f/Projection.getZoomFactor(detail), 1f/Projection.getZoomFactor(detail), 1f);
        
        gl.glTranslatef(x-215, y-800, -150);
        
        gl.glBegin(GL.GL_LINE_STRIP);
        gl.glVertex3f(0f, 0f, -1.5f);
        gl.glVertex3f(0f, 200f, -1.5f);
        gl.glVertex3f(200f, 200f, -1.5f);
        gl.glVertex3f(200f, 0f, -1.5f);
        gl.glVertex3f(0f, 0f, -1.5f);
        gl.glEnd();
        
        gl.glPopMatrix();
    }

}
