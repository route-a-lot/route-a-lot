package kit.route.a.lot.map.rendering;

import java.awt.image.BufferedImage;

import javax.media.opengl.GL;
import javax.media.opengl.GLException;
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

    private float lastFloor = 0, lastCeil = 100;
    
    private static final float HEIGHT_STEPS = 80;
    private static Logger logger = Logger.getLogger(Renderer3D.class);
    static {
        logger.setLevel(Level.INFO);
    }
    private Projection projection;
    private IHeightmap heightmap;
    
    
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
        heightmap = State.getInstance().getLoadedHeightmap();
        projection = ProjectionFactory.getProjectionForCurrentMap();
        int factor = Projection.getZoomFactor(detail);
        if (factor < 0) {
            return;
        }
        Coordinates topLeft = context.getTopLeft();
        Coordinates bottomRight = context.getBottomRight();
        
        topLeft.setLatitude(topLeft.getLatitude() - 50 * factor);
        topLeft.setLongitude(topLeft.getLongitude() - 80 * factor);
        bottomRight.setLongitude(bottomRight.getLongitude() + 80 * factor);
        bottomRight.setLatitude(bottomRight.getLatitude() + 20 * factor);
        
        BufferedImage img = new BufferedImage((int) (context.getWidth() / factor), 
                                              (int) (context.getHeight() / factor),
                                              BufferedImage.TYPE_INT_RGB);   
        Context context2D = new Context2D(topLeft, bottomRight, img.createGraphics());
        super.render(context2D, detail);
        
        
        GL gl = ((Context3D) context).getGL();
        gl.glScalef(1,1,10);
        
        // move camera up so that it won't intersect hills  
        Coordinates pos = new Coordinates((float)(bottomRight.getLongitude() + topLeft.getLongitude()) * 0.5f,
                                          (float)(bottomRight.getLatitude() + topLeft.getLatitude()) * 0.5f);
        float actFloor = getHeight(pos);
        float actCeil = actFloor;
        gl.glTranslatef(0,0,-lastFloor);
        
        /*gl.glTranslatef(topLeft.getLongitude(), topLeft.getLatitude(), 0);
        gl.glScalef(context.getWidth(), context.getHeight(), 1);
        gl.glColor3f(1,1,1);
        gl.glBegin(GL.GL_QUADS);
        gl.glVertex3f(0.1f,0.1f,0);
        gl.glVertex3f(0.1f,0.9f,0);
        gl.glVertex3f(0.9f,0.9f,0);
        gl.glVertex3f(0.9f,0.1f,0);
        gl.glEnd();*/
        
        // delete this block for rending the height grid view dependent instead of map dependent
        //int tileDim = (int) (200 * Projection.getZoomFactor(detail));
        //if (tileDim < 0) {
        //    logger.error("tileDim < 0 => seems like an overflow");
        //}
        //float texXOffset = topLeft.getLongitude();
        //float texYOffset = topLeft.getLatitude();
        //topLeft.setLongitude(tileDim * ((int) Math.floor(topLeft.getLongitude() / tileDim)));
        //topLeft.setLatitude(tileDim * ((int) Math.floor(topLeft.getLatitude() / tileDim)));   
        //texXOffset = (texXOffset - topLeft.getLongitude()) / context.getWidth();
        //texYOffset = (texYOffset - topLeft.getLatitude()) / context.getHeight();
        
        
        // render height mesh    
        int texture;
        try {
            texture = createTexture(gl, new GLU(), img);
        } catch (GLException e) {
            texture = -1;
        }
        gl.glEnable(GL.GL_TEXTURE_2D);   
        gl.glBindTexture(GL.GL_TEXTURE_2D, texture);     
        float stepSize = context.getHeight() / HEIGHT_STEPS;
        float xSteps = context.getWidth() / stepSize + 1;       
//        float[] normal = new float[3];
//        float[] vertex1 = new float[] {0f, 0f, 0f};
//        float[] vertex2 = new float[] {0f, 0f, 0f};
//        float[] vertex;
        
        gl.glColor3f(1,1,1);
        for (int x = 0; x < xSteps; x++) {    
            gl.glBegin(GL.GL_TRIANGLE_STRIP);
            for (int y = 0; y < HEIGHT_STEPS; y++) {
                pos.setLatitude(topLeft.getLatitude() + y * stepSize);
                pos.setLongitude(topLeft.getLongitude() + x * stepSize); 
                  
                float h = getHeight(pos);
                if (h < actFloor) actFloor = h;
                if (h > actCeil) actCeil = h;
                float color = Util.mapFloat(h, lastFloor, lastCeil, 0.3f, 1);
                gl.glColor3f(color, color, color);
                gl.glTexCoord2f(x / xSteps, y / HEIGHT_STEPS);
                //vertex = new float[] {pos.getLongitude(), pos.getLatitude(), h};
                //Util.getFaceNormal(normal, vertex, vertex2, vertex1);
                //gl.glNormal3f(-normal[0], -normal[1], -normal[2]);
                //gl.glNormal3f(1, 2, 3);
                gl.glVertex3f(pos.getLongitude(), pos.getLatitude(), h);
                //vertex2 = vertex1;
                //vertex1 = vertex;
                
                pos.setLongitude(pos.getLongitude() + stepSize);
                
                h = getHeight(pos);
                color = Util.mapFloat(h, lastFloor, lastCeil, 0.3f, 1);
                gl.glColor3f(color, color, color);
                gl.glTexCoord2f((x + 1) / xSteps, y / HEIGHT_STEPS) ;
                //vertex = new float[] {pos.getLongitude(), pos.getLatitude(), h};
                //Util.getFaceNormal(normal, vertex, vertex2, vertex1);
                //gl.glNormal3f(normal[0], normal[1], normal[2]);
                //gl.glNormal3f(-3, -2, 1);
                gl.glVertex3f(pos.getLongitude(), pos.getLatitude(), h);
                //vertex2 = vertex1;
                //vertex1 = vertex;
            }
            gl.glEnd();
        }
        gl.glDisable(GL.GL_TEXTURE_2D);
        Textures.delTexture(gl, texture);
        lastFloor = (actFloor / 3 + 5 * lastFloor) / 6;
        lastCeil = (actCeil + 5 * lastCeil) / 6;
        
        // Code for showing the grid.
        /*gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
        gl.glColor3f(1,0,0);     
        for (int x = 0; x < xSteps; x++) {       
            for (int y = 0; y < HEIGHT_STEPS; y++) {
                gl.glBegin(GL.GL_LINE_STRIP);
                
                pos.setLatitude(topLeft.getLatitude() + (y+1) * stepSize);
                pos.setLongitude(topLeft.getLongitude() + x * stepSize);                
                gl.glVertex3f(pos.getLongitude(), pos.getLatitude(), getHeight(pos));
                
                pos.setLatitude(topLeft.getLatitude() + y * stepSize);
                pos.setLongitude(topLeft.getLongitude() + x * stepSize);                
                gl.glVertex3f(pos.getLongitude(), pos.getLatitude(), getHeight(pos));
                
                pos.setLongitude(topLeft.getLongitude() + (x+1) * stepSize);      
                gl.glVertex3f(pos.getLongitude(), pos.getLatitude(), getHeight(pos));

                gl.glEnd();
            }
        }//*/  
    }
    
    private float getHeight(Coordinates position) {
        return heightmap.getHeight(projection.localCoordinatesToGeoCoordinates(position));
    }
    
    private int createTexture(GL gl, GLU glu, BufferedImage image)
    {
        int tex = Textures.genTexture(gl);
        gl.glBindTexture(GL.GL_TEXTURE_2D, tex);
        Textures.makeRGBTexture(gl, image, GL.GL_TEXTURE_2D);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP); 
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP); 
        //gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_MODULATE);
        return tex;
    }
}
