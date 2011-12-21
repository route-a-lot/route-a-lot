package kit.route.a.lot.map.rendering;

import java.awt.Image;

import kit.route.a.lot.common.Coordinates;

public interface RenderCache {

    /**
     * Gibt bei einem Cachetreffer die f�r die gegebenen Koordinaten
     * gespeicherte Kachel zur�ck. Andernfalls wird null zur�ckgegeben.
     * 
     * @param topLeft nordwestliche Ecke der gesuchten Kachel
     * @return die entsprechende im Cache gespeicherte Kachel
     */
    Image queryCache(Coordinates topLeft);

    /**
     * F�gt dem Cache die gegebene Kachel f�r die angegebenen Koordinaten hinzu.
     * 
     * @param topLeft nordwestliche Ecke der Kachel
     * @param image die Kachel
     */
    void addToCache(Coordinates topLeft, Image image);

}
