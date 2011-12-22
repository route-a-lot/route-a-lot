package kit.route.a.lot.map.rendering;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Context;
import kit.route.a.lot.map.rendering.Renderer;

public class Renderer3D extends Renderer {

    /**
     * Renders a map viewing rectangle in three dimensional form,
     * using height data and perspective projection in the process.
     * 
     * @param detail level of detail of the map view
     * @param topLeft northwestern corner of the viewing rectangle
     * @param bottomRight southeastern corner of the viewing rectangle
     * @param renderingContext an OpenGL rendering context
     */
    public void render(int detail, Coordinates topLeft,
            Coordinates bottomRight, Context renderingContext) {
    }
}
