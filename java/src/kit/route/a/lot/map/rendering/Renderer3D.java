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
import kit.route.a.lot.common.ProjectionFactory;
import kit.route.a.lot.common.Textures;
import kit.route.a.lot.common.Util;
import kit.route.a.lot.controller.State;
import kit.route.a.lot.heightinfo.IHeightmap;
import kit.route.a.lot.map.rendering.Renderer;

public class Renderer3D extends Renderer {

    private static final float HEIGHT_STEPS = 100;
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
        int factor = Projection.getZoomFactor(detail);
        if (factor < 0) {
            return;
        }
        BufferedImage img = new BufferedImage((int) (context.getWidth() / factor), 
                                              (int) (context.getHeight() / factor),
                                              BufferedImage.TYPE_INT_RGB);           
        Context context2D = new Context2D(topLeft, bottomRight, img.createGraphics());
        /*BufferedImage img = new BufferedImage((int) (width / Projection.getZoomFactor(detail) * 1.6), 
                (int) (height / Projection.getZoomFactor(detail) * 1.6),
                BufferedImage.TYPE_INT_RGB);           
        Context context2D = new Context2D(new Coordinates(topLeft.getLongitude() - 0.3f*width, topLeft.getLatitude() - 0.3f*height),
            new Coordinates(bottomRight.getLongitude() + 0.3f*width, bottomRight.getLatitude() + 0.3f*height),
            img.createGraphics());*/
        //super.render(context2D, detail);
        
        IHeightmap heightData = State.getInstance().getLoadedHeightmap();
        Projection projection = ProjectionFactory.getProjectionForCurrentMap();
        GL gl = ((Context3D) context).getGL();
        //int texture = createTexture(gl, new GLU(), img);
        
        
        // move camera up so that it won't intersect hills  
        Coordinates pos = new Coordinates((float)(bottomRight.getLongitude() + topLeft.getLongitude()) * 0.5f,
                                          (float)(bottomRight.getLatitude() + topLeft.getLatitude()) * 0.5f);
        gl.glTranslatef(0, 0, -heightData.getHeight(projection.localCoordinatesToGeoCoordinates(pos))*10);
        
        
        // delete this block for rending the height grid view dependent instead of map dependent
        int tileDim = (int) (200 * Projection.getZoomFactor(detail));
        if (tileDim < 0) {
            logger.error("tileDim < 0 => seems like an overflow");
        }
        float texXOffset = topLeft.getLongitude();
        float texYOffset = topLeft.getLatitude();
        topLeft.setLongitude(tileDim * ((int) Math.floor(topLeft.getLongitude() / tileDim) - 1));
        topLeft.setLatitude(tileDim * ((int) Math.floor(topLeft.getLatitude() / tileDim) - 1));   
        texXOffset = (texXOffset - topLeft.getLongitude()) / context.getWidth();
        texYOffset = (texYOffset - topLeft.getLatitude()) / context.getHeight();//*/
        
        
        // create texture and render height mesh
             
        //gl.glEnable(GL.GL_TEXTURE_2D);   
        //gl.glBindTexture(GL.GL_TEXTURE_2D, texture);     
        float stepSize = context.getHeight() / HEIGHT_STEPS;
        float xSteps = context.getWidth() / stepSize + 1;       
        float[] normal = new float[3];
        float[] vertex1 = new float[] {0f, 0f, 0f};
        float[] vertex2 = new float[] {0f, 0f, 0f};
        float[] vertex;
        
        gl.glColor3f(1,1,1);
        
        for (int x = 0; x < xSteps; x++) {       
            gl.glBegin(GL.GL_TRIANGLE_STRIP);
            for (int y = 0; y < HEIGHT_STEPS; y++) {
                pos.setLatitude(topLeft.getLatitude() + y * stepSize);
                pos.setLongitude(topLeft.getLongitude() + x * stepSize); 
                //gl.glTexCoord2f(x / xSteps, y / HEIGHT_STEPS);  
                
                float h = heightData.getHeight(projection.localCoordinatesToGeoCoordinates(pos)) * 10;
                float color = ((h > 3000) ? 3000 : h) / 3000f;
                //gl.glColor3f(color, color, color);
                vertex = new float[] {pos.getLongitude(), pos.getLatitude(), h};
                Util.getFaceNormal(normal, vertex1, vertex2, vertex);
                //gl.glNormal3f(normal[0], normal[1], normal[2]);
                gl.glNormal3f(0,1,1);
                gl.glVertex3f(pos.getLongitude(), pos.getLatitude(), h);
                vertex2 = vertex1;
                vertex1 = vertex;
                
                pos.setLongitude(pos.getLongitude() + stepSize);
                //gl.glTexCoord2f((x + 1) / xSteps, y / HEIGHT_STEPS) ;
                
                h = heightData.getHeight(projection.localCoordinatesToGeoCoordinates(pos)) * 10;
                color = ((h > 3000) ? 3000 : h) / 3000f;
                //gl.glColor3f(color, color, color);
                vertex = new float[] {pos.getLongitude(), pos.getLatitude(), h};
                Util.getFaceNormal(normal, vertex1, vertex, vertex2);
                //gl.glNormal3f(normal[0], normal[1], normal[2]);
                gl.glNormal3f(1,0,1);
                gl.glVertex3f(pos.getLongitude(), pos.getLatitude(), h);
                vertex2 = vertex1;
                vertex1 = vertex;
            }
            gl.glEnd();
        }
        //gl.glDisable(GL.GL_TEXTURE_2D);
        //Textures.delTexture(gl, texture);
        
        
        // Code for showing the grid.
        /*
        gl.glColor3f(1,0,0);     
        for (int x = 0; x < xSteps; x++) {       
            for (int y = 0; y < HEIGHT_STEPS; y++) {
                gl.glBegin(GL.GL_LINE_STRIP);
                pos.setLatitude(topLeft.getLatitude() + y * stepSize);
                pos.setLongitude(topLeft.getLongitude() + x * stepSize);                
                float h = heightData.getHeight(projection.localCoordinatesToGeoCoordinates(pos));
                gl.glVertex3f(pos.getLongitude(), pos.getLatitude(), h*10);
                
                pos.setLongitude(pos.getLongitude() + stepSize);
                h = heightData.getHeight(projection.localCoordinatesToGeoCoordinates(pos));
                gl.glVertex3f(pos.getLongitude(), pos.getLatitude(), h*10);
                
                pos.setLongitude(pos.getLongitude() + stepSize);
                h = heightData.getHeight(projection.localCoordinatesToGeoCoordinates(pos));
                gl.glVertex3f(pos.getLongitude(), pos.getLatitude(), h*10);
                
                pos.setLatitude(topLeft.getLatitude() + (y+1) * stepSize);
                pos.setLongitude(topLeft.getLongitude() + x * stepSize);                
                h = heightData.getHeight(projection.localCoordinatesToGeoCoordinates(pos));
                gl.glVertex3f(pos.getLongitude(), pos.getLatitude(), h*10);
                
                pos.setLatitude(topLeft.getLatitude() + y * stepSize);
                pos.setLongitude(topLeft.getLongitude() + x * stepSize);                
                h = heightData.getHeight(projection.localCoordinatesToGeoCoordinates(pos));
                gl.glVertex3f(pos.getLongitude(), pos.getLatitude(), h*10);
                gl.glEnd();
            }
        }//*/
        
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
