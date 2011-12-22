package kit.route.a.lot.map.infosupply;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.map.MapElement;

public class QuadTree {

    /** Attributes */
    /**
     * 
     */
    private Coordinates upLeft;
    /**
     * 
     */
    private Coordinates bottomRight;

    /**
     * Operation getLeafs
     * 
     * @param upLeft
     *            -
     * @param bottomRight
     *            -
     * @return Collection<QTLeaf>
     */
    protected Collection<QTLeaf> getLeafs(Coordinates upLeft,
            Coordinates bottomRight) {
        return null;
    }

    /**
     * Operation select
     * 
     * @param pos
     *            -
     * @return Selection
     */
    protected Selection select(Coordinates pos) {
        return null;
    }

    /**
     * Operation loadFromStream
     * 
     * @param stream
     *            -
     * @return
     * @return
     */
    protected void loadFromStream(InputStream stream) {
    }

    /**
     * Operation saveToStream
     * 
     * @param stream
     *            -
     * @return
     * @return
     */
    protected void saveToStream(OutputStream stream) {
    }

    /**
     * Operation addToOverlay
     * 
     * @param element
     *            -
     * @return
     * @return
     */
    protected void addToOverlay(MapElement element) {
    }

    /**
     * Operation addToBaseLayer
     * 
     * @param element
     *            -
     * @return
     * @return
     */
    protected void addToBaseLayer(MapElement element) {
    }
}
