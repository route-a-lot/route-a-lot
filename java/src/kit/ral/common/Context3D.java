package kit.ral.common;

import javax.media.opengl.GL;

public class Context3D extends Context {
    
    private GL gl;
    private Coordinates center;
 
    public Context3D(Coordinates center, int detailLevel, GL gl) {
        super(detailLevel);
        this.center = center;
        this.gl = gl;
    }

    public Coordinates getCenter() {
        return center;
    }   
    
    public GL getGL() {
        return this.gl;
    } 
    
}