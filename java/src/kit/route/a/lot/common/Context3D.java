package kit.route.a.lot.common;

import java.awt.Image;
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
    
    @Override
    public void drawImage(Coordinates position, Image image, int detail) {
        throw new UnsupportedOperationException("Method drawImage() is not implemented for Context3D.");
    }
}
