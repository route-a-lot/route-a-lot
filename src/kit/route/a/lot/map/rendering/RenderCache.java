package kit.route.a.lot.map.rendering;

import java.awt.Image;

import kit.route.a.lot.common.Coordinates;

public interface RenderCache {

    /**
     * Gibt bei einem Cachetreffer die für die gegebenen Koordinaten
     * gespeicherte Kachel zurück. Andernfalls wird null zurückgegeben.
     * 
     * @param topLeft nordwestliche Ecke der gesuchten Kachel
     * @return die entsprechende im Cache gespeicherte Kachel
     */
    Image queryCache(Coordinates topLeft);

    /**
     * Fügt dem Cache die gegebene Kachel für die angegebenen Koordinaten hinzu.
     * 
     * @param topLeft nordwestliche Ecke der Kachel
     * @param image die Kachel
     */
    void addToCache(Coordinates topLeft, Image image);

}
