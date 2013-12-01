
/**
Copyright (c) 2012, Malte Wolff, Josua Stabenow
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
        * Redistributions of source code must retain the above copyright
          notice, this list of conditions and the following disclaimer.
        * Redistributions in binary form must reproduce the above copyright
          notice, this list of conditions and the following disclaimer in the
          documentation and/or other materials provided with the distribution.
        * The names of the contributors may not be used to endorse or promote products
          derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
**/

package kit.ral.gui;

import kit.ral.common.Context3D;
import kit.ral.common.Coordinates;
import kit.ral.common.event.Listener;
import kit.ral.common.event.RenderEvent;
import kit.ral.common.projection.Projection;
import kit.ral.common.util.MathUtil;

import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.nio.FloatBuffer;

import static javax.media.opengl.GL.*;
import static kit.ral.common.event.Listener.MAP_RESIZED;
import static kit.ral.common.event.Listener.RENDER;

public class Map3D extends Map implements GLEventListener {
    
    private static final long serialVersionUID = 1;
    private static final float
            ROTATION_SPEED = 0.5f, // factor for the rotation caused by a mouse movement
            VIEW_ANGLE = 85, // horizontal camera opening angle
            UNIT_DISTANCE = 1, // (unscaled) average camera - model distance
            VIEW_MIN_DISTANCE = 0.01f, VIEW_MAX_DISTANCE = 3,
            FOG_START_DISTANCE = 1.4f, FOG_END_DISTANCE = 3,
            MAX_VERTICAL_ROTATION = 50;
            
        
    private float rotationHorizontal = 0, rotationVertical = 25;
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
        //gl.glClearColor(0.824f, 0.902f, 0.745f, 1);
        gl.glClearColor(0,0,0,1);
        gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);       
        
        // ENABLE ROUTE ANTIALIASING
        gl.glEnable(GL_LINE_SMOOTH);
        gl.glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        gl.glEnable(GL_POINT_SMOOTH);
        gl.glHint(GL_POINT_SMOOTH_HINT, GL_NICEST);     
        
        // DEFINE FOG
        gl.glEnable(GL_FOG);
        gl.glFogi(GL_FOG_MODE, GL_LINEAR);
        gl.glFogf(GL_FOG_START, FOG_START_DISTANCE);
        gl.glFogf(GL_FOG_END, FOG_END_DISTANCE);
        gl.glFogfv(GL_FOG_COLOR, new float[]{0, 0, 0, 1}, 0);   
        
        // LIGHTING (ONLY FOR PINS)        
        gl.glEnable(GL_LIGHT0);
        gl.glEnable(GL_COLOR_MATERIAL);
        float[] lightpos = {0.5f, 1, 1, 0};
        float[] ambient = {1, 0.5f, 0.5f, 0.5f}; 
        gl.glLightfv(GL_LIGHT0, GL_POSITION, FloatBuffer.wrap(lightpos));
        gl.glMaterialfv(GL_FRONT, GL_AMBIENT, FloatBuffer.wrap(ambient));
        gl.glMaterialf(GL_FRONT, GL_SHININESS, 0.4f);    
    }
    
    @Override
    public void display(GLAutoDrawable g) {
        GL gl = g.getGL();
        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity(); 
        
        // DEFINE UNIT SIZE (and invert Y axis)
        gl.glTranslatef(0, 0, -UNIT_DISTANCE);
        double unitScale = 2*(Math.tan(Math.toRadians(VIEW_ANGLE / 2)) * UNIT_DISTANCE)
                            / (canvas.getHeight() * Projection.getZoomFactor(zoomlevel));
        gl.glScaled(unitScale, -unitScale, unitScale);
        
        // SET CAMERA ROTATION AND POSITION
        gl.glRotatef(rotationHorizontal, 0, 0, 1);
        double rotHRadians = Math.toRadians(rotationHorizontal);
        gl.glRotated(rotationVertical, Math.cos(rotHRadians), -Math.sin(rotHRadians), 0);
        gl.glTranslated(-center.getLongitude(), -center.getLatitude(), 0);
        
        // CREATE RENDER EVENT
        Listener.fireEvent(RENDER, 
                new RenderEvent(new Context3D(center, zoomlevel, gl)));    
    }

    @Override
    public void displayChanged(GLAutoDrawable g, boolean modeChanged, boolean deviceChanged) {}

    @Override
    public void reshape(GLAutoDrawable g, int x, int y, int width, int height) {   
        // RECREATE PROJECTION MATRIX  
        displayRatio = width / (float)height;
        setProjection(g.getGL(), VIEW_MIN_DISTANCE, VIEW_MAX_DISTANCE);
        Listener.fireEvent(MAP_RESIZED, null); 
        render();
    }
    
    /**
     * Adapts the map position and rotation and schedules a map redraw.
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        if (oldMousePosX == Integer.MIN_VALUE) {
            oldMousePosX = e.getX();
            oldMousePosY = e.getY();
        }
        float diffX = e.getX() - oldMousePosX;
        float diffY = e.getY() - oldMousePosY;
        // ROTATE CAMERA if mouse wheel (or left mouse button + ctrl) is pressed
        if (isMouseButtonPressed(e, 2) || (isMouseButtonPressed(e, 1) && e.isControlDown())) {
            rotationHorizontal += ROTATION_SPEED * diffX;
            rotationHorizontal += (rotationHorizontal < 0) ? 360 : (rotationHorizontal > 360) ? - 360 : 0;
            rotationVertical = MathUtil.clip(rotationVertical + diffY, 0, MAX_VERTICAL_ROTATION);
        }    
        // MOVE CAMERA if left mouse button is pressed (and no ctrl)
        if (isMouseButtonPressed(e, 1) && !e.isControlDown() && !isMouseButtonPressed(e, 3)) {
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
        // QUERY MODELVIEW, PROJECTION MATRIX AND VIEWPORT
        double[] model = new double[16];
        gl.glGetDoublev(GL_MODELVIEW_MATRIX, model, 0);
        double[] proj = new double[16];
        gl.glGetDoublev(GL_PROJECTION_MATRIX, proj, 0);
        int[] viewport = new int[4];
        gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);    
        // QUERY Z (the depth buffer value) at the mouse coordinates
        float[] z = new float[1];
        gl.glReadPixels(x, y, 1, 1, GL_DEPTH_COMPONENT, GL_FLOAT, FloatBuffer.wrap(z));
        // GET AND RETURN WORLD COORDINATES AT (x,y,z)
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
