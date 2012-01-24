package kit.route.a.lot.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import kit.route.a.lot.common.Coordinates;

import net.java.games.jogl.*;


public class Map3D extends JComponent implements GLEventListener {
    
    private static final long serialVersionUID = 1L;
    
    private ListenerLists listener;
    ArrayList<Coordinates> navPoints;

    private int oldMousePosX;
    private int oldMousePosY;
    private int popupXPos;
    private int popupYPos;
    private Coordinates center;
    private int zoomlevel = 0;
    private Coordinates topLeft = new Coordinates();
    private Coordinates bottomRight = new Coordinates();

    private JPopupMenu navNodeMenu;
    private JMenuItem startItem;
    private JMenuItem endItem;
    private AbstractButton stopoverItem;
    private AbstractButton favoriteItem;
    private GLCanvas canvas;
    
    
    public Map3D(ListenerLists listeners, ArrayList<Coordinates> navPointsList)
    {
        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(this.getSize()));
        this.setBackground(Color.WHITE);
        this.setBorder(BorderFactory.createLineBorder(Color.GRAY, 5));
        this.setVisible(true);
        this.listener = listeners;
        this.navPoints = navPointsList;
        this.center = new Coordinates(0, 0);
        
        GLCapabilities glcaps = new GLCapabilities();
        canvas = GLDrawableFactory.getFactory().createGLCanvas(glcaps);
        canvas.addGLEventListener(this);
        this.add(canvas);
        this.setVisible(true);
    }
    
    public GLCanvas getCanvas() {
        return canvas;
    }
    
    
    
    
    
    
    @Override
    public void init(GLDrawable arg0) {
        GL gl = arg0.getGL();
        GLU glu = arg0.getGLU();

        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        setCamera(gl, glu);

        gl.glMatrixMode(GL.GL_MODELVIEW);
    }
    
    @Override
    public void display(GLDrawable arg0) {
        GL gl = arg0.getGL();
        GLU glu = arg0.getGLU();

        gl.glClear(GL.GL_COLOR_BUFFER_BIT);

        gl.glLoadIdentity();

        glu.gluLookAt(0, 12, 19,
                      0, 0, 0,
                      0, 1, 0);

        gl.glTranslated(0, 1, 0);

        //drawField(gl, glu);
    }
    
    private void setCamera(GL gl, GLU glu)
    {
        int w = 200, h = 100;

        gl.glViewport(0, 0, w, h);

        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();

        glu.gluPerspective(50.0, 1, 2.0, 40.0);
    }

    @Override
    public void displayChanged(GLDrawable arg0, boolean arg1, boolean arg2) {
        // TODO Auto-generated method stub
        
    }  

    @Override
    public void reshape(GLDrawable arg0, int arg1, int arg2, int arg3, int arg4) {
        // TODO Auto-generated method stub
        
    }
    
    public void drawField(GL gl, GLU glu)
    {
        gl.glBegin(GL.GL_QUADS);
            gl.glVertex3f(-6.5f, -1.5f, -6.5f);
            gl.glVertex3f(-6.5f, -1.5f, 6.5f);
            gl.glVertex3f(6.5f, -1.5f, 6.5f);
            gl.glVertex3f(6.5f, -1.5f, -6.5f);
        gl.glEnd();
    }
}
