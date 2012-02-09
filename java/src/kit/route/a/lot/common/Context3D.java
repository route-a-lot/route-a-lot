package kit.route.a.lot.common;

import javax.media.opengl.*;

public class Context3D extends Context {
    
    private GLAutoDrawable output;
 
    public Context3D(Coordinates topLeft, Coordinates bottomRight, GLAutoDrawable surface) {
        super(topLeft, bottomRight);
        output = surface;
    }

    public GL getGL() {
        return output.getGL();
    } 
    
}
