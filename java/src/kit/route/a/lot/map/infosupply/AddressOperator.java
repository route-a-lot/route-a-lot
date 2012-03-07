package kit.route.a.lot.map.infosupply;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;

import kit.route.a.lot.common.Selection;
import kit.route.a.lot.map.MapElement;

public interface AddressOperator {

    /**
     * Operation suggestCompletions
     * 
     * @param expression
     *            -
     * @return List<String>
     */
    List<String> getCompletions(String expression);

    /**
     * Operation select
     * 
     * @param address
     *            -
     * @return Selection
     */
    Selection select(String address);

    /**
     * Operation add
     * 
     * @param element
     *            -
     * @return
     * @return
     */
    void add(MapElement element);
    
    /**
     * 
     */
    void compactify();
    /**
     * Operation loadFromStream
     * 
     * @param stream
     *            -
     * @return
     * @return
     */
    void loadFromInput(DataInput input) throws IOException;

    /**
     * Operation saveToStream
     * 
     * @param stream
     *            -
     * @return
     * @return
     */
    void saveToOutput(DataOutput output) throws IOException;

}
