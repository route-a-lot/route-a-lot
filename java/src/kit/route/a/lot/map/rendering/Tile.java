package kit.route.a.lot.map.rendering;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import kit.route.a.lot.common.Address;
import kit.route.a.lot.common.Bounds;
import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.OSMType;
import kit.route.a.lot.common.Projection;
import kit.route.a.lot.common.WayInfo;
import kit.route.a.lot.controller.State;
import kit.route.a.lot.map.Area;
import kit.route.a.lot.map.MapElement;
import kit.route.a.lot.map.Node;
import kit.route.a.lot.map.POINode;
import kit.route.a.lot.map.Street;
import kit.route.a.lot.map.infosupply.MapInfo;
import kit.route.a.lot.routing.Precalculator;

import org.apache.log4j.Logger;


public class Tile {

    private static Logger logger = Logger.getLogger(Tile.class);
    private static final int POI_SIZE = 8;
    private static final Color POI_BORDER_COLOR = new Color(196, 161, 80);
    private static final Color POI_COLOR = new Color(229, 189, 100);

    protected Bounds bounds;
    protected int detailLevel, tileSize;

    private BufferedImage image = null;
    private boolean finished = false;

    private MapInfo mapInfo;

    /** Temporary variable (only guaranteed to be valid when rendering) */
    private Graphics2D graphics;

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
            image =
                    new BufferedImage(tileSize / Projection.getZoomFactor(detailLevel), tileSize
                            / Projection.getZoomFactor(detailLevel), BufferedImage.TYPE_INT_ARGB);
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
        graphics = getImage().createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // color tile background
        // int c1 = Math.abs(this.hashCode()) % 256;
        // int c2 = Math.abs(getImage().hashCode()) % 256;
        // graphics.setColor(new Color(c1, c2, ((c1 + c2) * 34) % 256, 64));
        // graphics.fillRect(0, 0, tileSize / Projection.getZoomFactor(detailLevel), tileSize /
        // Projection.getZoomFactor(detailLevel));

        // draw base layer elements

        for (MapElement element : map) {
            if ((element instanceof Area) && (!((Area) element).getWayInfo().isBuilding())) {
                draw((Area) element);
            }
        }
  
        for (MapElement element : map) {
            if (element instanceof Node && !(element instanceof POINode)) {
                draw((Node) element);
            } else if (element instanceof Street) {
                draw((Street) element, false);
            }
        }

        for (MapElement element : map) {
            if ((element instanceof Street) && (((Street) element).getDrawingSize() < 20)) {
                draw((Street) element, true);
            }
        }
        for (MapElement element : map) {
            if ((element instanceof Street) && (((Street) element).getDrawingSize() >= 20)) {
                draw((Street) element, true);
            }
        }
        
        for (MapElement element : map) {
            if ((element instanceof Area) && (((Area) element).getWayInfo().isBuilding())) {
                draw((Area) element);
            }
        }

        for (MapElement element : map) {
            if (element instanceof Street) {
                drawStreetArrows((Street) element);
            }
        }
        for (MapElement element : map) {
            if (element instanceof Street) {
                drawStreetNames((Street) element);
            }
        }

        if (State.getInstance().getActiveRenderer().drawAreas) {
            Precalculator.drawAreas(bounds, detailLevel, graphics);
        }
        
        graphics.dispose();

    }

    public void drawPOIs() {
        Set<MapElement> elements = mapInfo.queryElements(detailLevel, bounds, false);
        if (elements.size() == 0) {
            return;
        }
        graphics = getImage().createGraphics();
        for (MapElement element : elements) {
            if (element instanceof POINode) {
                POINode poi = (POINode) element;
                if ((poi.getInfo().getName() == null) || (poi.getInfo().getName().length() == 0)) {
                    continue;
                }
                graphics.setColor(POI_BORDER_COLOR);
                drawPoint(poi.getPos(), POI_SIZE + 2);
                graphics.setColor(POI_COLOR);
                drawPoint(poi.getPos(), POI_SIZE);       
            } 
        }
        graphics.dispose();
    }


    /**
     * Draws a regular node on the tile.
     * 
     * @param poi
     *            the node to be drawn
     */
    private void draw(Node node) {
        graphics.setColor(Color.LIGHT_GRAY);
        drawPoint(node.getPos(), 3);
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
            graphics.setColor(Color.GRAY);
        } else if (wayInfo.isArea()) {
            switch (wayInfo.getType()) {
                case OSMType.NATURAL_WOOD:
                    graphics.setColor(Color.GREEN);
                    break;
                default:
                    // System.out.println("Unknown area type in tile rendering: " + wayInfo.getType());
                    return;
                    // graphics.setColor(Color.WHITE);
            }
        } else {
            return;
            // graphics.setColor(Color.WHITE);
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
    private void draw(Street street, boolean top) {
        Node[] nodes = getRelevantNodesForStreet(street.getNodes(), street.getDrawingSize());
        int nPoints = nodes.length;
        int[] xPoints = new int[nPoints];
        int[] yPoints = new int[nPoints];
        WayInfo wayInfo = street.getWayInfo();

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
        int basicSize = street.getDrawingSize();
        int size = basicSize / Projection.getZoomFactor(detailLevel);
        if (!top) {
            graphics.setStroke(new BasicStroke(size + 2 / (float) Math.pow(detailLevel + 1, 0.8),
                    BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        } else {
            graphics.setStroke(new BasicStroke(size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        }

        for (int i = 0; i < nPoints; i++) {
            Coordinates curCoordinates = getLocalCoordinates(nodes[i].getPos());
            xPoints[i] = (int) curCoordinates.getLongitude();
            yPoints[i] = (int) curCoordinates.getLatitude();
        }
        
        graphics.drawPolyline(xPoints, yPoints, nPoints);
    }

    private void drawPoint(Coordinates globalCoordinates, int size) {
        Coordinates localCoordinates = getLocalCoordinates(globalCoordinates).add(-size/2, -size/2);
        graphics.fillOval((int) localCoordinates.getLongitude(), (int) localCoordinates.getLatitude(), size, size);
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

        graphics.setColor(new Color(217, 192, 129));
        graphics.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));

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

                graphics.drawLine((int) arrowStart.getLongitude(), (int) arrowStart.getLatitude(),
                        (int) arrowEnd.getLongitude(), (int) arrowEnd.getLatitude());
                graphics.drawLine((int) arrowEnd.getLongitude(), (int) arrowEnd.getLatitude(),
                        (int) headLeft.getLongitude(), (int) headLeft.getLatitude());
                graphics.drawLine((int) arrowEnd.getLongitude(), (int) arrowEnd.getLatitude(),
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

        float streetNameLength = graphics.getFontMetrics().stringWidth(curStreetName);
        double streetNameDistance = 512;

        graphics.setColor(Color.DARK_GRAY);
        graphics.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));

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

                Font oldFont = graphics.getFont();
                Font newFont = oldFont.deriveFont(AffineTransform.getRotateInstance(drawBackwards ? angle + Math.PI : angle));
                graphics.setFont(newFont);
                FontMetrics fontMetrics = graphics.getFontMetrics();
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
                    int charWidth = graphics.getFontMetrics(oldFont).stringWidth(character);
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
                        graphics.setFont(newFont);
                        fontMetrics = graphics.getFontMetrics();
                        descent = fontMetrics.getDescent();
                        ascent = fontMetrics.getAscent();
                        normal = vector.clone().rotate(drawBackwards ? 270 : 90).normalize().scale((descent + ascent) / descent);
                        arrowStart.add(normal);
                    }
                }
                graphics.setFont(oldFont);
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
        graphics.drawGlyphVector(newFont.createGlyphVector(graphics.getFontRenderContext(), character),
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
                && !Street.isEdgeInBounds(streetNodes[start].getPos(),
                        streetNodes[start + 1].getPos(), extendedBounds)) {
            start++;
        }
        int end = streetNodes.length - 1;
        while (end > 1 && !Street.isEdgeInBounds(streetNodes[end - 1].getPos(),
                streetNodes[end].getPos(), extendedBounds)) {
            end--;
        }
        for (int i = start; i <= end; i++) {
            relevantNodes.add(streetNodes[i]);
        }

        return relevantNodes.toArray(new Node[relevantNodes.size()]);
    }

    private Coordinates getLocalCoordinates(Coordinates coordinates) {
        return Renderer.getLocalCoordinates(coordinates, bounds.getTop(), bounds.getLeft(), detailLevel);
    }

    public static long getSpecifier(float lat, float lon, int tileSize, int detail) {
        return (long) Math.floor((lon + lat * 10000) * 100000) + tileSize + detail;
    }

    public long getSpecifier() {
        return getSpecifier(bounds.getTop(), bounds.getLeft(), tileSize, detailLevel);
    }

}
