package kit.route.a.lot.map.infosupply;

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
    protected List<String> suggestCompletions ( String expression );

    /**
     * Operation select
     *
     * @param address - 
     * @return Selection
     */
    protected Selection select ( String address );

    /**
     * Operation add
     *
     * @param element - 
     * @return 
     */
    protected add ( MapElement element );

    /**
     * Operation loadFromStream
     *
     * @param stream - 
     * @return 
     */
    protected loadFromStream ( InputStream stream );

    /**
     * Operation saveToStream
     *
     * @param stream - 
     * @return 
     */
    protected saveToStream ( OutputStream stream );

}

