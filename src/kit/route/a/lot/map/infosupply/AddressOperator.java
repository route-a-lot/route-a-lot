package kit.route.a.lot.map.infosupply;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import kit.route.a.lot.common.Selection;
import kit.route.a.lot.map.MapElement;

public interface AddressOperator

{
    /**
     * Operation suggestCompletions
     *
     * @param expression - 
     * @return List<String>
     */
    List<String> suggestCompletions ( String expression );

    /**
     * Operation select
     *
     * @param address - 
     * @return Selection
     */
    Selection select ( String address );

    /**
     * Operation add
     *
     * @param element - 
     * @return 
     * @return 
     */
    void add ( MapElement element );

    /**
     * Operation loadFromStream
     *
     * @param stream - 
     * @return 
     * @return 
     */
    void loadFromStream ( InputStream stream );

    /**
     * Operation saveToStream
     *
     * @param stream - 
     * @return 
     * @return 
     */
    void saveToStream ( OutputStream stream );

}

