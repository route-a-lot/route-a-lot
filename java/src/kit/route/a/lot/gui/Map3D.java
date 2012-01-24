package kit.route.a.lot.gui;

import kit.route.a.lot.common.Context3D;
import kit.route.a.lot.gui.event.ViewChangedEvent;
import net.java.games.jogl.*;


public class Map3D extends Map implements GLEventListener {
    
    private static final long serialVersionUID = 1L;
    
     
    public Map3D(GUI gui)
    {
        super(gui);
    }
    
    public GLCanvas createCanvas() {
        GLCapabilities glCaps = new GLCapabilities();
        GLCanvas result = GLDrawableFactory.getFactory().createGLCanvas(glCaps);
        result.addGLEventListener(this);
        return result;
    }
     
    
    @Override
    public void init(GLDrawable g) {
        GL gl = g.getGL();
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }
    
    @Override
    public void display(GLDrawable g) {
        GL gl = g.getGL();
        GLU glu = g.getGLU();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glLoadIdentity();
        glu.gluLookAt(0, 12, 19,
                      0, 0, 0,
                      0, 1, 0);
        gl.glTranslated(0, 1, 0);
        
        //gl.glScalef(0.1f, 0.1f, 0.1f);
        //gl.glTranslatef(- topLeft.getLongitude(), - topLeft.getLatitude(), 0);
        ListenerLists.fireEvent(gui.getListener().viewChanged,
                new ViewChangedEvent(new Context3D(topLeft, bottomRight, g), zoomlevel));  
    }

    @Override
    public void displayChanged(GLDrawable g, boolean arg1, boolean arg2) {
        // TODO Auto-generated method stub        
    }

    @Override
    public void reshape(GLDrawable g, int x, int y, int width, int height) {
        calculateView();
        GL gl = g.getGL();
        
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        g.getGLU().gluPerspective(50.0, 1, 1.0, 300.0);
        gl.glMatrixMode(GL.GL_MODELVIEW);
    }
    
}
