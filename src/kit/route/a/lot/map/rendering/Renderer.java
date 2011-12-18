package kit.route.a.lot.map.rendering;

import java.util.List;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Context;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.map.rendering.Renderer;
import kit.route.a.lot.map.POINode;
import kit.route.a.lot.map.Area;
import kit.route.a.lot.map.Edge;
import kit.route.a.lot.map.rendering.RenderCache;

public class Renderer {

    /** Associations */
    private RenderCache detailCaches;

    /**
     * Operation render
     * 
     * render holt sich die aktuelle Route aus dem State und zeichnet sie mit in
     * das Overlay
     * 
     * @param detail
     *            -
     * @param topLeft
     *            -
     * @param bottomRight
     *            -
     * @param renderingContext
     *            -
     * @return
     * @return
     */
    public void render(int detail, Coordinates topLeft,
            Coordinates bottomRight, Context renderingContext) {
    }

    /**
     * Operation prerenderIdle
     * 
     * @return boolean
     */
    public boolean prerenderIdle() {
        return false;
    }

    /**
     * Operation inheritCache
     * 
     * @param source
     *            -
     * @return
     * @return
     */
    public void inheritCache(Renderer source) {
    }

    /**
     * Operation prerenderTile
     * 
     * @param detail
     *            -
     * @param topLeft
     *            -
     * @return
     * @return
     */
    private void prerenderTile(int detail, Coordinates topLeft) {
    }

    /**
     * Operation drawRoute
     * 
     * @param route
     *            -
     * @param selection
     *            -
     * @return
     * @return
     */
    private void drawRoute(List<Integer> route, List<Selection> selection) {
    }

    /**
     * Operation draw
     * 
     * @param poi
     *            -
     * @return
     * @return
     */
    private void draw(POINode poi) {
    }

    /**
     * Operation draw
     * 
     * @param area
     *            -
     * @return
     * @return
     */
    private void draw(Area area) {
    }

    /**
     * Operation draw
     * 
     * @param edge
     *            -
     * @return
     * @return
     */
    private void draw(Edge edge) {
    }
}
