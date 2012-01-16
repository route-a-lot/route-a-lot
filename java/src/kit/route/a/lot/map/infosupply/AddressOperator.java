package kit.route.a.lot.map.infosupply;

import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
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
    List<String> suggestCompletions(String expression);

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
     * Operation loadFromStream
     * 
     * @param stream
     *            -
     * @return
     * @return
     */
    void loadFromStream(DataInputStream stream) throws IOException;

    /**
     * Operation saveToStream
     * 
     * @param stream
     *            -
     * @return
     * @return
     */
    void saveToStream(DataOutputStream stream) throws IOException;

}
