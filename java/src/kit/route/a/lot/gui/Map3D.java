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
    
    public Component createCanvas() {
        GLCapabilities glCaps = new GLCapabilities();
        //GLCanvas result = new GLCanvas(glCaps);
        GLJPanel result = new GLJPanel(glCaps);
        
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
        gl.glEnable(GL.GL_DEPTH_TEST);
        
        //gl.glEnable(GL.GL_LIGHTING);
        /*gl.glEnable(GL.GL_LIGHT1);
        
        float SHINE_ALL_DIRECTIONS = 1;
        float[] lightPos = {-30, 0, 0, SHINE_ALL_DIRECTIONS};
        float[] lightColorAmbient = {0.2f, 0.2f, 0.2f, 1f};
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

        /*gl.glTranslatef(0 , 0, -3);
        
        float[] v1 = new float[]{1,0,0};
        float[] v2 = new float[]{1,1,0};
        float[] v3 = new float[]{0,1,0};
        float[] v4 = new float[]{-3,-3,10};
        float[] n1 = new float[3];
        float[] n2 = new float[3];
        
        Util.getFaceNormal(n1, v1, v2, v3);
        Util.getFaceNormal(n2, v1, v3, v4);
        
        gl.glColor3f(1,1,1);
        gl.glBegin(GL.GL_TRIANGLES);
        gl.glNormal3f(n1[0],n1[1],n1[2]);
        gl.glVertex3f(v1[0],v1[1],v1[2]);
        gl.glVertex3f(v2[0],v2[1],v2[2]);
        gl.glVertex3f(v3[0],v3[1],v3[2]);
        
        gl.glNormal3f(n2[0],n2[1],n2[2]);
        gl.glVertex3f(v1[0],v1[1],v1[2]);
        gl.glVertex3f(v3[0],v3[1],v3[2]);
        gl.glVertex3f(v4[0],v4[1],v4[2]);
        gl.glEnd();*/
               
        //gl.glLightfv(GL.GL_LIGHT1, GL.GL_POSITION, new float[]{0,-3,-2,1}, 0);
        
        float height = (bottomRight.getLatitude() - topLeft.getLatitude());
        
        gl.glTranslatef(0 , -0.28f, 0); // camera correction // TODO dynamic
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
