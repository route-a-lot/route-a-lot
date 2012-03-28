package kit.ral.gui;


import static kit.ral.common.event.Listener.*;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

import kit.ral.common.Bounds;
import kit.ral.common.Context2D;
import kit.ral.common.Coordinates;
import kit.ral.common.event.Listener;
import kit.ral.common.event.RenderEvent;
import kit.ral.common.projection.Projection;

public class Map2D extends Map  {
    
    private static final long serialVersionUID = 1;


    public Map2D(GUI gui) {
        super(gui);
    }

    protected Component createCanvas() {
        JPanel result = new JPanel() {
            private static final long serialVersionUID = 1L;
            @Override
            public void paint(Graphics g) {
                super.paint(g);       
                Coordinates diff = new Coordinates(getHeight(), getWidth())
                                        .scale(Projection.getZoomFactor(zoomlevel) / 2f);
                Listener.fireEvent(RENDER,
                        new RenderEvent(new Context2D(new Bounds(
                                center.clone().subtract(diff), diff.add(center)),
                                zoomlevel, g)));
            }      
        };
        return result;
    }
    
    /**
     * Adapts the map position and schedules a map redraw.
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        if (oldMousePosX == Integer.MIN_VALUE) {
            oldMousePosX = e.getX();
            oldMousePosY = e.getY();
        }
        if (isMouseButtonPressed(e, 1) && !isMouseButtonPressed(e, 3)) {
            center.add(new Coordinates(oldMousePosY - e.getY(), oldMousePosX - e.getX())
                .scale(Projection.getZoomFactor(zoomlevel)));                      
        }      
        super.mouseDragged(e);     
    }

    @Override
    protected Coordinates getPosition(int x, int y) {
        Coordinates result = new Coordinates(y - canvas.getHeight() / 2, x - canvas.getWidth() / 2);
        return result.scale(Projection.getZoomFactor(zoomlevel)).add(center);
    }

}