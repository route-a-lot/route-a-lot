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
