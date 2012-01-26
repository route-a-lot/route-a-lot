package kit.route.a.lot.map.rendering;

import java.awt.image.BufferedImage;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import kit.route.a.lot.common.Context2D;
import kit.route.a.lot.common.Context3D;
import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Context;
import kit.route.a.lot.common.Projection;
import kit.route.a.lot.common.Textures;
import kit.route.a.lot.controller.State;
import kit.route.a.lot.heightinfo.IHeightmap;
import kit.route.a.lot.map.rendering.Renderer;

public class Renderer3D extends Renderer {

    private static final float HEIGHT_STEPS = 10;
    private static Logger logger = Logger.getLogger(Renderer3D.class);
    static {
        logger.setLevel(Level.INFO);
    }
    
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
        Coordinates topLeft = context.getTopLeft();
        Coordinates bottomRight = context.getBottomRight();
        float width = context.getWidth();
        float height = context.getHeight();
        
        // render usual 2D to a new image. output is a bit larger than strict screen size
        BufferedImage img = new BufferedImage((int) (width / Projection.getZoomFactor(detail)), 
                                              (int) (height / Projection.getZoomFactor(detail)),
                                              BufferedImage.TYPE_INT_RGB);           
        Context context2D = new Context2D(topLeft, bottomRight, img.createGraphics());
        /*BufferedImage img = new BufferedImage((int) (width / Projection.getZoomFactor(detail) * 1.6), 
                (int) (height / Projection.getZoomFactor(detail) * 1.6),
                BufferedImage.TYPE_INT_RGB);           
        Context context2D = new Context2D(new Coordinates(topLeft.getLongitude() - 0.3f*width, topLeft.getLatitude() - 0.3f*height),
            new Coordinates(bottomRight.getLongitude() + 0.3f*width, bottomRight.getLatitude() + 0.3f*height),
            img.createGraphics());*/
        super.render(context2D, detail);
  
        IHeightmap heightData = State.getInstance().getLoadedHeightmap();
        Projection projection = Projection.getProjectionForCurrentMap();
        GL gl = ((Context3D) context).getGL();
        
        // move camera up so that it won't intersect hills, TODO doesn't seem to work    
        Coordinates pos = new Coordinates((float)(bottomRight.getLongitude() + topLeft.getLongitude()) * 0.5f,
                                          (float)(bottomRight.getLatitude() + topLeft.getLatitude()) * 0.5f);
        gl.glTranslatef(0, 0, -heightData.getHeight(projection.localCoordinatesToGeoCoordinates(pos))*10);
        
        // create texture and render height mesh
        gl.glEnable(GL.GL_TEXTURE_2D);        
        int texture = createTexture(gl, new GLU(), img);
        gl.glBindTexture(GL.GL_TEXTURE_2D, texture);   
        float stepSize = height / HEIGHT_STEPS;
        float xSteps = width / stepSize + 1;       
        for (int x = 0; x < xSteps; x++) {       
            gl.glBegin(GL.GL_TRIANGLE_STRIP);
            for (int y = 0; y < HEIGHT_STEPS; y++) {
                pos.setLongitude(topLeft.getLongitude() + x * stepSize);
                pos.setLatitude(topLeft.getLatitude() + y * stepSize);
                int h = heightData.getHeight(projection.localCoordinatesToGeoCoordinates(pos));
                gl.glColor3b((byte)(h%256), (byte)(h%256), (byte)(h%256));
                gl.glTexCoord2f(x / xSteps, y / HEIGHT_STEPS);
                gl.glVertex3f(pos.getLongitude(), pos.getLatitude(), h*10);
                pos.setLongitude(pos.getLongitude() + stepSize);
                h = heightData.getHeight(projection.localCoordinatesToGeoCoordinates(pos));
                gl.glTexCoord2f((x + 1) / xSteps, y / HEIGHT_STEPS);
                gl.glColor3b((byte)(h%256), (byte)(h%256), (byte)(h%256));
                gl.glVertex3f(pos.getLongitude(), pos.getLatitude(), h*10);
            }
            gl.glEnd();
        }
        gl.glDisable(GL.GL_TEXTURE_2D);
        Textures.delTexture(gl, texture);
    }
    
    public int createTexture(GL gl, GLU glu, BufferedImage image)
    {
        int tex = Textures.genTexture(gl);
        gl.glBindTexture(GL.GL_TEXTURE_2D, tex);
        Textures.makeRGBTexture(gl, glu, image, GL.GL_TEXTURE_2D, false);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP); 
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP); 
        gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_MODULATE);
        return tex;
    }
}
