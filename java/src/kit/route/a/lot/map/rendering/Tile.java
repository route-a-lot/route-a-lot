package kit.route.a.lot.map.rendering;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Set;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.controller.State;
import kit.route.a.lot.map.Area;
import kit.route.a.lot.map.Edge;
import kit.route.a.lot.map.MapElement;
import kit.route.a.lot.map.Node;


public class Tile {

    public static final double BASE_TILE_DIM = 0.001;
    
    private Coordinates topLeft;
    private Coordinates bottomRight; // DISCUSS: keep or drop?
    private int detail;
    private BufferedImage data;
    private int width; // DISCUSS: keep or drop?
    private int height;

    /**
     * Creates an new (empty) tile using the defined resolution.
     * 
     * @param topLeft the northwestern corner of the tile
     * @param bottomRight the southeastern corner of the tile
     * @param detail the desired level of detail
     * @param width the width (in pixels) of the tile output
     * @param height the height (in pixels) of the tile output
     */
    public Tile(Coordinates topLeft, Coordinates bottomRight, int detail, int width, int height) {
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
        this.detail = detail;
        this.width = width;
        this.height = height;
        this.data = null;      
    }
    
    /**
     * Creates an new (empty) tile using the default resolution (350px*350px).
     * 
     * @param topLeft the northwestern corner of the tile
     * @param bottomRight the southeastern corner of the tile
     * @param detail the desired level of detail
     */
    public Tile(Coordinates topLeft, Coordinates bottomRight, int detail) {
        this(topLeft, bottomRight, detail, 350, 350);
    }

    public void prerender() {
        reset();
        List<MapElement> map = State.getInstance().loadedMapInfo.getBaseLayer(detail, topLeft, bottomRight);
        for (MapElement element: map) {
            draw(element);
        }
    }
    
    /**
     * (Re-)Creates the tile image, filling it with a background color.
     */
    protected void reset() {
        data = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        data.getGraphics().setColor(Color.LIGHT_GRAY);
        data.getGraphics().fillRect(0, 0, this.width, this.height);
    }

    /**
     * Returns the rendered tile image. If nothing was rendered so far, returns
     * an empty (background color filled) tile image.
     * 
     * @return the tile image
     */
    protected BufferedImage getData() {
        if (this.data == null) {
            reset();
        }
        return this.data;
    }
    
    /**
     * Sets the tile image.
     * 
     * @param data new tile image
     */
    protected void setData(BufferedImage data) {
        this.data = data;
        if (data != null) {
            this.width = data.getWidth();
            this.height = data.getHeight();
        }
    }

    protected void draw(MapElement element) {
        throw new UnsupportedOperationException(
                "Can't draw an element with type " + element.getClass().toString());
    }
    
    /**
     * Draws a regular node on the tile.
     * 
     * @param poi the node to be drawn
     */
    protected void draw(Node node) {
    }

    /**
     * Draws an area on the tile.
     * 
     * @param area the area to be drawn.
     */
    protected void draw(Area area) {
    }

    /**
     * Draws a single street edge on the tile,
     * taking the street type into consideration.
     * 
     * @param edge the edge to be drawn
     */
    protected void draw(Edge edge) {
    }
    
    /**
     * Derives a hash code using the tiles defining attributes' values,
     * such as the origin coordinates and the level of detail.
     * 
     * @return the hash code
     */
    @Override
    public int hashCode() {
        // EXTEND: better hash code derivation
        return (int) (Math.round((topLeft.getLon() + topLeft.getLat() * 100) * 1000) + detail);
    }
}
