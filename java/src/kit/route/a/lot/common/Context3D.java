package kit.route.a.lot.common;

import javax.media.opengl.*;

public class Context3D extends Context {
    
    private GL gl;
 
    public Context3D(Coordinates topLeft, Coordinates bottomRight, GL gl, int zoomlevel) {
        super(topLeft, bottomRight, zoomlevel);
        this.gl = gl;
    }

    public GL getGL() {
        return this.gl;
    } 
    
}
