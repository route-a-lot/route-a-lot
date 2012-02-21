package kit.route.a.lot.gui;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.nio.FloatBuffer;

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
    private static final float ROTATION_SPEED = 0.5f, VIEW_ANGLE = 85, FOG_START_DISTANCE = 1.3f;
    
    private static final float[] LOD_STAGES = {0.01f, 1, 2}; 
    private static final int[] LOD_STAGE_LEVELS = {0, 1}; 
    
    
    private float rotationHorizontal = 0f, rotationVertical = 25f;
    private float displayRatio = 1;
   
    public Map3D(GUI gui)
    {
        super(gui);
    }
    
    protected Component createCanvas() {
        GLCapabilities glCaps = new GLCapabilities();
        GLJPanel result = new GLJPanel(glCaps);     
        result.addGLEventListener(this);
        return result;
    }     
    
    @Override
    public void init(GLAutoDrawable g) {
        GL gl = g.getGL();
        gl.glClearColor(0, 0, 0, 1);
        gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);        
        // enable antialiasig for the route
        gl.glEnable(GL_LINE_SMOOTH);
        gl.glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        gl.glEnable(GL_POINT_SMOOTH);
        gl.glHint(GL_POINT_SMOOTH_HINT, GL_NICEST);       
        // define fog
        gl.glEnable(GL_FOG);
        gl.glFogi(GL_FOG_MODE, GL_LINEAR);
        gl.glFogf(GL_FOG_START, FOG_START_DISTANCE);
        gl.glFogf(GL_FOG_END, LOD_STAGES[LOD_STAGES.length - 1]);
        gl.glFogfv(GL_FOG_COLOR, new float[]{0, 0, 0, 1}, 0);     
    }
    
    @Override
    public void display(GLAutoDrawable g) {
        GL gl = g.getGL();
        gl.glClear(GL_COLOR_BUFFER_BIT);
        gl.glLoadIdentity(); 
        gl.glScalef(1, -1, 1);
        // apply camera rotation
        gl.glRotatef(rotationHorizontal, 0, 0, 1);
        double rotHRadians = (float) Math.toRadians(rotationHorizontal);
        gl.glRotated(rotationVertical, Math.cos(rotHRadians), -Math.sin(rotHRadians), 0);
        // apply camera position and unit scale
        float height = (bottomRight.getLatitude() - topLeft.getLatitude()); 
        gl.glScalef(1 / height, 1 / height, 1 / height);
        gl.glTranslated(-0.5*(topLeft.getLongitude() + bottomRight.getLongitude()), // camera position
                        -0.5*(topLeft.getLatitude() + bottomRight.getLatitude()), // camera position
                        -0.5 * height / Math.atan(Math.PI/2)); // define unit size = 2D unit size      
        
        // create render events (one for each level of detail)
        for (int i = LOD_STAGES.length - 1; i > 0; i--) {
            if (i == LOD_STAGES.length - 1) continue;
            gl.glClear(GL_DEPTH_BUFFER_BIT);
            setProjection(gl, LOD_STAGES[i - 1] * 0.9f, LOD_STAGES[i]);
            gl.glPushMatrix();
            gui.getListeners().fireEvent(VIEW_CHANGED,
                    new RenderEvent(new Context3D(topLeft, bottomRight,
                            gl, zoomlevel + LOD_STAGE_LEVELS[i - 1]))); 
            gl.glPopMatrix();     
        }
    }

    @Override
    public void displayChanged(GLAutoDrawable g, boolean modeChanged, boolean deviceChanged) {}

    @Override
    public void reshape(GLAutoDrawable g, int x, int y, int width, int height) {   
        // recreate the projection matrix     
        displayRatio = width / (float)height;
        setProjection(g.getGL(), LOD_STAGES[0], LOD_STAGES[1]);
        gui.getListeners().fireEvent(MAP_RESIZED, null); 
        calculateView();
    }
    
    /**
     * Adapts the map position and rotation and schedules a map redraw.
     */
    @Override
    public void mouseDragged(MouseEvent e) {      
        float diffX = e.getX() - oldMousePosX;
        float diffY = e.getY() - oldMousePosY;
        // rotate camera if mouse wheel (or left mouse button + ctrl) is pressed
        if (isMouseButtonPressed(e, 2) || (isMouseButtonPressed(e, 1) && e.isControlDown())) {
            rotationHorizontal += ROTATION_SPEED * diffX;
            rotationHorizontal += (rotationHorizontal < 0) ? 360 : (rotationHorizontal > 360) ? - 360 : 0;
            rotationVertical = Util.clip(rotationVertical + diffY, 0, 60);
        }    
        // move camera if left mouse button is pressed (and no ctrl)
        if (isMouseButtonPressed(e, 1) && !e.isControlDown()) {
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
        // activate OGL context, invert y
        ((GLJPanel) canvas).getContext().makeCurrent();
        GL gl = ((GLJPanel) canvas).getGL();
        y = canvas.getHeight() - y;
        // query modelview and projection matrix as well as the vieport
        double[] model = new double[16];
        gl.glGetDoublev(GL_MODELVIEW_MATRIX, model, 0);
        double[] proj = new double[16];
        gl.glGetDoublev(GL_PROJECTION_MATRIX, proj, 0);
        int[] viewport = new int[4];
        gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);    
        // query z (the depth buffer value) at the mouse coordinates
        float[] z = new float[1];
        gl.glReadPixels(x, y, 1, 1, GL_DEPTH_COMPONENT, GL_FLOAT, FloatBuffer.wrap(z));
        // get and return world coordinates at (x,y,z)
        double[] result = new double[3];
        (new GLU()).gluUnProject((double) x, (double) y, (double) z[0],
                model, 0, proj, 0, viewport, 0, result, 0);
        return new Coordinates((float) result[1], (float) result[0]);    
    }
    
    private void setProjection(GL gl, float nearPlane, float farPlane) {
        gl.glMatrixMode(GL_PROJECTION);
        gl.glLoadIdentity();
        (new GLU()).gluPerspective(VIEW_ANGLE, displayRatio, nearPlane, farPlane);
        gl.glMatrixMode(GL_MODELVIEW);
    }
}
