package kit.route.a.lot.gui;

import java.awt.Component;
import java.awt.event.MouseEvent;

import kit.route.a.lot.common.Context3D;
import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Projection;
import kit.route.a.lot.common.Util;
import kit.route.a.lot.gui.event.ChangeViewEvent;
import javax.media.opengl.*;
import static javax.media.opengl.GL.*;

import javax.media.opengl.glu.GLU;



public class Map3D extends Map implements GLEventListener {
    
    private static final long serialVersionUID = 1L;
    private float rotationHorizontal = 0f;
    private float rotationVertical = 25f;
    
    public Map3D(GUI gui)
    {
        super(gui);
    }
    
    protected Component createCanvas() {
        GLCapabilities glCaps = new GLCapabilities();
        //GLCanvas result = new GLCanvas(glCaps);
        GLJPanel result = new GLJPanel(glCaps);
        
        result.addGLEventListener(this);
        return result;
    }
     
    
    @Override
    public void init(GLAutoDrawable g) {
        GL gl = g.getGL();
        gl.glClearColor(0,0,0,1f);
        gl.glShadeModel(GL_SMOOTH);
        gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);
        
        gl.glEnable(GL_FOG);
        gl.glFogi(GL_FOG_MODE, GL_LINEAR);
        gl.glFogf(GL_FOG_START, 0.6f);
        gl.glFogf(GL_FOG_END, 1f);
        gl.glFogfv(GL_FOG_COLOR, new float[]{0,0,0,1f}, 0);
        
        /*gl.glEnable(GL.GL_LIGHTING);
        gl.glEnable(GL.GL_LIGHT1);
        
        float SHINE_ALL_DIRECTIONS = 1;
        float[] lightPos = {2, 2, 5, SHINE_ALL_DIRECTIONS};
        float[] lightColorAmbient = {0.5f, 0.5f, 0.5f, 1f};
        float[] lightColorSpecular = {0.8f, 0.8f, 0.8f, 1f};

        // Set light parameters.
        gl.glLightfv(GL.GL_LIGHT1, GL.GL_POSITION, lightPos, 0);
        gl.glLightfv(GL.GL_LIGHT1, GL.GL_AMBIENT, lightColorAmbient, 0);
        gl.glLightfv(GL.GL_LIGHT1, GL.GL_SPECULAR, lightColorSpecular, 0);

        // Set material properties.
        float[] rgba = {1f, 1f, 1f};
        gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, rgba, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, rgba, 0);
        gl.glMaterialf(GL.GL_FRONT, GL.GL_SHININESS, 0.5f);
        //*/
    }
    
    @Override
    public void display(GLAutoDrawable g) {
        GL gl = g.getGL();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity(); 
        gl.glScalef(1f, -1f, 1f);

             
        gl.glRotatef(rotationHorizontal, 0, 0, 1);
        double rotHRadians = (float) Math.toRadians(rotationHorizontal);
        gl.glRotated(rotationVertical, Math.cos(rotHRadians), -Math.sin(rotHRadians), 0);
        float height = (bottomRight.getLatitude() - topLeft.getLatitude()); 
        gl.glScalef(1 / height, 1 / height, 1 / height);
        gl.glTranslated(-0.5*(topLeft.getLongitude() + bottomRight.getLongitude()), // camera position
                        -0.5*(topLeft.getLatitude() + bottomRight.getLatitude()), // camera position
                        -0.5 * height / Math.atan(Math.PI/2)); // define unit size = 2D unit size      
        Listeners.fireEvent(gui.getListeners().viewChanged,
                new ChangeViewEvent(new Context3D(topLeft, bottomRight, g), zoomlevel));
    }

    @Override
    public void displayChanged(GLAutoDrawable g, boolean modeChanged, boolean deviceChanged) {}

    @Override
    public void reshape(GLAutoDrawable g, int x, int y, int width, int height) {
        calculateView();
        GL gl = g.getGL();
        
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        (new GLU()).gluPerspective(85.0, width/(float)height, 0.01, 1f);
        gl.glMatrixMode(GL.GL_MODELVIEW);
    }
    
    /**
     * Adapts the map position and rotation and schedules a map redraw.
     */
    @Override
    public void mouseDragged(MouseEvent e) { 
        
        float diffX = e.getX() - oldMousePosX;
        float diffY = e.getY() - oldMousePosY;
        if ((e.getModifiersEx() & MouseEvent.BUTTON2_DOWN_MASK) != 0) {
            rotationHorizontal += 0.5f * diffX;
            rotationHorizontal += (rotationHorizontal < 0) ? 360 : (rotationHorizontal > 360) ? - 360 : 0;
            rotationVertical = Util.clip(rotationVertical + diffY, 0, 60);
        }    
        if ((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0) {
            float shareX = (float) Math.sin(Math.toRadians(rotationHorizontal));
            float shareY = (float) Math.cos(Math.toRadians(rotationHorizontal));
            Coordinates movement = new Coordinates(shareY * diffY + shareX * diffX, shareX * diffY +  shareY * diffX);
            getCenter().add(movement.scale(-Projection.getZoomFactor(zoomlevel)));                 
        }      
        super.mouseDragged(e);
    }
    
}
