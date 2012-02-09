package kit.route.a.lot.gui;

import java.awt.Component;

import kit.route.a.lot.common.Context3D;
import kit.route.a.lot.gui.event.ChangeViewEvent;
import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;



public class Map3D extends Map implements GLEventListener {
    
    private static final long serialVersionUID = 1L;
    
     
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
        gl.glShadeModel(GL.GL_SMOOTH);
        gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA,GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glDepthFunc(GL.GL_LEQUAL);
        
        gl.glEnable(GL.GL_FOG);
        gl.glFogi(GL.GL_FOG_MODE, GL.GL_LINEAR);
        gl.glFogf(GL.GL_FOG_START, 0.6f);
        gl.glFogf(GL.GL_FOG_END, 1f);
        gl.glFogfv(GL.GL_FOG_COLOR, new float[]{0,0,0,1f}, 0);
        
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

        float height = (bottomRight.getLatitude() - topLeft.getLatitude());      
        gl.glRotatef(25f + 5f / (zoomlevel + 1), 1,0,0); // camera angle
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
    
}
