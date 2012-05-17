
/**
Copyright (c) 2012, Josua Stabenow
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
        * Redistributions of source code must retain the above copyright
          notice, this list of conditions and the following disclaimer.
        * Redistributions in binary form must reproduce the above copyright
          notice, this list of conditions and the following disclaimer in the
          documentation and/or other materials provided with the distribution.
        * The names of the contributors may not be used to endorse or promote products
          derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
**/

package kit.ral.map.rendering;

import java.awt.BasicStroke;
import static java.awt.BasicStroke.*;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import kit.ral.common.Bounds;
import kit.ral.common.Coordinates;
import kit.ral.common.description.WayInfo;
import kit.ral.common.projection.Projection;
import kit.ral.common.util.MathUtil;
import kit.ral.map.Area;
import kit.ral.map.MapElement;
import kit.ral.map.Node;
import kit.ral.map.Street;

/* THIS CLASS IS WORK IN PROGRESS, THUS CURRENTLY NOT USED */
public class Style {
   
    private static final boolean USE_PATTERNS = true;
    
    public static final int
        STREET_FOOT = 0, STREET_BICYCLE = 1, STREET_RESIDENTIAL = 2,
        STREET_TERTIARY = 3, STREET_SECONDARY = 4, STREET_PRIMARY = 5,
        STREET_MOTORWAY = 6,
        STREET_DISABLED = 7, BRIDGE = 8, TUNNEL = 9, RAILWAY = 10,
        BUILDING = 11, FOREST = 12, WATER = 13, POI = 14;
    
    private static Paint[] FILL_PAINT = {
        Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE,
        Color.WHITE, new Color(255, 204, 51), new Color(0, 51, 153),
        Color.LIGHT_GRAY, null, null, Color.GRAY /*TODO pattern*/, /*inherit*/
        new Color(200, 200, 200), new Color(126, 159, 107),
        new Color(135, 168, 198), new Color(229, 189, 100)};
    
    private static Paint[] BORDER_PAINT = {
        Color.DARK_GRAY, Color.DARK_GRAY, Color.DARK_GRAY,
        Color.DARK_GRAY, Color.DARK_GRAY, Color.DARK_GRAY, Color.DARK_GRAY,
        Color.DARK_GRAY, Color.DARK_GRAY, Color.DARK_GRAY, null, /*no border*/
        new Color(150, 150, 150), null, null, new Color(196, 161, 80)};

    private static float[] FILL_SIZE = {
        10, 15, 18, 20, 22, 30, 40,
        -1 , -1, -1, 20, /*inherit*/
        0, 0, 0, 5 /*not applicable*/};
    
    private static float[] BORDER_SIZE = {
        1, 1, 1, 1, 1, 1, 1,
        1, 10, 5, 0,
        1, 0, 0, 1};
    
    private static int[] CAP_STYLE = {
        CAP_ROUND, CAP_ROUND, CAP_ROUND, CAP_ROUND,
        CAP_ROUND, CAP_ROUND, CAP_ROUND,
        CAP_ROUND, CAP_ROUND, CAP_ROUND, CAP_SQUARE,
        CAP_SQUARE, CAP_SQUARE, CAP_SQUARE, CAP_ROUND};
    private static int[] JOIN_STYLE = {
        JOIN_ROUND, JOIN_ROUND, JOIN_ROUND, JOIN_ROUND,
        JOIN_ROUND, JOIN_ROUND, JOIN_ROUND,
        JOIN_ROUND, JOIN_ROUND, JOIN_ROUND, JOIN_MITER,
        JOIN_MITER, JOIN_MITER, JOIN_MITER, JOIN_ROUND};
    
    static {
        if (USE_PATTERNS) {
            FILL_PAINT[FOREST] = new TexturePaint(
                    loadImageResource("pattern_forest.png"),
                    new Rectangle2D.Float(0, 0, 62, 62));
            FILL_PAINT[WATER] = new TexturePaint(
                    loadImageResource("pattern_water.png"),
                    new Rectangle2D.Float(0, 0, 64, 64));        
        }
    }
    
    
    
    
    
    private int MAX_STREET_DETAIL_LEVEL = 3;
    private int MAX_MINOR_STREET_SHADOW_LEVEL = 4; 
    private int AREA_ALPHA_VALUE = 100; //140  
    
    private Tile tile;
    private Graphics2D g;
    private float zoomFactor;
    
    private Paint fillPaint, borderPaint;
    private Stroke fillStroke, borderStroke;
    
    
    public Style(Tile tile) {
        this.tile = tile;
        this.g = tile.getImage().createGraphics();
        this.g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        this.g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        this.zoomFactor = Projection.getZoomFactor(tile.detailLevel);
    } 
    

    public void drawElement(MapElement element, int type) {
        int baseType = type % 16;
        int extType = type / 16;
        
        fillPaint = FILL_PAINT[baseType];
        borderPaint = BORDER_PAINT[baseType];
        float fillSize = FILL_SIZE[baseType];
        float borderSize = BORDER_SIZE[baseType];
        int capStyle = CAP_STYLE[baseType];
        int joinStyle = JOIN_STYLE[baseType];
        
        if (extType != 0) {
            if (FILL_PAINT[extType] != null) {
                fillPaint = FILL_PAINT[extType];
            }
            borderPaint = BORDER_PAINT[extType];
            if (FILL_SIZE[extType] >= 0) {
                fillSize = FILL_SIZE[extType];
            }
            borderSize = BORDER_SIZE[extType];
            capStyle = CAP_STYLE[extType];
            joinStyle = JOIN_STYLE[extType];
        }
        borderSize += fillSize;
        fillStroke = new BasicStroke(fillSize / zoomFactor, capStyle, joinStyle);
        borderStroke = new BasicStroke(borderSize / zoomFactor, capStyle, joinStyle);
        if (element instanceof Area) {
            customDrawArea((Area) element);
        } else if (element instanceof Street) {
            customDrawLine((Street) element);
        }
    }
    
    private void customDrawLine(Street street) {
        Node[] nodes = getRelevantNodesForStreet(
                street.getNodes(), street.getDrawingSize());
        int[] xPoints = new int[nodes.length];
        int[] yPoints = new int[nodes.length];
        for (int i = 0; i < nodes.length; i++) {
            Coordinates curCoordinates = getLocalCoordinates(nodes[i].getPos());
            xPoints[i] = (int) curCoordinates.getLongitude();
            yPoints[i] = (int) curCoordinates.getLatitude();
        }
        if (borderPaint != null) {
            g.setStroke(borderStroke);
            g.setPaint(borderPaint);
            g.drawPolyline(xPoints, yPoints, nodes.length);
        }
        if (fillPaint != null) {
            g.setStroke(fillStroke);
            g.setPaint(fillPaint);
            g.drawPolyline(xPoints, yPoints, nodes.length);
        }   
    }
    
    private void customDrawArea(Area area) {
        Node[] nodes = area.getNodes();
        int[] xPoints = new int[nodes.length];
        int[] yPoints = new int[nodes.length];
        for (int i = 0; i < nodes.length; i++) {
            Coordinates curCoordinates = getLocalCoordinates(nodes[i].getPos());
            xPoints[i] = (int) curCoordinates.getLongitude();
            yPoints[i] = (int) curCoordinates.getLatitude();
        }
        if (fillPaint != null) {
            g.setPaint(fillPaint);
            g.fillPolygon(xPoints, yPoints, nodes.length);
        }
        if (borderPaint != null) {
            g.setStroke(borderStroke);
            g.setPaint(borderPaint);
            g.drawPolygon(xPoints, yPoints, nodes.length);
        }
    }
    
    public Coordinates getLocalCoordinates(Coordinates global) {
        return global.clone().add(-tile.bounds.getTop(), -tile.bounds.getLeft())
                    .scale(1f / Projection.getZoomFactor(tile.detailLevel));
    }
    
    private static BufferedImage loadImageResource(String name) {
        BufferedImage result = null;
        try {
            result = ImageIO.read(ClassLoader.getSystemResource(name));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    protected Node[] getRelevantNodesForStreet(Node[] streetNodes, int drawingSize) {
        List<Node> relevantNodes = new ArrayList<Node>(streetNodes.length);
        int start = 0;
        drawingSize += 2;
        Bounds extendedBounds = tile.bounds.clone().extend(drawingSize);
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
    
    
    
    
}
