package kit.ral.map.rendering;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Collection;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import static javax.media.opengl.GL.*;

import com.sun.opengl.util.BufferUtil;

import kit.ral.common.Bounds;
import kit.ral.common.Coordinates;
import kit.ral.common.Frustum;
import kit.ral.common.description.OSMType;
import kit.ral.common.projection.Projection;
import kit.ral.common.projection.ProjectionFactory;
import kit.ral.common.util.MathUtil;
import kit.ral.controller.State;
import kit.ral.heightinfo.Heightmap;
import kit.ral.map.MapElement;
import kit.ral.map.POINode;


public class Tile3D extends Tile {

    private static final int
        HEIGHT_BORDER = 1, // [1] border in height data that is not rendered but used for coloring
        HEIGHT_RESOLUTION = 64, // [2^n] resolution of the tile's height grid and height texture
        GRAIN_RESOLUTION = 128; // [2^n] resolution of the applied noise texture
    private static final float
        GRAIN_INTENSITY = 0.05f, // [0..1] darkest value of grain texture (% of black)
        SLOPE_SHADE_FACTOR = 0.6f, // factor multiplied on all slope shades
        MAX_SLOPE_SHADE_VALUE = 0.6f; // [0..1] upper limit of slope shade (1 = black)
    private static final float[] COLOR_POI = {1, 1, 0}; //{0.898f, 0.741f, 0.392f};
    
    // [meters] heights that specific colors (s.b.) will be mapped to
    private static final float[] COLOR_STAGES =
        {-500, 0, 5, 70, 200, 350, 520, 700, 900, 1100, 1450, 2300};
    // [{{0..255, 0..255, 0..255}, ...}] rgb colors for the above declared heights
    private static final float[][] COLORS = 
        {{0, 0, 0}, {210, 230, 190}, {140, 170, 150},
         {143, 189, 143}, {151, 253, 153}, 
         {239, 222, 166}, {227, 187, 138}, {174, 144, 115},
         {245, 166, 127}, {203, 115, 76}, {126, 69, 40}, {255, 255, 255}};
    
    private float[][] heights;
    private float minHeight = -500, maxHeight = 8000;
    private int textureID = -1, heightTextureID = -1, displaylistID = -1;
    private static int grainTextureID = -1;
    
    // Temporary variables (only guaranteed to be valid when rendering):
    Projection projection;
    Heightmap heightmap;
    
    
    /**
     * Creates a new quadratic 3D tile. Needed resources will not
     * be allocated here, but instead as soon as needed.
     * @param topLeft northwestern corner of the tile area
     * @param tileSize tile width and height
     * @param detailLevel level indicating the desired tile quality
     */
    public Tile3D(Coordinates topLeft, int tileSize, int detailLevel) {
        super(topLeft, tileSize, detailLevel);
    }

    /**
     * Returns the tile's height matrix. If the height matrix has not been
     * created so far, this will be done here.
     * @return the tile's height matrix
     */
    private float[][] getHeights() {
        if (heights == null) {
            heights = new float[HEIGHT_RESOLUTION + 2 * HEIGHT_BORDER]
                               [HEIGHT_RESOLUTION + 2 * HEIGHT_BORDER];
        }
        return heights;
    }
    
    @Override
    public void prerender() {
        super.prerender();
        projection = ProjectionFactory.getCurrentProjection();
        Bounds geoBounds = new Bounds(
                projection.getGeoCoordinates(bounds.getTopLeft()),
                projection.getGeoCoordinates(bounds.getBottomRight()), true);
        State.getInstance().getLoadedHeightmap().reduceSection(
                geoBounds, getHeights(), HEIGHT_BORDER);
        minHeight = heights[0][0];
        maxHeight = heights[0][0];
        for (int x = 0; x < heights.length; x++) {
            for (int y = 0; y < heights[x].length; y++) {
                if (heights[x][y] < minHeight) {
                    minHeight = heights[x][y];
                }
                if (heights[x][y] > maxHeight) {
                    maxHeight = heights[x][y];
                }
            }
        }
    }

    /**
     * Renders the tile to the given context, in the process building all needed resources.
     * @param gl
     */
    public void render(GL gl) {
        projection = ProjectionFactory.getCurrentProjection();
        heightmap = State.getInstance().getLoadedHeightmap();
        
        // BUILD TEXTURES IF NECESSARY
        if (!gl.glIsTexture(textureID)) {
            textureID = createTexture(gl, getImage(), false);
        }  
        if (!gl.glIsTexture(grainTextureID)) {
            createGrainTexture(gl);
        }
        // RENDER TILE FROM DISPLAY LIST, OR ELSE BUILD DISPLAY LIST
        if (gl.glIsList(displaylistID)) {            
            gl.glCallList(displaylistID);
        } else {
            displaylistID = gl.glGenLists(1);
            gl.glNewList(displaylistID, GL_COMPILE_AND_EXECUTE);
                gl.glActiveTexture(GL_TEXTURE0);
                gl.glEnable(GL_TEXTURE_2D);
                gl.glBindTexture(GL_TEXTURE_2D, grainTextureID);
                gl.glActiveTexture(GL_TEXTURE1);
                gl.glEnable(GL_TEXTURE_2D);
                gl.glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_DECAL);
                gl.glBindTexture(GL_TEXTURE_2D, textureID);
                
                gl.glColor3f(1,1,1);
                float[] color = new float[3];
                float hRes = HEIGHT_RESOLUTION - 1;
                float stepSize = bounds.getWidth() / hRes;
                Coordinates pos = new Coordinates();
                for (int x = 0; x < hRes; x++) {
                    gl.glBegin(GL_TRIANGLE_STRIP);
                    for (int y = 0; y <= hRes; y++) {            
                        for (int i = 0; i < 2; i++) {
                            pos.setLatitude(bounds.getTop() + y * stepSize);
                            pos.setLongitude(bounds.getLeft() + (x + i) * stepSize);
                            gl.glMultiTexCoord2f(GL_TEXTURE0, (x + i) / (float) GRAIN_RESOLUTION, y / (float) GRAIN_RESOLUTION);
                            gl.glMultiTexCoord2f(GL_TEXTURE1, (x + i) / hRes, y / hRes);
                            float height = heights[x + HEIGHT_BORDER + i][y + HEIGHT_BORDER];
                            getHeightColor(color, height);
                            float shade = getShade(x + i, y, stepSize);
                            gl.glColor3f(color[0] * shade, color[1] * shade, color[2] * shade);
                            gl.glVertex3f(pos.getLongitude(), pos.getLatitude(), height);
                        }
                    }
                    gl.glEnd();
                }
                gl.glDisable(GL_TEXTURE_2D);
                gl.glActiveTexture(GL_TEXTURE0);
                renderPOIs(gl);
            gl.glEndList(); 
        }    
    }
        
    private void renderPOIs(GL gl) {
        Collection<MapElement> elements = State.getInstance().getMapInfo()
                    .queryElements(detailLevel, bounds, false);
        if (elements.size() == 0) {
            return;
        }
        for (MapElement element : elements) {
            if (!element.isInBounds(bounds)) {
                continue;
            }
            if (element instanceof POINode) {
                POINode poi = (POINode) element;
                if ((poi.getInfo().getName() != null)
                     && (poi.getInfo().getName().length() > 0)
                     && (poi.getInfo().getCategory() != OSMType.FAVOURITE)){
                    renderPin(gl, poi.getPos(), COLOR_POI, 1);
                } 
            }
        }
    }
    
    private void renderPin(GL gl, Coordinates position, float[] color, float size) {
        float height = heightmap.getHeight(projection.getGeoCoordinates(position));
        gl.glPushMatrix();
        double[] model = new double[16];
        gl.glGetDoublev(GL_MODELVIEW_MATRIX, model, 0);
        double zoomH = 0.1 / Math.sqrt((model[0] * model[0]) + (model[1] * model[1]) + (model[2] * model[2]));
        double zoomZ = 0.1 / Math.sqrt((model[8] * model[8]) + (model[9] * model[9]) + (model[10] * model[10]));
        gl.glTranslatef(position.getLongitude(), position.getLatitude(), height);
        gl.glScaled(zoomH * size, zoomH * size, zoomZ * size);
        gl.glDisable(GL_TEXTURE_2D);
        
        gl.glRotatef(20, 0.3f, 1, 0);
        
        GLU glu = new GLU();
        GLUquadric quadric = glu.gluNewQuadric();
        //glu.gluQuadricNormals(quadric, GLU.GLU_FLAT);
        gl.glColor3f(0.5f, 0.5f, 0.5f);
        gl.glEnable(GL_LIGHTING);
        glu.gluCylinder(quadric, 0.03, 0.03, 0.6f, 5, 1);
        gl.glTranslatef(0, 0, 0.6f);
        
        gl.glColor3f(color[0], color[1], color[2]);
        gl.glColorMaterial(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE);
        glu.gluSphere(quadric, 0.12, 8, 8);
        //glu.gluCylinder(quadric, 0.2, 0.1, 0.5, 8, 1);
        gl.glDisable(GL_LIGHTING);
        glu.gluDeleteQuadric(quadric);     
        gl.glPopMatrix();  
    }
    
    private float getShade(int x, int y, float stepSize) {
        float height = heights[x][y];
        float min = height, max = height;
        for (int i = -1; i < 2; i++) {
            for (int k = -1; k < 2; k++) {
                height = heights[x + HEIGHT_BORDER + i][y + HEIGHT_BORDER + k];
                min = Math.min(min, height);
                max = Math.max(max, height);
            }
        }       
        return 1 - MathUtil.clip(SLOPE_SHADE_FACTOR * (max - min) / stepSize, 0, MAX_SLOPE_SHADE_VALUE);
    }

    /**
     * Creates the random noise texture that will be used as detail texture on the tiles.
     * @param gl
     */
    private static void createGrainTexture(GL gl) {
        BufferedImage grainImage = new BufferedImage(GRAIN_RESOLUTION, GRAIN_RESOLUTION,
                BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < GRAIN_RESOLUTION; x++) {
            for (int y = 0; y < GRAIN_RESOLUTION; y++) { 
                float random = (float)(Math.random() * GRAIN_INTENSITY) + (1 - GRAIN_INTENSITY);
                grainImage.setRGB(x, y, (new Color(random, random, random)).getRGB());
            }
        }
        grainTextureID = createTexture(gl, grainImage, true);
    }
    
    /**
     * Returns a color value corresponding to the given height.
     * The color is interpolated from the color stages found in
     * <code>COLOR_STAGES</code> and <code>COLORS</code>.
     * @param color the output color
     * @param height the height that is to be color encoded
     */
    private static void getHeightColor(float[] color, float height) {
        int col1 = COLOR_STAGES.length - 1, col2 = col1;
        for (int i = 1; i < COLOR_STAGES.length; i++) {
            if (height < COLOR_STAGES[i]) {
                col1 = i - 1;
                col2 = i;
                break;
            }
        }        
        float ratio = (col1 == col2) ? 0 : (height - COLOR_STAGES[col1])
                / (COLOR_STAGES[col2] - COLOR_STAGES[col1]);
        for (int i = 0; i < 3; i++) {
            color[i] = (COLORS[col1][i] + (COLORS[col2][i] - COLORS[col1][i]) * ratio) / 255;
        }
        
    }
    
    /**
     * Creates a texture in the given OpenGL context.
     * @param gl the context
     * @param image the RGB or RGBA image serving as texture source
     * @param repeat whether the texture should should have repeat mode activated
     * @return the texture name (ID)
     */
    private static int createTexture(GL gl, BufferedImage image, boolean repeat) {
        if (image == null) {
            return -1;
        }
        final int[] tmp = new int[1];
        gl.glGenTextures(1, tmp, 0);
        int tex = tmp[0];
        gl.glBindTexture(GL_TEXTURE_2D, tex);
        int[] data = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        ByteBuffer dest = ByteBuffer.allocate(data.length * BufferUtil.SIZEOF_INT); // TODO direct?
        dest.order(ByteOrder.nativeOrder());
        dest.asIntBuffer().put(data, 0, data.length);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        int wrapMode = (repeat) ? GL_REPEAT : GL_CLAMP_TO_EDGE;
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, wrapMode);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, wrapMode);
        int oglFormat = (image.getType() == BufferedImage.TYPE_INT_ARGB) ? GL_RGBA : GL_RGB;
        gl.glTexImage2D(GL_TEXTURE_2D, 0, oglFormat, image.getWidth(), image.getHeight(),
                0, GL_BGRA, GL_UNSIGNED_BYTE, dest);
        //(new GLU()).gluBuild2DMipmaps(GL.GL_TEXTURE_2D, GL.GL_RGB, image.getWidth(), image.getHeight(), GL.GL_BGRA, GL.GL_UNSIGNED_BYTE, dest);       
        return tex;
    }
    
    /**
     * Tests whether this tile is in the given view frustum.
     * If the tile was not loaded so far, the test will assume
     * a minimum height of -500 and a maximum height of 8000.
     * @param frustum the current view frustum
     * @return whether the tile is in the frustum
     */
    boolean isInFrustum(Frustum frustum) {
        Coordinates center = bounds.getCenter();
        return (frustum == null) || frustum.isBoxWithin(
                new float[] {center.getLongitude(), center.getLatitude(),
                             0.5f * (minHeight + maxHeight) },
                new float[] {center.getLongitude() - bounds.getLeft(), 
                             center.getLatitude() - bounds.getTop(),
                             0.5f * (maxHeight - minHeight)});
    }

    /**
     * Frees all resources used exclusively by the tile that were stored
     * in the GPU RAM, as those will not be freed by the garbage collector.
     * @param gl the current OpenGL context
     */
    public void freeResources(GL gl) {
        if (gl.glIsTexture(textureID)) {
            gl.glDeleteTextures(1, new int[] { textureID }, 0);
        }
        if (gl.glIsTexture(heightTextureID)) {
            gl.glDeleteTextures(1, new int[] { heightTextureID }, 0);
        }
        if (gl.glIsList(displaylistID)) {
            gl.glDeleteLists(displaylistID, 1);
        }
    }
   
    public void renderBox(GL gl) {
        gl.glDisable(GL_TEXTURE_2D);
        int c1 = Math.abs(this.hashCode()) % 256;
        int c2 = Math.abs(getImage().hashCode()) % 256;
        gl.glColor4f(c1 / 256f, c2 / 256f, ((c1 + c2) * 34) % 256 / 256f, 0.3f);
        gl.glPushMatrix();
        gl.glTranslatef(bounds.getLeft(), bounds.getTop(), minHeight);
        gl.glScalef(bounds.getWidth(), bounds.getHeight(), maxHeight - minHeight);
        gl.glBegin(GL_QUADS);
            gl.glVertex3f(0, 0, 0);
            gl.glVertex3f(0, 1, 0);
            gl.glVertex3f(1, 1, 0);
            gl.glVertex3f(1, 0, 0);
            
            gl.glVertex3f(0, 0, 1);
            gl.glVertex3f(0, 1, 1);
            gl.glVertex3f(1, 1, 1);
            gl.glVertex3f(1, 0, 1);
        gl.glEnd();
        gl.glColor4f(1,1,0,0.2f);
        gl.glBegin(GL_QUAD_STRIP);
            gl.glVertex3f(0, 0, 0);
            gl.glVertex3f(0, 0, 1);             
            gl.glVertex3f(0, 1, 0);
            gl.glVertex3f(0, 1, 1);           
            gl.glVertex3f(1, 1, 0);
            gl.glVertex3f(1, 1, 1);            
            gl.glVertex3f(1, 0, 0);
            gl.glVertex3f(1, 0, 1);     
            gl.glVertex3f(0, 0, 0);
            gl.glVertex3f(0, 0, 1);
        gl.glEnd();
        gl.glPopMatrix();
    }
    
}
