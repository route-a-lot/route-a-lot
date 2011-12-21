package kit.route.a.lot.map.rendering;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Context;
import kit.route.a.lot.map.rendering.Renderer;

public class Renderer3D extends Renderer {

    /**
     * Zeichnet einen Kartenausschnitt in dreidimensionaler Form 
     * unter Verwendung von Höhendaten und perspektivischer Projektion.
     * 
     * @param detail Detailgrad, in dem gezeichnet werden soll
     * @param topLeft nordwestliche Ecke des Kartenausschnitts
     * @param bottomRight südöstliche Ecke des Kartenausschnitts
     * @param renderingContext Ausgabekontext
     */
    public void render(int detail, Coordinates topLeft,
            Coordinates bottomRight, Context renderingContext) {
    }
}
