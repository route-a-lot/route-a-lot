package kit.route.a.lot.map;

import java.io.InputStream;
import java.io.OutputStream;

import kit.route.a.lot.common.Selection;
import kit.route.a.lot.common.Coordinates;

public abstract class MapElement {

    /** Attributes */
    /**
     * 
     */
    private int id;

    /**
     * Operation getName
     * 
     * @return String
     */
    abstract protected String getName();

    /**
     * Operation getSelection
     * 
     * @return Selection
     */
    abstract protected Selection getSelection();

    /**
     * Operation isInBounds
     * 
     * @param topLeft
     *            -
     * @param bottomRight
     *            -
     * @return boolean
     */
    abstract protected boolean isInBounds(Coordinates topLeft,
            Coordinates bottomRight);

    /**
     * Operation loadFromStream
     * 
     * @param stream
     *            -
     * @return MapElement
     */
    protected static MapElement loadFromStream(InputStream stream) {
        return null;
    }

    /**
     * Operation saveToStream
     * 
     * 
     * @param stream
     *            -
     * @param element
     *            -
     * @return
     * @return
     */
    protected static void saveToStream(OutputStream stream, MapElement element) {
    }

    /**
     * Operation load
     * 
     * @param stream
     *            -
     * @return
     * @return
     */
    abstract protected void load(InputStream stream);

    /**
     * Operation save
     * 
     * @param stream
     *            -
     * @return
     * @return
     */
    abstract protected void save(OutputStream stream);

}
