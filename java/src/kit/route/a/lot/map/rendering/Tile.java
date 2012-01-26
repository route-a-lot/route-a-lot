package kit.route.a.lot.map.rendering;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Collection;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.OSMType;
import kit.route.a.lot.common.Projection;
import kit.route.a.lot.common.WayInfo;
import kit.route.a.lot.controller.State;
import kit.route.a.lot.map.Area;
import kit.route.a.lot.map.MapElement;
import kit.route.a.lot.map.Node;
import kit.route.a.lot.map.Street;


public class Tile {

    public static final float BASE_TILE_DIM = 0.005f;
    //private static Logger logger = Logger.getLogger(Tile.class);

    private Coordinates topLeft;
    private Coordinates bottomRight;
    private int detail;
    private BufferedImage data;
    private int width;
    private int height;
    private static int num = 0;

    /**
     * Creates an new (empty) tile using the defined resolution.
     * 
     * @param topLeft
     *            the northwestern corner of the tile
     * @param bottomRight
     *            the southeastern corner of the tile
     * @param detail
     *            the desired level of detail
     * @param width
     *            the width (in pixels) of the tile output
     * @param height
     *            the height (in pixels) of the tile output
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
     * Creates an new (empty) tile using a calculated resolution
     * 
     * @param topLeft
     *            the northwestern corner of the tile
     * @param bottomRight
     *            the southeastern corner of the tile
     * @param detail
     *            the desired level of detail
     */
    public Tile(Coordinates topLeft, Coordinates bottomRight, int detail) {
        this(topLeft, bottomRight, detail, 0, 0);
        width = (int) Math.abs(topLeft.getLongitude() - bottomRight.getLongitude());
        height = (int) Math.abs(topLeft.getLatitude() - bottomRight.getLatitude());
    }

    public void prerender(State state) {
        reset();

        // Graphics2D graphics = data.createGraphics();
        // int c1 = Math.abs(this.hashCode()) % 256;
        // int c2 = Math.abs(data.hashCode()) % 256;
        // int c3 = ((c1 + c2) * 34) % 256;
        // graphics.setColor(new Color(c1, c2, c3));
        // graphics.setStroke(new BasicStroke(3));
        // graphics.fillRect(0, 0, this.width, this.height);
        // graphics.drawLine(0, this.height, this.width, 0);
        // graphics.setColor(Color.BLACK);
        // graphics.setFont(new Font("Arial", Font.BOLD, 32));
        // graphics.drawChars((new Integer(num)).toString().concat("    ").toCharArray(), 0, 5, 5, 50);
        // num++;
        if (++num % 100 == 0) {
            // System.out.println("Rendering tile " + num);
        }

        Graphics2D graphics = data.createGraphics();

        //long start = System.nanoTime();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        Collection<MapElement> map = state.getLoadedMapInfo().getBaseLayer(detail, topLeft, bottomRight);

        //long middle = System.nanoTime();

        for (MapElement element : map) {
            if (element instanceof Area) {
                draw((Area) element, graphics);
            }
        }

        for (MapElement element : map) {
            if (element instanceof Node) {
                draw((Node) element, graphics);
            } else if (element instanceof Street) {
                draw((Street) element, false, graphics);
            }
        }

        for (MapElement element : map) {
            if (element instanceof Street) {
                draw((Street) element, true, graphics);
            }
        }
        
        for (MapElement element : map) {
            if (element instanceof Street) {
                drawStreetArrows((Street) element, graphics);
            }
        }

        //long end = System.nanoTime();
        //double mapElements = (middle - start) / 1000000;
        //double drawing = (end - middle) / 1000000;
        // System.out.println("time for mapElements " + mapElements + "ms; for drawing " + drawing + "ms");

        graphics.dispose();

    }

    /**
     * (Re-)Creates the tile image, filling it with a background color.
     */
    protected void reset() {
        data =
                new BufferedImage(width / Projection.getZoomFactor(detail), height
                        / Projection.getZoomFactor(detail), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = data.createGraphics();
        graphics.setColor(new Color(210, 230, 190));
        graphics.fillRect(0, 0, this.width, this.height);
    }

    /**
     * Returns the rendered tile image. If nothing was rendered so far, returns an empty (background color
     * filled) tile image.
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
     * @param data
     *            new tile image
     */
    protected void setData(BufferedImage data) {
        this.data = data;
        if (data != null) {
            this.width = data.getWidth();
            this.height = data.getHeight();
        }
    }

    protected void draw(MapElement element) {
        throw new UnsupportedOperationException("Can't draw an element with type "
                + element.getClass().toString());
    }

    /**
     * Draws a regular node on the tile.
     * 
     * @param poi
     *            the node to be drawn
     */
    protected void draw(Node node, Graphics2D graphics) {
        int size = 3;

        Coordinates localCoordinates = Renderer.getLocalCoordinatesFromGlobalCoordinates(node.getPos(), topLeft, detail);
        graphics.setColor(Color.LIGHT_GRAY);
        graphics.fillOval((int) localCoordinates.getLongitude() - size / 2,
                (int) localCoordinates.getLatitude() - size / 2, size, size);
    }


    /**
     * Draws an area on the tile.
     * 
     * @param area
     *            the area to be drawn.
     */
    protected void draw(Area area, Graphics2D graphics) {
        int[] xPoints, yPoints;
        int nPoints;
        Node[] nodes = area.getNodes();
        nPoints = nodes.length;
        xPoints = new int[nPoints];
        yPoints = new int[nPoints];

        for (int i = 0; i < nPoints; i++) {
            Coordinates curCoordinates = Renderer.getLocalCoordinatesFromGlobalCoordinates(nodes[i].getPos(), topLeft, detail);
            xPoints[i] = (int) curCoordinates.getLongitude();
            yPoints[i] = (int) curCoordinates.getLatitude();
        }

        WayInfo wayInfo = area.getWayInfo();

        // TODO would be nice not to have that hardcoded here

        if (wayInfo.isBuilding()) {
            graphics.setColor(Color.GRAY);
        } else if (wayInfo.isArea()) {
            switch (wayInfo.getType()) {
                case OSMType.NATURAL_WOOD:
                    graphics.setColor(Color.GREEN);
                    break;
                default:
//                    // System.out.println("Unknown area type in tile rendering: " + wayInfo.getType());
                    return;
//                    graphics.setColor(Color.WHITE);
            }
        } else {
            return;
//            graphics.setColor(Color.WHITE);
        }

        graphics.fillPolygon(xPoints, yPoints, nPoints);

        graphics.setStroke(new BasicStroke(1));
        graphics.setColor(Color.BLACK);
        graphics.drawPolygon(xPoints, yPoints, nPoints);
    }

    /**
     * Draws a street on the tile, taking the street type into consideration.
     * 
     * @param street
     *            the street to be drawn
     */
    protected void draw(Street street, boolean top, Graphics2D graphics) {
        Node[] nodes = street.getNodes();
        int nPoints = nodes.length;
        int[] xPoints = new int[nPoints];
        int[] yPoints = new int[nPoints];

        WayInfo wayInfo = street.getWayInfo();
        int basicSize = 10;

        // set colors
        if (top) {
            graphics.setColor(Color.WHITE);
            if (!wayInfo.isRoutable()) {
                graphics.setColor(Color.LIGHT_GRAY);
            }
            switch (wayInfo.getType()) {
                case OSMType.HIGHWAY_MOTORWAY:
                case OSMType.HIGHWAY_MOTORWAY_JUNCTION:
                case OSMType.HIGHWAY_MOTORWAY_LINK:
                    graphics.setColor(new Color(0, 51, 153));
                    break;
                case OSMType.HIGHWAY_PRIMARY:
                case OSMType.HIGHWAY_PRIMARY_LINK:
                    graphics.setColor(new Color(255, 204, 51));
                    break;
            }
        } else {
            graphics.setColor(Color.DARK_GRAY);
        }

        // set size
        switch (wayInfo.getType()) {
            case OSMType.HIGHWAY_MOTORWAY:
            case OSMType.HIGHWAY_MOTORWAY_JUNCTION:
            case OSMType.HIGHWAY_MOTORWAY_LINK:
                basicSize = 40;
                break;
            case OSMType.HIGHWAY_PRIMARY:
            case OSMType.HIGHWAY_PRIMARY_LINK:
                basicSize = 30;
                break;
            case OSMType.HIGHWAY_SECONDARY:
            case OSMType.HIGHWAY_SECONDARY_LINK:
                basicSize = 22;
                break;
            case OSMType.HIGHWAY_TERTIARY:
            case OSMType.HIGHWAY_TERTIARY_LINK:
            case OSMType.HIGHWAY_RESIDENTIAL:
            case OSMType.HIGHWAY_LIVING_STREET:
                basicSize = 18;
            case OSMType.HIGHWAY_CYCLEWAY:
                basicSize = 15;
                break;
        }
        int size = basicSize / Projection.getZoomFactor(detail);
        if (!top) {
            graphics.setStroke(new BasicStroke(size + 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        } else {
            graphics.setStroke(new BasicStroke(size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        }

        for (int i = 0; i < nPoints; i++) {
            Coordinates curCoordinates = getLocalCoordinatesFromGlobalCoordinates(nodes[i].getPos());
            xPoints[i] = (int) curCoordinates.getLongitude();
            yPoints[i] = (int) curCoordinates.getLatitude();
        }
        
        graphics.drawPolyline(xPoints, yPoints, nPoints);
        
        
    }
    
    protected void drawStreetArrows(Street street, Graphics2D graphics) {
        if (detail > 1 || street.getWayInfo().getOneway() == WayInfo.ONEWAY_NO) {
            return;
        }
        
        Node[] nodes = street.getNodes();
        int nPoints = nodes.length;
        
        float arrowLength = 12.f / Projection.getZoomFactor(detail);
        float headLength = arrowLength / 2;
        double arrowDistance = 12 * arrowLength;
        double currentDistance = 0;

        graphics.setColor(new Color(217, 192, 129));
        graphics.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));

        for (int i = 1; i < nPoints; i++) {
            Coordinates from = getLocalCoordinatesFromGlobalCoordinates(nodes[i-1].getPos());
            Coordinates to = getLocalCoordinatesFromGlobalCoordinates(nodes[i].getPos());
            currentDistance += Street.getDistanceProj(from, to);

            if (currentDistance > arrowDistance) {
                currentDistance = 0;

                Coordinates vector = new Coordinates();
                vector.setLatitude((to.getLatitude() - from.getLatitude()));
                vector.setLongitude((to.getLongitude() - from.getLongitude()));
                Coordinates edgeMiddle = new Coordinates();
                edgeMiddle.setLatitude(from.getLatitude() + vector.getLatitude() / 2);
                edgeMiddle.setLongitude(from.getLongitude() + vector.getLongitude() / 2);
                vector = normalizeVector(vector);

                Coordinates arrowStart = new Coordinates();
                arrowStart.setLatitude(edgeMiddle.getLatitude() - vector.getLatitude() * arrowLength);
                arrowStart.setLongitude(edgeMiddle.getLongitude() - vector.getLongitude() * arrowLength);
                Coordinates arrowEnd = new Coordinates();
                arrowEnd.setLatitude(edgeMiddle.getLatitude() + vector.getLatitude() * arrowLength);
                arrowEnd.setLongitude(edgeMiddle.getLongitude() + vector.getLongitude() * arrowLength);

                Coordinates headLeft = new Coordinates();
                Coordinates rotatedVector = normalizeVector(rotateVector(vector, 210));
                headLeft.setLatitude(arrowEnd.getLatitude() + rotatedVector.getLatitude() * headLength);
                headLeft.setLongitude(arrowEnd.getLongitude() + rotatedVector.getLongitude() * headLength);
                Coordinates headRight = new Coordinates();
                rotatedVector = normalizeVector(rotateVector(vector, 150));
                headRight.setLatitude(arrowEnd.getLatitude() + rotatedVector.getLatitude() * headLength);
                headRight.setLongitude(arrowEnd.getLongitude() + rotatedVector.getLongitude() * headLength);

                graphics.drawLine((int) arrowStart.getLongitude(), (int) arrowStart.getLatitude(), (int) arrowEnd.getLongitude(), (int) arrowEnd.getLatitude());
                if (street.getWayInfo().getOneway() == WayInfo.ONEWAY_OPPOSITE) {
                    arrowEnd = arrowStart;
                }
                graphics.drawLine((int) arrowEnd.getLongitude(), (int) arrowEnd.getLatitude(), (int) headLeft.getLongitude(), (int) headLeft.getLatitude());
                graphics.drawLine((int) arrowEnd.getLongitude(), (int) arrowEnd.getLatitude(), (int) headRight.getLongitude(), (int) headRight.getLatitude());
            }
        }
    }
    
    private Coordinates getLocalCoordinatesFromGlobalCoordinates(Coordinates coordinates) {
        return Renderer.getLocalCoordinatesFromGlobalCoordinates(coordinates, topLeft, detail);
    }
    
    private Coordinates normalizeVector(Coordinates vector) {
        Coordinates newVector = new Coordinates();
        float vectorLength = (float) Math.sqrt(Math.pow(vector.getLatitude(), 2) + Math.pow(vector.getLongitude(), 2));
        newVector.setLatitude(vector.getLatitude() / vectorLength);
        newVector.setLongitude(vector.getLongitude() / vectorLength);
        return newVector;
    }
    
    private Coordinates rotateVector(Coordinates vector, int angle) {
        Coordinates rotatedVector = new Coordinates();
        rotatedVector.setLongitude((float) ((Math.cos(angle * Math.PI / 180) * vector.getLongitude() - Math.sin(angle * Math.PI / 180) * vector.getLatitude()) * 180 / Math.PI));
        rotatedVector.setLatitude((float) ((Math.sin(angle * Math.PI / 180) * vector.getLongitude() + Math.cos(angle * Math.PI / 180) * vector.getLatitude()) * 180 / Math.PI));
        return rotatedVector;
    }

    /**
     * Derives a hash code using the tiles defining attributes' values, such as the origin coordinates and the
     * level of detail.
     * 
     * @return the hash code
     */
    @Override
    public int hashCode() {
        // EXTEND: better hash code derivation
        return (int) (Math.round((topLeft.getLongitude() + topLeft.getLatitude() * 100) * 1000) + detail);
    }

    public static long getSpecifier(Coordinates topLeft, int detail) {
        return (long) Math.floor((topLeft.getLongitude() + topLeft.getLatitude() * 10000) * 100000) + detail;
    }

    public long getSpecifier() {
        return getSpecifier(topLeft, detail);
    }

}
