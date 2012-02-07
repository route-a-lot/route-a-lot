package kit.route.a.lot.common;

import java.awt.Image;
import javax.media.opengl.*;

public class Context3D extends Context {
    
    private GLAutoDrawable output;
    
    private Frustum frustum;
 
    public Context3D(Coordinates topLeft, Coordinates bottomRight, GLAutoDrawable surface) {
        super(topLeft, bottomRight);
        output = surface;
        frustum = new Frustum(output.getGL());
    }
    
    /*public Context3D(Coordinates topLeft, int width, int height, float scale, GL surface) {
    super(width, height, topLeft, null);
    output = surface;
    projection = Projection.getNewProjection(topLeft);
    Coordinates localTopLeft = projection.geoCoordinatesToLocalCoordinates(topLeft);
    Coordinates localBottomRight = new Coordinates(localTopLeft.getLatitude() - height,
                                                   localTopLeft.getLongitude() + width);
    bottomRight = projection.localCoordinatesToGeoCoordinates(localBottomRight);
    }*/

    public GL getGL() {
        return output.getGL();
    }
    
    public Frustum getFrustum() {
        return frustum;
    }
    
    @Override
    public void drawImage(Coordinates position, Image image, int detail) {
        throw new UnsupportedOperationException("Method drawImage() is not implemented for Context3D.");
    }
}
