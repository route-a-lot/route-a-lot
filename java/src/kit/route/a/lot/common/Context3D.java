package kit.route.a.lot.common;

import java.awt.Color;
import java.awt.Image;

import net.java.games.jogl.GL;
import net.java.games.jogl.GLDrawable;

//import kit.route.a.lot.map.rendering.Projection;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class Context3D extends Context {
    
    private static Logger logger = Logger.getLogger(Context3D.class);
    static {
        logger.setLevel(Level.INFO);
    }
    
    private GLDrawable output;
    //private Projection projection;
    
    /*public Context3D(int width, int height, Coordinates topLeft, Coordinates bottomRight, GL surface) {
        super(width, height, topLeft, bottomRight);
        if (surface == null) {
            throw new IllegalArgumentException();
        }
        output = surface;
    }*/

    /*public Context3D(Coordinates topLeft, int width, int height, float scale, GL surface) {
        super(width, height, topLeft, null);
        output = surface;
        projection = Projection.getNewProjection(topLeft);
        Coordinates localTopLeft = projection.geoCoordinatesToLocalCoordinates(topLeft);
        Coordinates localBottomRight = new Coordinates(localTopLeft.getLatitude() - height,
                                                       localTopLeft.getLongitude() + width);
        bottomRight = projection.localCoordinatesToGeoCoordinates(localBottomRight);
    }*/
    
    public Context3D(Coordinates topLeft, Coordinates bottomRight, GLDrawable surface) {
        super(0, 0, topLeft, bottomRight);
        output = surface;
        calculateSize();
    }

    @Override
    public void fillBackground(Color color) {
        GL gl = output.getGL();
        gl.glClearColor(color.getRed() / 255f, color.getGreen() / 255f,
                            color.getBlue() / 255f, color.getAlpha() / 255f);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
    }

    @Override
    public void drawImage(Coordinates position, Image image, int detail) {
        GL gl = output.getGL();
        gl.glBegin(GL.GL_QUADS);
        gl.glVertex3f(-6.5f, -1.5f, -6.5f);
        gl.glVertex3f(-6.5f, -1.5f, 6.5f);
        gl.glVertex3f(6.5f, -1.5f, 6.5f);
        gl.glVertex3f(6.5f, -1.5f, -6.5f);
        gl.glEnd();
        logger.info("draw");
    }

    @Override
    public void calculateSize() {
        width = (int) Math.abs(bottomRight.getLongitude() - topLeft.getLongitude());
        height = (int) Math.abs(bottomRight.getLatitude() - topLeft.getLatitude());
    }

    @Override
    public float getScale() {
        return 1;
    }

}
