
/**
Copyright (c) 2012, Yvonne Braun, Josua Stabenow
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


import java.awt.Image;

import kit.ral.common.Bounds;
import kit.ral.common.Context2D;
import kit.ral.common.Coordinates;
import kit.ral.common.projection.Projection;


import org.apache.log4j.Logger;


public class RendererMock {
    
    private static Logger logger = Logger.getLogger(Renderer.class);
    protected static int BASE_TILEDIM = 200;
    
    protected StateMock state = new StateMock();
    
    
    public void render(Context2D context) {
        int detail = context.getDetailLevel();
        int tileSize = (int) (BASE_TILEDIM * Projection.getZoomFactor(detail));
        if (tileSize < 0) {
            logger.error("tileDim < 0 => seems like an overflow");
        }
        Bounds bounds = context.getBounds();
        //Graphics graphics = ((Context2D) context).getGraphics();
        //graphics.setColor(new Color(210, 230, 190));
        //graphics.fillRect(0, 0, (int)context.getWidth(), (int)context.getHeight());
        int maxLon = (int) (bounds.getRight() / tileSize);
        int maxLat = (int) (bounds.getBottom() / tileSize);
        int minLon = (int) (bounds.getLeft() / tileSize);
        int minLat = (int) (bounds.getTop() / tileSize);
        for (int i = minLon; i <= maxLon; i++) {
            for (int k = minLat; k <= maxLat; k++) {
                Coordinates topLeft = new Coordinates(k * tileSize, i * tileSize);
                TileMock currentTile = prerenderTile(topLeft, tileSize, detail);
                drawImage(context, topLeft, currentTile.getImage(), detail);
            }
        }
       // drawRoute(context, detail);
        //drawNavPoints(context, detail);
        //drawOverlay(context, detail);
    }
    
    private TileMock prerenderTile(Coordinates topLeft, float tileDim, int detail) {
        //Tile tile = cache.queryCache(topLeft, detail);
        //if (tile == null) {
          TileMock  tile = new TileMock(topLeft, tileDim, detail,state);
            tile.prerender();
          //  cache.addToCache(tile);
       // }
        return tile;
    }
    
    protected static Coordinates getLocalCoordinates(Coordinates global, Coordinates topLeft, int detail) {
        return global.clone().subtract(topLeft).scale(1f / Projection.getZoomFactor(detail));
    }
    
    private void drawImage(Context2D context, Coordinates topLeft, Image image, int detail) {
        int x = (int) ((topLeft.getLongitude() - context.getBounds().getLeft())
                / Projection.getZoomFactor(detail));
        int y = (int) ((topLeft.getLatitude() - context.getBounds().getTop())
                / Projection.getZoomFactor(detail));
        ((Context2D) context).getGraphics().drawImage(image, x, y, null);
    }
    
    
}
