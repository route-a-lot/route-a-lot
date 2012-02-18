package kit.route.a.lot.gui;

import java.awt.Component;
import java.awt.event.MouseEvent;
import kit.route.a.lot.common.Context3D;
import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Projection;
import kit.route.a.lot.common.Util;
import kit.route.a.lot.gui.event.RenderEvent;
import static kit.route.a.lot.common.Listener.*;

import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;
import static javax.media.opengl.GL.*;

public class Map3D extends Map implements GLEventListener {
    
    private static final long serialVersionUID = 1L;
    private static final float ROTATION_SPEED = 0.5f, MAX_DISTANCE = 1f, VIEW_ANGLE = 85f;
    private float rotationHorizontal = 0f, rotationVertical = 25f;
    
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
        
        gl.glEnable(GL_LINE_SMOOTH);
        gl.glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        gl.glEnable(GL_POINT_SMOOTH);
        gl.glHint(GL_POINT_SMOOTH_HINT, GL_NICEST);
        
        gl.glEnable(GL_FOG);
        gl.glFogi(GL_FOG_MODE, GL_LINEAR);
        gl.glFogf(GL_FOG_START, 0.6f * MAX_DISTANCE);
        gl.glFogf(GL_FOG_END, MAX_DISTANCE);
        gl.glFogfv(GL_FOG_COLOR, new float[]{0,0,0,1f}, 0);     
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
        gui.getListeners().fireEvent(VIEW_CHANGED,
                new RenderEvent(new Context3D(topLeft, bottomRight, gl, zoomlevel)));
    }

    @Override
    public void displayChanged(GLAutoDrawable g, boolean modeChanged, boolean deviceChanged) {}

    @Override
    public void reshape(GLAutoDrawable g, int x, int y, int width, int height) {
        calculateView();
        GL gl = g.getGL(); 
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        (new GLU()).gluPerspective(VIEW_ANGLE, width/(float)height, 0.01, MAX_DISTANCE);
        gl.glMatrixMode(GL.GL_MODELVIEW);
    }
    
    /**
     * Adapts the map position and rotation and schedules a map redraw.
     */
    @Override
    public void mouseDragged(MouseEvent e) {      
        float diffX = e.getX() - oldMousePosX;
        float diffY = e.getY() - oldMousePosY;
        if ((e.getModifiersEx() & MouseEvent.BUTTON2_DOWN_MASK) != 0
                || ((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0 
                        && e.isControlDown())) {
            rotationHorizontal += ROTATION_SPEED * diffX;
            rotationHorizontal += (rotationHorizontal < 0) ? 360 : (rotationHorizontal > 360) ? - 360 : 0;
            rotationVertical = Util.clip(rotationVertical + diffY, 0, 60);
        }    
        if ((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0 && !e.isControlDown()) {
            float shareY = (float) Math.cos(Math.toRadians(rotationHorizontal));
            float shareX = (float) Math.sin(Math.toRadians(rotationHorizontal));            
            Coordinates movement = new Coordinates(shareY * diffY - shareX * diffX,
                    shareX * diffY + shareY * diffX);
            center.add(movement.scale(-Projection.getZoomFactor(zoomlevel)));                 
        }      
        super.mouseDragged(e);
    }

    @Override
    protected Coordinates getPosition(int x, int y) {
        Coordinates result = new Coordinates(y - canvas.getHeight() / 2, x - canvas.getWidth() / 2);
        return result.scale(Projection.getZoomFactor(zoomlevel)).add(center);
        
        /*GL gl = ((GLJPanel) canvas).getGL();// TODO doesnt work properly
           
        y = canvas.getHeight() - y;
        double[] model = new double[16];
        gl.glGetDoublev(GL_MODELVIEW_MATRIX, model, 0);
        double[] proj = new double[16];
        gl.glGetDoublev(GL_PROJECTION_MATRIX, proj, 0);
        int[] viewport = new int[4];
        gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);    
        float[] z = new float[1];
        gl.glReadPixels(x, y, 1, 1, GL_DEPTH_COMPONENT, GL_FLOAT, FloatBuffer.wrap(z));
        System.out.println(x + " / " + y + " / " + z[0]); 
        double[] result = new double[3];
        (new GLU()).gluUnProject((double) x, (double) y, (double) z[0],
                model, 0, proj, 0, viewport, 0, result, 0);
        return new Coordinates((float) result[0], (float) result[1]);//*/
    }
    
}
