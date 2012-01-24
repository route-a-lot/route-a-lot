package kit.route.a.lot.gui;

import net.java.games.jogl.*;


public class OpenGL {
    
    private GLCanvas canvas;
    
    public GLCanvas getCanvas() {
        return canvas;
    }
    
    public OpenGL()
    {
        GLCapabilities glcaps = new GLCapabilities();
        canvas = GLDrawableFactory.getFactory().createGLCanvas(glcaps);
        canvas.setSize(200, 100);

        OpenGLListener oglListener = new OpenGLListener();
        canvas.addGLEventListener(oglListener);
      
    }
}
