package kit.route.a.lot.gui;


import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

import kit.route.a.lot.common.Context2D;
import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Projection;
import kit.route.a.lot.gui.event.ChangeViewEvent;

public class Map2D extends Map  {
    
    private static final long serialVersionUID = 1L;


    public Map2D(GUI gui) {
        super(gui);
    }

    protected Component createCanvas() {
        JPanel result = new JPanel() {
            private static final long serialVersionUID = 1L;
            @Override
            public void paint(Graphics g) {
                super.paint(g);           
                Listeners.fireEvent(gui.getListeners().viewChanged,
                        new ChangeViewEvent(new Context2D(topLeft, bottomRight, g, zoomlevel)));
            }      
        };
        result.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                calculateView();
            }
        });
        return result;
    }
    
    /**
     * Adapts the map position and schedules a map redraw.
     */
    @Override
    public void mouseDragged(MouseEvent e) {         
        if ((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0) {
            getCenter().add(new Coordinates(oldMousePosY - e.getY(), oldMousePosX - e.getX())
                .scale(Projection.getZoomFactor(zoomlevel)));                      
        }      
        super.mouseDragged(e);
    }

    @Override
    protected Coordinates getPosition(int x, int y) {
        Coordinates result = new Coordinates(y - canvas.getHeight() / 2, x - canvas.getWidth() / 2);
        return result.scale(Projection.getZoomFactor(zoomlevel)).add(getCenter());
    }

}
