package kit.route.a.lot.common;

import java.awt.Image;
import java.awt.image.BufferedImage;

import kit.route.a.lot.map.rendering.Projection;

import net.java.games.jogl.GL;
import net.java.games.jogl.GLDrawable;
import net.java.games.jogl.GLU;

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
        GL gl = output.getGL();
        GLU glu = output.getGLU();
        int width = image.getWidth(null);
        int height = image.getHeight(null);
        
        int texture = createTexture(gl, glu, (BufferedImage) image);
        
        float x = (position.getLongitude() - topLeft.getLongitude()) / Projection.getZoomFactor(detail);
        float y = (position.getLatitude() - topLeft.getLatitude()) / Projection.getZoomFactor(detail);
        
        
        gl.glPushMatrix();
        
        //gl.glRotatef(30f, 1f, 0f, 0f);         
        gl.glTranslatef(x - width, y - width, -300);
        
        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glBindTexture(GL.GL_TEXTURE_2D, texture);        
        gl.glBegin(GL.GL_QUADS);        
        gl.glTexCoord2f(0f, 0f);
        gl.glVertex3i(0, 0, 0); 
        
        gl.glTexCoord2f(0, 1);
        gl.glVertex3i(0, height, 0);  
        
        gl.glTexCoord2f(1, 1);
        gl.glVertex3i(width, height, 0);   
        
        gl.glTexCoord2f(1, 0);
        gl.glVertex3i(width, 0, 0);       
        
        gl.glTexCoord2f(0, 0);
        gl.glVertex3i(0, 0, 0);
        
        gl.glEnd();        
        gl.glDisable(GL.GL_TEXTURE_2D);
        
        gl.glPopMatrix();
        Textures.delTexture(gl, texture);
    }
    
    public int createTexture(GL gl, GLU glu, BufferedImage image)
    {
        //gl.glShadeModel(GL.GL_SMOOTH);
        gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
        int tex = Textures.genTexture(gl);
        gl.glBindTexture(GL.GL_TEXTURE_2D, tex);
        Textures.makeRGBTexture(gl, glu, image, GL.GL_TEXTURE_2D, false);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP); 
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP); 
        gl.glTexEnvf(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_REPLACE);
        return tex;
    }

}
