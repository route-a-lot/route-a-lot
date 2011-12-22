package kit.route.a.lot.map.infosupply;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import kit.route.a.lot.common.Selection;
import kit.route.a.lot.common.StringTrie;
import kit.route.a.lot.map.MapElement;
import kit.route.a.lot.map.infosupply.AddressOperator;

public class TrieAddressOperator implements AddressOperator {

    /** Attributes */
    /**
     * 
     */
    private StringTrie<MapElement> mapElements;

    @Override
    public List<String> suggestCompletions(String expression) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Selection select(String address) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void add(MapElement element) {
        // TODO Auto-generated method stub

    }

    @Override
    public void loadFromStream(InputStream stream) {
        // TODO Auto-generated method stub

    }

    @Override
    public void saveToStream(OutputStream stream) {
        // TODO Auto-generated method stub

    }
}
