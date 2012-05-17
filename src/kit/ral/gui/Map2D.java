
/**
Copyright (c) 2012, Malte Wolff, Jan Jacob, Josua Stabenow
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
