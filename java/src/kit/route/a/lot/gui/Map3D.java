package kit.route.a.lot.gui;

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
    
    public GLCanvas createCanvas() {
        GLCapabilities glCaps = new GLCapabilities();
        GLCanvas result = new GLCanvas(glCaps);
        result.addGLEventListener(this);
        return result;
    }
     
    
    @Override
    public void init(GLAutoDrawable g) {
        GL gl = g.getGL();
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glShadeModel(GL.GL_SMOOTH);
        gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA,GL.GL_ONE_MINUS_SRC_ALPHA);
        
        /*float[] lightPos = { 5,5,5,1};        // light position
        float[] noAmbient = { 0.2f, 0.2f, 0.2f, 1f };   // low ambient light
        float[] diffuse =  { 1f, 1f, 1f, 1f };      // full diffuse color
        gl.glEnable(GL.GL_LIGHTING);
        gl.glEnable(GL.GL_LIGHT0);
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT, noAmbient, 0);
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, diffuse, 0);
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION,lightPos, 0);
        
        float[] rgba = {0.3f, 0.5f, 1f};
        gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, rgba, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, rgba, 0);
        gl.glMaterialf(GL.GL_FRONT, GL.GL_SHININESS, 0.5f);//*/

        
        //gl.glLightModelfv(GL.GL_LIGHT_MODEL_AMBIENT, FloatBuffer.wrap(new float[] {0.2f, 0.2f, 0.2f, 1.0f}));
    }
    
    @Override
    public void display(GLAutoDrawable g) {
        GL gl = g.getGL();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity(); 
        gl.glScalef(1f, -1f, 1f);
        
        
        float height = (bottomRight.getLatitude() - topLeft.getLatitude());
        //gl.glTranslatef(0f , 0f, -0.2f); 
        gl.glTranslatef(-0.049019f , -0.28f, 0.05f); // camera correction // TODO dynamic
        gl.glRotatef(20, 1,0,0); // camera angle
        gl.glScalef(1 / height, 1 / height, 1 / height);
        gl.glTranslated(-0.5*(topLeft.getLongitude() + bottomRight.getLongitude()), // camera position
                        -0.5*(topLeft.getLatitude() + bottomRight.getLatitude()), // camera position
                        (float) (-0.5 * height / Math.atan(Math.PI/2))); // define unit size = 2D unit size
        
        //System.out.println("d: " + (-0.5 * height / Math.atan(Math.PI/2) / height));
        Listeners.fireEvent(gui.getListener().viewChanged,
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
        (new GLU()).gluPerspective(85.0, width/(float)height, 0.001, 500);
        gl.glMatrixMode(GL.GL_MODELVIEW);
    }
    
}
