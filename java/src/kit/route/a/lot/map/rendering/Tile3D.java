package kit.route.a.lot.map.rendering;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.media.opengl.GL;
import com.sun.opengl.util.BufferUtil;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Frustum;
import kit.route.a.lot.common.Projection;
import kit.route.a.lot.common.ProjectionFactory;
import kit.route.a.lot.common.Util;
import kit.route.a.lot.controller.State;


public class Tile3D extends Tile {

    private static final int HEIGHT_RESOLUTION = 5;
    private float[][] heightdata;
    private float minHeight = 0;
    private float maxHeight = 0;
    private int textureID = -1;

    public Tile3D(Coordinates topLeft, float width, int detail) {
        super(topLeft, width, detail);
    }

    @Override
    protected void reset() {
        super.reset();
        heightdata = new float[HEIGHT_RESOLUTION + 1][HEIGHT_RESOLUTION + 1];
    }

    public void prerender() {
        super.prerender();
        Projection projection = ProjectionFactory.getProjectionForCurrentMap();
        State.getInstance().getLoadedHeightmap().reduceSection(
                projection.localCoordinatesToGeoCoordinates(getTopLeft()),
                projection.localCoordinatesToGeoCoordinates(getBottomRight()),
                heightdata);
        minHeight = heightdata[0][0];
        maxHeight = heightdata[0][0];
        for (int x = 0; x < HEIGHT_RESOLUTION; x++) {
            for (int y = 0; y < HEIGHT_RESOLUTION + 1; y++) {
                if (heightdata[x][y] < minHeight) {
                    minHeight = heightdata[x][y];
                }
                if (heightdata[x][y] > maxHeight) {
                    maxHeight = heightdata[x][y];
                }
            }
        }
    }


    public void render(GL gl) {
        if (heightdata == null) {
            reset();
        }
        if (textureID < 0) {
            textureID = createTexture(gl, getImage());
        }
        
        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textureID);
        
        Coordinates topLeft = getTopLeft();
        float stepSize = (getBottomRight().getLatitude() - topLeft.getLatitude()) / (float) HEIGHT_RESOLUTION;
        Coordinates pos = new Coordinates();
        for (int x = 0; x < HEIGHT_RESOLUTION; x++) {
            gl.glBegin(GL.GL_TRIANGLE_STRIP);
            for (int y = 0; y < HEIGHT_RESOLUTION + 1; y++) {
                pos.setLatitude(Math.round(topLeft.getLatitude() + y * stepSize));
                pos.setLongitude(topLeft.getLongitude() + x * stepSize);
                

                float h = heightdata[x][y];
                float color =  1f; //Util.mapFloat(h, 0, 127, 0.3f, 1);
                gl.glColor3f(color, color, color);
                gl.glTexCoord2f(x / (float) HEIGHT_RESOLUTION, y / (float) HEIGHT_RESOLUTION);
                gl.glVertex3f(pos.getLongitude(), pos.getLatitude(), h);

                pos.setLongitude(pos.getLongitude() + stepSize);

                h = heightdata[x + 1][y];
                //color = Util.mapFloat(h, 0, 127, 0.3f, 1);
                gl.glColor3f(color, color, color);
                gl.glTexCoord2f((x + 1) / (float) HEIGHT_RESOLUTION, y / (float) HEIGHT_RESOLUTION);
                gl.glVertex3f(pos.getLongitude(), pos.getLatitude(), h);
            }
            gl.glEnd();
        }
        gl.glDisable(GL.GL_TEXTURE_2D);
    }

    public boolean isInFrustum(Frustum frustum) {
        Coordinates center = getTopLeft().clone().add(getBottomRight()).scale(0.5f);
        /*return frustum.isPointWithin(new float[] {getTopLeft().getLongitude(), getTopLeft().getLatitude(), 0})
                || frustum.isPointWithin(new float[] {getTopLeft().getLongitude(), getBottomRight().getLatitude(), 0})
                || frustum.isPointWithin(new float[] {getBottomRight().getLongitude(), getTopLeft().getLatitude(), 0})
                || frustum.isPointWithin(new float[] {getBottomRight().getLongitude(), getBottomRight().getLatitude(), 0});//*/
        return frustum.isBoxWithin(
                new float[] { center.getLongitude(), center.getLatitude(), minHeight },
                new float[] { center.getLongitude() - getTopLeft().getLongitude(),
                        center.getLatitude() - getTopLeft().getLatitude(), maxHeight});//*/
    }


    private static int createTexture(GL gl, BufferedImage image) {
        final int[] tmp = new int[1];
        gl.glGenTextures(1, tmp, 0);
        int tex = tmp[0];
        gl.glBindTexture(GL.GL_TEXTURE_2D, tex);
        int[] data = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        ByteBuffer dest = ByteBuffer.allocateDirect(data.length * BufferUtil.SIZEOF_INT);
        dest.order(ByteOrder.nativeOrder());
        dest.asIntBuffer().put(data, 0, data.length);
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGB, image.getWidth(), image.getHeight(), 0, GL.GL_BGRA,
                GL.GL_UNSIGNED_BYTE, dest);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP);
        // gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_MODULATE);
        return tex;
    }

    private static void deleteTexture(GL gl, int tex) {
        gl.glDeleteTextures(1, new int[] { tex }, 0);
    }

}
