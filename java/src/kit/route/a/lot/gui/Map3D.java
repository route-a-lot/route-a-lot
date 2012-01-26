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
    }
    
    @Override
    public void display(GLAutoDrawable g) {
        GL gl = g.getGL();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();
        gl.glScalef(1f, -1f, 1f);
        
        Listeners.fireEvent(gui.getListener().viewChanged,
                new ChangeViewEvent(new Context3D(topLeft, bottomRight, g), zoomlevel));  
    }

    @Override
    public void displayChanged(GLAutoDrawable g, boolean arg1, boolean arg2) {
        // TODO ???   
    }

    @Override
    public void reshape(GLAutoDrawable g, int x, int y, int width, int height) {
        calculateView();
        GL gl = g.getGL();
        
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        (new GLU()).gluPerspective(85.0, width/(float)height, 5.0, 1000.0);
        gl.glMatrixMode(GL.GL_MODELVIEW);
    }
    
}
