package kit.ral.map.rendering;



import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import kit.ral.common.Bounds;
import kit.ral.common.Coordinates;
import kit.ral.common.description.OSMType;
import kit.ral.common.description.WayInfo;
import kit.ral.common.projection.Projection;
import kit.ral.common.util.MathUtil;
import kit.ral.map.Area;
import kit.ral.map.MapElement;
import kit.ral.map.Node;
import kit.ral.map.Street;


public class TileMock {
    
    private Bounds bounds;
    private BufferedImage image;
    private int detail;
    private int tileDim;
    private StateMock state;
    
    public TileMock(Coordinates topLeft, float tileDim, int detail, StateMock state) {
        this.bounds = new Bounds(topLeft, topLeft.clone().add(tileDim, tileDim));
        this.detail = detail;
        this.tileDim = (int) tileDim;
        this.state = state;
    }
    
    public void prerender() {
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

        Graphics2D graphics = image.createGraphics();

        //long start = System.nanoTime();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        Collection<MapElement> map = state.getMapInfo().queryElements(detail, bounds, false);
        /*-----für Test-----------*/
        System.out.println(state.getMapInfo().queryElements(detail, bounds, false).size());
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
                System.out.println("is Street");
                draw((Street) element, false, graphics);
            }
        }

        for (MapElement element : map) {
            if (element instanceof Street) {
                System.out.println("is Street");
                draw((Street) element, true, graphics);
            }
        }
        
       

        //long end = System.nanoTime();
        //double mapElements = (middle - start) / 1000000;
        //double drawing = (end - middle) / 1000000;
        // System.out.println("time for mapElements " + mapElements + "ms; for drawing " + drawing + "ms");
        
        graphics.dispose();
    }
    
    protected void reset() {
        image = new BufferedImage(tileDim / Projection.getZoomFactor(detail),
                tileDim / Projection.getZoomFactor(detail), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(new Color(210, 230, 190));
        graphics.fillRect(0, 0, tileDim, tileDim);
    }
    
    
    protected BufferedImage getImage() {
        return this.image;
    }

    private void draw(Node node, Graphics2D graphics) {
        graphics.setColor(Color.LIGHT_GRAY);
        drawPoint(node.getPos(), 3, graphics);
    }
    
    private void draw(Area area, Graphics2D graphics) {
        int[] xPoints, yPoints;
        int nPoints;
        Node[] nodes = area.getNodes();
        nPoints = nodes.length;
        xPoints = new int[nPoints];
        yPoints = new int[nPoints];

        for (int i = 0; i < nPoints; i++) {
            Coordinates curCoordinates = Renderer.getLocalCoordinates(nodes[i].getPos(),
                    bounds.getTop(), bounds.getLeft(), detail);
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
    
    protected Node[] getRelevantNodesForStreet(Node[] streetNodes) {
        List<Node> relevantNodes = new ArrayList<Node>(streetNodes.length);
        int start = 0;
        while (start < streetNodes.length - 1 && !MathUtil.isLineInBounds(streetNodes[start].getPos(),
                streetNodes[start+1].getPos(), bounds)) {
            start++;
        }
        int end = streetNodes.length - 1;
        while (end > 1 && !MathUtil.isLineInBounds(streetNodes[end - 1].getPos(),
                streetNodes[end].getPos(), bounds)) {
            end--;
        }
        for (int i = start; i <= end; i++) {
            relevantNodes.add(streetNodes[i]);
        }
        
        return relevantNodes.toArray(new Node[relevantNodes.size()]);
    }

    /**
     * Draws a street on the tile, taking the street type into consideration.
     * @param street the street to be drawn
     */
    private void draw(Street street, boolean top, Graphics2D graphics) {
        Node[] nodes = getRelevantNodesForStreet(street.getNodes());
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
                basicSize = 20;
                break;
            case OSMType.HIGHWAY_RESIDENTIAL:
            case OSMType.HIGHWAY_LIVING_STREET:
            case OSMType.HIGHWAY_UNCLASSIFIED:
                basicSize = 18;
                break;
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
            Coordinates curCoordinates = getLocalCoordinates(nodes[i].getPos());
            xPoints[i] = (int) curCoordinates.getLongitude();
            yPoints[i] = (int) curCoordinates.getLatitude();
        }
        
        graphics.drawPolyline(xPoints, yPoints, nPoints);
        
        
    }
  
    private void drawPoint(Coordinates globalCoordinates, int size, Graphics2D graphics) {
        Coordinates localCoordinates = Renderer.getLocalCoordinates(globalCoordinates,
                bounds.getTop(), bounds.getLeft(), detail);
        graphics.fillOval((int) localCoordinates.getLongitude() - size / 2,
                (int) localCoordinates.getLatitude() - size / 2, size, size);
    }
    
    private Coordinates getLocalCoordinates(Coordinates coordinates) {
        return Renderer.getLocalCoordinates(coordinates, bounds.getTop(), bounds.getLeft(), detail);
    }

}
