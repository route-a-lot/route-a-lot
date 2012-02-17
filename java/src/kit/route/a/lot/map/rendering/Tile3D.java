package kit.route.a.lot.map.rendering;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.media.opengl.GL;
import static javax.media.opengl.GL.*;

import com.sun.opengl.util.BufferUtil;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Frustum;
import kit.route.a.lot.common.Projection;
import kit.route.a.lot.common.ProjectionFactory;
import kit.route.a.lot.common.Util;
import kit.route.a.lot.controller.State;


public class Tile3D extends Tile {

    private static final int
        HEIGHT_BORDER = 1, // should stay 1
        HEIGHT_RESOLUTION = 64, 
        GRAIN_RESOLUTION = 128;
    private static final float
        GRAIN_INTENSITY = 0.05f,
        SLOPE_SHADE_FACTOR = 0.6f,
        MAX_SLOPE_SHADE_VALUE = 0.6f;
    private static final float[] COLOR_STAGES =
        {-500, 0, 5, 70, 200, 350, 520, 700, 900, 1100, 1450, 2300};
    private static final float[][] COLORS = 
        {{0, 0, 0}, {0, 0, 100}, {140, 170, 150},
         {143, 189, 143}, {151, 253, 153}, 
         {239, 222, 166}, {227, 187, 138}, {174, 144, 115},
         {245, 166, 127}, {203, 115, 76}, {126, 69, 40}, {255, 255, 255}};
    
    private float[][] heights;
    private float minHeight = -500, maxHeight = 8000;
    private int textureID = -1, heightTextureID = -1, displaylistID = -1;
    private static int grainTextureID = -1;
    
    public Tile3D(Coordinates topLeft, float width, int detail) {
        super(topLeft, width, detail);
    }

    @Override
    protected void reset() {
        super.reset();
        heights = new float[HEIGHT_RESOLUTION + 2 * HEIGHT_BORDER]
                           [HEIGHT_RESOLUTION + 2 * HEIGHT_BORDER];
    }

    public void freeResources(GL gl) {
        if (textureID >= 0) {
            gl.glDeleteTextures(1, new int[] { textureID }, 0);
        }
        if (heightTextureID >= 0) {
            gl.glDeleteTextures(1, new int[] { heightTextureID }, 0);
        }
        if (displaylistID >= 0) {
            gl.glDeleteLists(displaylistID, 1);
        }
    }
    
    boolean isInFrustum(Frustum frustum) {
        Coordinates center = getTopLeft().clone().add(getBottomRight()).scale(0.5f);
        return (frustum == null) || frustum.isBoxWithin(
                new float[] {center.getLongitude(), center.getLatitude(),
                        0.5f * (minHeight + maxHeight) },
                new float[] {center.getLongitude() - getTopLeft().getLongitude(), 
                        center.getLatitude() - getTopLeft().getLatitude(),
                        0.5f * (maxHeight - minHeight)});
    }
    
    @Override
    public void prerender() {
        super.prerender();
        Projection projection = ProjectionFactory.getProjectionForCurrentMap();
        State.getInstance().getLoadedHeightmap().reduceSection(
                projection.localCoordinatesToGeoCoordinates(getTopLeft()),
                projection.localCoordinatesToGeoCoordinates(getBottomRight()),
                heights, HEIGHT_BORDER);
        minHeight = heights[0][0];
        maxHeight = heights[0][0];
        for (int x = 0; x <= HEIGHT_RESOLUTION; x++) {
            for (int y = 0; y <= HEIGHT_RESOLUTION; y++) {
                if (heights[x][y] < minHeight) {
                    minHeight = heights[x + HEIGHT_BORDER][y + HEIGHT_BORDER];
                }
                if (heights[x][y] > maxHeight) {
                    maxHeight = heights[x + HEIGHT_BORDER][y + HEIGHT_BORDER];
                }
            }
        }
    }

    public void render(GL gl) {
        // BUILD TEXTURES IF NECESSARY
        if (textureID < 0) {
            textureID = createTexture(gl, getImage(), false, true);
        }
        if (heightTextureID == -1) {
            createHeightTexture(gl);
        }      
        if (grainTextureID < 0) {
            createGrainTexture(gl);
        }
        // RENDER TILE FROM DISPLAY LIST, OR ELSE BUILD DISPLAY LIST
        if (displaylistID >= 0) {
            gl.glCallList(displaylistID);
        } else {
            displaylistID = gl.glGenLists(1);
            gl.glNewList(displaylistID, GL_COMPILE_AND_EXECUTE);
                gl.glActiveTexture(GL_TEXTURE0);
                gl.glEnable(GL_TEXTURE_2D);
                gl.glBindTexture(GL_TEXTURE_2D, textureID);
                gl.glActiveTexture(GL_TEXTURE1);
                gl.glEnable(GL_TEXTURE_2D);
                gl.glBindTexture(GL_TEXTURE_2D, grainTextureID);
                gl.glActiveTexture(GL_TEXTURE2);
                gl.glEnable(GL_TEXTURE_2D);
                gl.glBindTexture(GL_TEXTURE_2D, heightTextureID);
                gl.glColor3f(1,1,1);
                Coordinates topLeft = getTopLeft();
                float stepSize = (getBottomRight().getLatitude() - topLeft.getLatitude()) / (float) HEIGHT_RESOLUTION;
                Coordinates pos = new Coordinates();
                for (int x = 0; x < HEIGHT_RESOLUTION; x++) {
                    gl.glBegin(GL_TRIANGLE_STRIP);
                    for (int y = 0; y <= HEIGHT_RESOLUTION; y++) {            
                        for (int i = 0; i < 2; i++) {
                            pos.setLatitude(topLeft.getLatitude() + y * stepSize);
                            pos.setLongitude(topLeft.getLongitude() + (x + i) * stepSize);
                            gl.glMultiTexCoord2f(GL_TEXTURE0, (x + i) / (float) HEIGHT_RESOLUTION, y / (float) HEIGHT_RESOLUTION);
                            gl.glMultiTexCoord2f(GL_TEXTURE1, (x + i) / (float) GRAIN_RESOLUTION, y / (float) GRAIN_RESOLUTION);
                            gl.glMultiTexCoord2f(GL_TEXTURE2, (x + i) / (float) HEIGHT_RESOLUTION, y / (float) HEIGHT_RESOLUTION);
                            gl.glVertex3f(pos.getLongitude(), pos.getLatitude(), heights[x + HEIGHT_BORDER + i][y + HEIGHT_BORDER]);
                        }
                    }
                    gl.glEnd();
                }
                gl.glDisable(GL_TEXTURE_2D);
                gl.glActiveTexture(GL_TEXTURE1);
                gl.glDisable(GL_TEXTURE_2D);
                gl.glActiveTexture(GL_TEXTURE0);
            gl.glEndList();
        }   
    }
    
    private void createHeightTexture(GL gl) {
        BufferedImage heightImage = new BufferedImage(HEIGHT_RESOLUTION, HEIGHT_RESOLUTION,
                BufferedImage.TYPE_INT_RGB);
        Coordinates topLeft = getTopLeft();
        float stepSize = (getBottomRight().getLatitude() - topLeft.getLatitude()) / (float) (HEIGHT_RESOLUTION - 1);
        Coordinates pos = new Coordinates();
        float[] color = new float[3];
        for (int x = 0; x < HEIGHT_RESOLUTION; x++) { 
            for (int y = 0; y < HEIGHT_RESOLUTION; y++) {            
                    pos.setLatitude(topLeft.getLatitude() + y * stepSize);
                    pos.setLongitude(topLeft.getLongitude() + x * stepSize);
                    float height = heights[x + HEIGHT_BORDER][y + HEIGHT_BORDER];
                    getHeightColor(color, height);
                    float min = height, max = height;
                    for (int i = -1; i < 2; i++) {
                        for (int k = -1; k < 2; k++) {
                            height = heights[x + HEIGHT_BORDER + i][y + HEIGHT_BORDER + k];
                            min = Math.min(min, height);
                            max = Math.max(max, height);
                        }
                    }
                    
                    float shade = 1 - Util.clip(SLOPE_SHADE_FACTOR * (max - min) / stepSize, 0, MAX_SLOPE_SHADE_VALUE);
                    color[0] *= shade;
                    color[1] *= shade;
                    color[2] *= shade;
                    heightImage.setRGB(x, y, Util.RGBToInt(color));
            }
        }
        heightTextureID = createTexture(gl, heightImage, false, true);
    } 
    
    private static void createGrainTexture(GL gl) {
        BufferedImage grainImage = new BufferedImage(GRAIN_RESOLUTION, GRAIN_RESOLUTION,
                BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < GRAIN_RESOLUTION; x++) {
            for (int y = 0; y < GRAIN_RESOLUTION; y++) { 
                float random = (float)(Math.random() * GRAIN_INTENSITY) + (1 - GRAIN_INTENSITY);
                grainImage.setRGB(x, y, (new Color(random, random, random)).getRGB());
            }
        }
        grainTextureID = createTexture(gl, grainImage, true, true);
    }
    
    private static void getHeightColor(float[] color, float height) {
        int col1 = COLOR_STAGES.length - 1, col2 = col1;
        for (int i = 1; i < COLOR_STAGES.length; i++) {
            if (height < COLOR_STAGES[i]) {
                col1 = i - 1;
                col2 = i;
                break;
            }
        }        
        float ratio = (col1 == col2) ? 0 : (height - COLOR_STAGES[col1]) / (COLOR_STAGES[col2] - COLOR_STAGES[col1]);
        for (int i = 0; i < 3; i++) {
            color[i] = (COLORS[col1][i] + (COLORS[col2][i] - COLORS[col1][i]) * ratio) / 255;
        }
        
    }
    
    private static int createTexture(GL gl, BufferedImage image, boolean repeat, boolean linear) {
        final int[] tmp = new int[1];
        gl.glGenTextures(1, tmp, 0);
        int tex = tmp[0];
        gl.glBindTexture(GL_TEXTURE_2D, tex);
        int[] data = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        ByteBuffer dest = ByteBuffer.allocateDirect(data.length * BufferUtil.SIZEOF_INT);
        dest.order(ByteOrder.nativeOrder());
        dest.asIntBuffer().put(data, 0, data.length);
        //gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_GENERATE_MIPMAP, 1);
        int filter = (linear) ? GL_LINEAR : GL_NEAREST;
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filter);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, filter);
        int wrapMode = (repeat) ? GL_REPEAT : GL_CLAMP_TO_EDGE;
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, wrapMode);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, wrapMode);
        gl.glTexImage2D(GL_TEXTURE_2D, 0, GL.GL_RGB, image.getWidth(), image.getHeight(), 0, GL_BGRA, GL_UNSIGNED_BYTE, dest);
        //(new GLU()).gluBuild2DMipmaps(GL.GL_TEXTURE_2D, GL.GL_RGB, image.getWidth(), image.getHeight(), GL.GL_BGRA, GL.GL_UNSIGNED_BYTE, dest);
        
        // gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_MODULATE);
        return tex;
    }
    
}
