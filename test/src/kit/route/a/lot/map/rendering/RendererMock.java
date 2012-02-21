package kit.route.a.lot.map.rendering;


import java.awt.Image;

import kit.route.a.lot.common.Context2D;
import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Projection;


import org.apache.log4j.Logger;


public class RendererMock {
    
    private static Logger logger = Logger.getLogger(Renderer.class);
    protected static int BASE_TILEDIM = 200;
    
    protected StateMock state = new StateMock();
    
    
    public void render(Context2D context) {
        int detail = context.getDetailLevel();
        int tileDim = (int) (BASE_TILEDIM * Projection.getZoomFactor(detail));
        if (tileDim < 0) {
            logger.error("tileDim < 0 => seems like an overflow");
        }
        //Graphics graphics = ((Context2D) context).getGraphics();
        //graphics.setColor(new Color(210, 230, 190));
        //graphics.fillRect(0, 0, (int)context.getWidth(), (int)context.getHeight());
        int maxLon = (int) Math.floor(context.getBottomRight().getLongitude() / tileDim);
        int maxLat = (int) Math.floor(context.getBottomRight().getLatitude() / tileDim);
        int minLon = (int) Math.floor(context.getTopLeft().getLongitude() / tileDim);
        int minLat = (int) Math.floor(context.getTopLeft().getLatitude() / tileDim);
        for (int i = minLon; i <= maxLon; i++) {
            for (int k = minLat; k <= maxLat; k++) {
                Coordinates topLeft = new Coordinates(k * tileDim, i * tileDim);
                TileMock currentTile = prerenderTile(topLeft, tileDim, detail);
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
        int x = (int) ((topLeft.getLongitude() - context.getTopLeft().getLongitude())
                / Projection.getZoomFactor(detail));
        int y = (int) ((topLeft.getLatitude() - context.getTopLeft().getLatitude())
                / Projection.getZoomFactor(detail));
        ((Context2D) context).getGraphics().drawImage(image, x, y, null);
    }
    
    
}
