package kit.ral.map.rendering;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import javax.imageio.ImageIO;

import kit.ral.common.Bounds;
import kit.ral.common.Coordinates;
import kit.ral.common.description.Address;
import kit.ral.common.description.OSMType;
import kit.ral.common.description.WayInfo;
import kit.ral.common.projection.Projection;
import kit.ral.common.util.MathUtil;
import kit.ral.controller.State;
import kit.ral.map.Area;
import kit.ral.map.MapElement;
import kit.ral.map.MapElementComparator;
import kit.ral.map.Node;
import kit.ral.map.POINode;
import kit.ral.map.Street;
import kit.ral.map.info.MapInfo;
import kit.ral.routing.Precalculator;

import org.apache.log4j.Logger;


public class Tile {

    private static Logger logger = Logger.getLogger(Tile.class);
    private static final int POI_SIZE = 5;
    private static final int MAX_STREET_DETAIL_LEVEL = 3;
    private static final int MAX_MINOR_STREET_SHADOW_LEVEL = 4;
    private static final int AREA_ALPHA_VALUE = 100; //140
    private static final Color
        POI_BORDER_COLOR = new Color(196, 161, 80),
        POI_COLOR = new Color(229, 189, 100),
        BUILDING_COLOR = new Color(200, 200, 200),
        BUILDING_BORDER_COLOR = new Color(150, 150, 150);
    private static final boolean USE_PATTERNS = true;
    private static final Paint FOREST_PATTERN, WATER_PATTERN;
    
    static {
        if (USE_PATTERNS) {
            BufferedImage forestPattern, waterPattern;
            try {
                forestPattern = ImageIO.read(ClassLoader.getSystemResource("pattern_forest.png"));
                waterPattern = ImageIO.read(ClassLoader.getSystemResource("pattern_water.png"));
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
            FOREST_PATTERN = new TexturePaint(forestPattern, new Rectangle2D.Float(0, 0, 62, 62));
            WATER_PATTERN = new TexturePaint(waterPattern, new Rectangle2D.Float(0, 0, 64, 64));
        } else {
            FOREST_PATTERN = new Color(126, 159, 107);
            WATER_PATTERN = new Color(135, 168, 198);
        }     
    }

    protected Bounds bounds;
    protected int detailLevel, tileSize;

    private BufferedImage image = null;
    private boolean finished = false;

    private MapInfo mapInfo;

    /** Temporary variable (only guaranteed to be valid when rendering) */
    private Graphics2D g;
    

    /**
     * Creates an new (empty) tile using a calculated resolution
     * 
     * @param topLeft
     *            the north western corner of the tile
     * @param bottomRight
     *            the south eastern corner of the tile
     * @param detailLevel
     *            the desired level of detail
     */
    public Tile(Coordinates topLeft, int tileSize, int detailLevel) {
        this.bounds = new Bounds(topLeft, topLeft.clone().add(tileSize, tileSize));
        this.detailLevel = detailLevel;
        this.tileSize = tileSize;
        this.mapInfo = State.getInstance().getMapInfo();
    }

    public Tile(Coordinates topLeft, int tileSize, int detailLevel, MapInfo mapInfo) {
        this(topLeft, tileSize, detailLevel);
        this.mapInfo = mapInfo;
    }


    /**
     * Returns the rendered tile image. If nothing was rendered so far, returns an empty tile image.
     * 
     * @return the tile image
     */
    protected BufferedImage getImage() {
        if (image == null) {
            int dim = tileSize / Projection.getZoomFactor(detailLevel);
            image = new BufferedImage(dim, dim, BufferedImage.TYPE_INT_ARGB);
        }
        return image;
    }

    public boolean isFinished() {
        return finished;
    }

    public void markAsFinished() {
        finished = true;
    }

    public void prerender() {
        // query quadtree elements, exact=true is generally faster (by more than 30%)
        Set<MapElement> map = mapInfo.queryElements(detailLevel, bounds, true);

        if (map.size() == 0) {
            return;
        }

        // prepare image
        image = null;
        g = getImage().createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // color tile background
//         int c1 = Math.abs(this.hashCode()) % 256;
//         int c2 = Math.abs(getImage().hashCode()) % 256;
//         graphics.setColor(new Color(c1, c2, ((c1 + c2) * 34) % 256, 64));
//         graphics.fillRect(0, 0, tileSize / Projection.getZoomFactor(detailLevel), tileSize /
//         Projection.getZoomFactor(detailLevel));


        // draw areas, but no buildings or water
        for (MapElement element : map) {
            if ((element instanceof Area) && (!((Area) element).getWayInfo().isBuilding())
                && (((Area) element).getWayInfo().getType() != OSMType.NATURAL_WATER)) {
                draw((Area) element);
            }
        }

        // set alpha for everything drawn previously
        g.setComposite(AlphaComposite.DstIn);
        g.setColor(new Color(0, 0, 0, AREA_ALPHA_VALUE));
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        g.setComposite(AlphaComposite.SrcOver);
  
        //Style style = new Style(this);
        
        for (MapElement element : map) {
            if (element instanceof Street) {                
                draw((Street) element, true);
                //style.drawElement(element, Style.STREET_SECONDARY + 16 * Style.BRIDGE);
            } else if ((element instanceof Area) && (((Area) element).getWayInfo().isBuilding())) {
                draw((Area) element);
                //style.drawElement(element, Style.BUILDING);
            }
        }

        if (detailLevel <= MAX_STREET_DETAIL_LEVEL) {
            for (MapElement element : map) {
                if (element instanceof Street) {
                    drawStreetArrows((Street) element);
                    drawStreetNames((Street) element);
                }
            }
        }   
        
        State.getInstance().getActiveRenderer();
        if (Renderer.drawAreas) {
            Precalculator.drawAreas(bounds, detailLevel, g);
        }
        
        g.dispose();

    }

    public void drawPOIs() {
        Set<MapElement> elements = mapInfo.queryElements(detailLevel, bounds, false);
        if (elements.size() == 0) {
            return;
        }
        g = getImage().createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (MapElement element : elements) {
            if (element instanceof POINode) {
                POINode poi = (POINode) element;
                if ((poi.getInfo().getName() != null) && (poi.getInfo().getName().length() > 0)
                        && poi.getInfo().getCategory() != OSMType.FAVOURITE) {
                    g.setColor(POI_BORDER_COLOR);
                    drawPoint(poi.getPos(), POI_SIZE + 2);
                    g.setColor(POI_COLOR);
                    drawPoint(poi.getPos(), POI_SIZE); 
                }
                      
            } 
        }
        g.dispose();
    }

    /**
     * Draws an area on the tile.
     * 
     * @param area
     *            the area to be drawn.
     */
    private void draw(Area area) {
        int[] xPoints, yPoints;
        int nPoints;
        Node[] nodes = area.getNodes();
        nPoints = nodes.length;
        xPoints = new int[nPoints];
        yPoints = new int[nPoints];

        for (int i = 0; i < nPoints; i++) {
            Coordinates curCoordinates = getLocalCoordinates(nodes[i].getPos());
            xPoints[i] = (int) curCoordinates.getLongitude();
            yPoints[i] = (int) curCoordinates.getLatitude();
        }

        WayInfo wayInfo = area.getWayInfo();

        // TODO would be nice not to have that hardcoded here
        if (wayInfo.isBuilding()) {
            g.setStroke(new BasicStroke(1));
            g.setColor(BUILDING_COLOR);
        } else if (wayInfo.isArea()) {
            switch (wayInfo.getType()) {
                case OSMType.LANDUSE_FOREST:
                case OSMType.NATURAL_WOOD:
                    g.setPaint(FOREST_PATTERN);
                    break;
                case OSMType.NATURAL_WATER:
                    g.setPaint(WATER_PATTERN);
                    break;
                default: return;
            }
        }
        g.fillPolygon(xPoints, yPoints, nPoints);
        if (wayInfo.isBuilding()) {
            g.setColor(BUILDING_BORDER_COLOR);
            g.drawPolygon(xPoints, yPoints, nPoints); 
        } 
    }

    /**
     * Draws a street on the tile, taking the street type into consideration.
     * 
     * @param street
     *            the street to be drawn
     */
    private void draw(Street street, boolean top) {
        Node[] nodes = getRelevantNodesForStreet(street.getNodes(), street.getDrawingSize());
        int[] xPoints = new int[nodes.length];
        int[] yPoints = new int[nodes.length];
        WayInfo wayInfo = street.getWayInfo();

        // set colors
        if (top) {
            g.setColor(Color.WHITE);
            if (!wayInfo.isRoutable()) {
                g.setColor(Color.LIGHT_GRAY);
            }
            switch (wayInfo.getType()) {
                case OSMType.HIGHWAY_MOTORWAY:
                case OSMType.HIGHWAY_MOTORWAY_JUNCTION:
                case OSMType.HIGHWAY_MOTORWAY_LINK:
                    g.setColor(new Color(0, 51, 153));
                    break;
                case OSMType.HIGHWAY_PRIMARY:
                case OSMType.HIGHWAY_PRIMARY_LINK:
                    g.setColor(new Color(255, 204, 51));
                    break;
            }
        } else {
            g.setColor(Color.DARK_GRAY);
        }
        
        // set size
        float size = street.getDrawingSize() / (float) Projection.getZoomFactor(detailLevel);
        g.setStroke(new BasicStroke(size + ((top) ? 0
                : 1f / (float) Math.pow(detailLevel + 1, 0.8)),
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 0; i < nodes.length; i++) {
            Coordinates curCoordinates = getLocalCoordinates(nodes[i].getPos());
            xPoints[i] = (int) curCoordinates.getLongitude();
            yPoints[i] = (int) curCoordinates.getLatitude();
        }
        g.drawPolyline(xPoints, yPoints, nodes.length);
    }

    private void drawPoint(Coordinates globalCoordinates, int size) {
        Coordinates localCoordinates = getLocalCoordinates(globalCoordinates).add(-size/2, -size/2);
        g.fillOval((int) localCoordinates.getLongitude(), (int) localCoordinates.getLatitude(), size, size);
    }

    private void drawStreetArrows(Street street) {
        if (detailLevel > 1 || street.getWayInfo().getOneway() == WayInfo.ONEWAY_NO) {
            return;
        }

        Node[] nodes = street.getNodes();
        int nPoints = nodes.length;

        float arrowLength = 12.f / Projection.getZoomFactor(detailLevel);
        float headLength = arrowLength / 2;
        double arrowDistance = 12 * arrowLength;
        double currentDistance = 0;

        g.setColor(new Color(217, 192, 129));
        g.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));

        for (int i = 1; i < nPoints; i++) {
            Coordinates from = getLocalCoordinates(nodes[i - 1].getPos());
            Coordinates to = getLocalCoordinates(nodes[i].getPos());
            currentDistance += Coordinates.getDistance(from, to);

            if (currentDistance > arrowDistance) {
                currentDistance = 0;
                Coordinates vector = to.clone().subtract(from);
                Coordinates edgeMiddle = vector.clone().scale(0.5f).add(from);
                vector.normalize();
                Coordinates arrowStart = vector.clone().scale(arrowLength).subtract(edgeMiddle).invert();
                Coordinates arrowEnd = vector.clone().scale(arrowLength).add(edgeMiddle);
                if (street.getWayInfo().getOneway() == WayInfo.ONEWAY_OPPOSITE) {
                    Coordinates tmp = arrowEnd;
                    arrowEnd = arrowStart;
                    arrowStart = tmp;
                }
                Coordinates headLeft = vector.clone().rotate(210).normalize().scale(headLength).add(arrowEnd);
                Coordinates headRight = vector.clone().rotate(150).normalize().scale(headLength).add(arrowEnd);

                g.drawLine((int) arrowStart.getLongitude(), (int) arrowStart.getLatitude(),
                        (int) arrowEnd.getLongitude(), (int) arrowEnd.getLatitude());
                g.drawLine((int) arrowEnd.getLongitude(), (int) arrowEnd.getLatitude(),
                        (int) headLeft.getLongitude(), (int) headLeft.getLatitude());
                g.drawLine((int) arrowEnd.getLongitude(), (int) arrowEnd.getLatitude(),
                        (int) headRight.getLongitude(), (int) headRight.getLatitude());
            }
        }
    }

    private void drawStreetNames(Street street) {
        Address curAddress = street.getWayInfo().getAddress();
        if (detailLevel > 3 || curAddress == null) {
            return;
        }
        String curStreetName = curAddress.getStreet();
        
        // Test code for sorting
        //MapElementComparator com = new MapElementComparator();
        //curStreetName = "" + street.getWayInfo().getType() + " / " +  com.getLayer(street);
        
        if (curStreetName == null || curStreetName.equals("")) {
            return;
        }

        if (detailLevel == 3 && street.getWayInfo().getType() != OSMType.HIGHWAY_PRIMARY
                && street.getWayInfo().getType() != OSMType.HIGHWAY_MOTORWAY) {
            return;
        }

        if (detailLevel == 2 && street.getWayInfo().getType() != OSMType.HIGHWAY_PRIMARY
                && street.getWayInfo().getType() != OSMType.HIGHWAY_MOTORWAY
                && street.getWayInfo().getType() != OSMType.HIGHWAY_SECONDARY) {
            return;
        }

        if (detailLevel == 1 && street.getWayInfo().getType() != OSMType.HIGHWAY_PRIMARY
                && street.getWayInfo().getType() != OSMType.HIGHWAY_MOTORWAY
                && street.getWayInfo().getType() != OSMType.HIGHWAY_SECONDARY
                && street.getWayInfo().getType() != OSMType.HIGHWAY_TERTIARY
                && street.getWayInfo().getType() != OSMType.HIGHWAY_UNCLASSIFIED
                && street.getWayInfo().getType() != OSMType.HIGHWAY_RESIDENTIAL
                && street.getWayInfo().getType() != OSMType.HIGHWAY_LIVING_STREET) {
            return;
        }

        Node[] nodes = street.getNodes();
        int nPoints = nodes.length;

        float streetNameLength = g.getFontMetrics().stringWidth(curStreetName);
        double streetNameDistance = 512;

        g.setColor(Color.DARK_GRAY);
        g.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));

        int i = 1;
        double distanceToFrom = -streetNameDistance / 1.3;
        Coordinates from = getLocalCoordinates(nodes[i - 1].getPos());
        Coordinates to = getLocalCoordinates(nodes[i].getPos());
        double currentLength = Coordinates.getDistance(from, to);
        double remainingStreetLength = calculateStreetLength(nodes);
        boolean drawBackwards = false;
        while (i < nPoints) {
            if (distanceToFrom + streetNameDistance <= currentLength) {
                distanceToFrom += streetNameDistance;
                remainingStreetLength -= distanceToFrom;
                if (remainingStreetLength < streetNameLength) {
                    // no (more) space for street name
                    break;
                }
                Coordinates vector = to.clone().subtract(from);
                Coordinates arrowStart = vector.clone().scale((float) (distanceToFrom / currentLength)).add(from);
                vector.normalize();
                Coordinates arrowEnd = vector.clone().scale(streetNameLength).add(arrowStart);
                double angle = Coordinates.getAngle(vector, new Coordinates(0.f, 1.f));
                if (arrowEnd.getLongitude() < arrowStart.getLongitude()) {
                    drawBackwards = true;
                } else {
                    drawBackwards = false;
                }
                if (arrowEnd.getLatitude() < arrowStart.getLatitude()) {
                    angle = -angle;
                }

                Font oldFont = g.getFont();
                Font newFont = oldFont.deriveFont(AffineTransform.getRotateInstance(drawBackwards ? angle + Math.PI : angle));
                g.setFont(newFont);
                FontMetrics fontMetrics = g.getFontMetrics();
                float descent = fontMetrics.getDescent();
                float ascent = fontMetrics.getAscent();
                Coordinates normal = vector.clone().rotate(drawBackwards ? 270 : 90).normalize().scale((descent + ascent) / descent);
                arrowStart.add(normal);
                int j = drawBackwards ? curStreetName.length() - 1 : 0;
                int jStep = drawBackwards ? -1 : 1;
                for (; j < curStreetName.length() && j >= 0; j += jStep) {
                    String character = curStreetName.substring(j, j + 1);
                    if (!drawBackwards) {
                        drawCharacterToCoordinates(arrowStart, newFont, character);
                    }
                    int charWidth = g.getFontMetrics(oldFont).stringWidth(character);
                    Coordinates lineVector = new Coordinates(0, 1).rotate(angle).normalize().scale(charWidth);
                    arrowStart.add(lineVector);
                    distanceToFrom += charWidth;
                    if (drawBackwards) {
                        drawCharacterToCoordinates(arrowStart, newFont, character);
                    }
                    while (distanceToFrom > currentLength) {
                        distanceToFrom -= currentLength;
                        remainingStreetLength -= currentLength;
                        i++;
                        if (i == nPoints) {
                            logger.error("I should not be able to break here. " + curStreetName);
                            break;
                        }
                        from = getLocalCoordinates(nodes[i - 1].getPos());
                        to = getLocalCoordinates(nodes[i].getPos());
                        currentLength = Coordinates.getDistance(from, to);
                        vector = to.clone().subtract(from);
                        arrowStart = vector.clone().scale((float) (distanceToFrom / currentLength)).add(from);
                        vector.normalize();
                        arrowEnd = vector.clone().scale(streetNameLength).add(arrowStart);
                        angle = Coordinates.getAngle(vector, new Coordinates(0.f, 1.f));
                        if (arrowEnd.getLatitude() < arrowStart.getLatitude()) {
                            angle = -angle;
                        }

                        newFont = oldFont.deriveFont(AffineTransform.getRotateInstance(drawBackwards ? angle + Math.PI : angle));
                        g.setFont(newFont);
                        fontMetrics = g.getFontMetrics();
                        descent = fontMetrics.getDescent();
                        ascent = fontMetrics.getAscent();
                        normal = vector.clone().rotate(drawBackwards ? 270 : 90).normalize().scale((descent + ascent) / descent);
                        arrowStart.add(normal);
                    }
                }
                g.setFont(oldFont);
            } else {
                while (distanceToFrom + streetNameDistance > currentLength) {
                    distanceToFrom -= currentLength;
                    remainingStreetLength -= currentLength;
                    i++;
                    if (i < nPoints) {
                        from = getLocalCoordinates(nodes[i - 1].getPos());
                        to = getLocalCoordinates(nodes[i].getPos());
                        currentLength = Coordinates.getDistance(from, to);
                    } else {
                        break;
                    }
                }
            }
        }
    }

    private void drawCharacterToCoordinates(Coordinates arrowStart, Font newFont, String character) {
        g.drawGlyphVector(newFont.createGlyphVector(g.getFontRenderContext(), character),
                arrowStart.getLongitude(), arrowStart.getLatitude());
    }

    private double calculateStreetLength(Node[] nodes) {
        double streetLength = 0;
        for (int j = 1; j < nodes.length; j++) {
            streetLength +=
                    Coordinates.getDistance(getLocalCoordinates(nodes[j - 1].getPos()),
                            getLocalCoordinates(nodes[j].getPos()));
        }
        return streetLength;
    }

    protected Node[] getRelevantNodesForStreet(Node[] streetNodes, int drawingSize) {
        List<Node> relevantNodes = new ArrayList<Node>(streetNodes.length);
        int start = 0;
        drawingSize += 2;
        Bounds extendedBounds = bounds.clone().extend(drawingSize);
        while (start < streetNodes.length - 1
                && !MathUtil.isLineInBounds(streetNodes[start].getPos(),
                        streetNodes[start + 1].getPos(), extendedBounds)) {
            start++;
        }
        int end = streetNodes.length - 1;
        while (end > 1 && !MathUtil.isLineInBounds(streetNodes[end - 1].getPos(),
                streetNodes[end].getPos(), extendedBounds)) {
            end--;
        }
        for (int i = start; i <= end; i++) {
            relevantNodes.add(streetNodes[i]);
        }

        return relevantNodes.toArray(new Node[relevantNodes.size()]);
    }

    protected Coordinates getLocalCoordinates(Coordinates coordinates) {
        return Renderer.getLocalCoordinates(coordinates, bounds.getTop(), bounds.getLeft(), detailLevel);
    }

    public static long getSpecifier(float lat, float lon, int tileSize, int detail) {
        return (long) (lon * 1000 + lat * 1000000) + tileSize + detail;
    }

    public long getSpecifier() {
        return getSpecifier(bounds.getTop(), bounds.getLeft(), tileSize, detailLevel);
    }

}
