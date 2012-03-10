package kit.route.a.lot.map.infosupply;


import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.common.StringTrie;
import kit.route.a.lot.controller.State;
import kit.route.a.lot.map.MapElement;

public class TrieAddressOperator implements AddressOperator {

    private StringTrie mapElements;

    public TrieAddressOperator() {
        this.mapElements = new StringTrie();
    }

    @Override
    public ArrayList<String> getCompletions(String expression) {
        if(expression.length() < 3) {
            return null;
        }
        ArrayList<String> completions = new ArrayList<String>();
       
        for (Integer id : mapElements.search(expression)) {
            String name = State.getInstance().getMapInfo().getMapElement(id).getName();
            if (name != null && name.length() != 0) {
                completions.add(name);
            }       
        }
        return completions;
    }

    @Override
    public Selection select(String address) {
        ArrayList<Integer> targets = mapElements.search(address);
        if (targets == null || targets.size() == 0) {
            return null;
        }
        return State.getInstance().getMapInfo().getMapElement(targets.remove(0)).getSelection();
    }

    @Override
    public void add(MapElement element) {
        mapElements.insert(element.getName(), element.getID());
    }

    @Override
    public void compactify() {
        mapElements.compactify();
    }

    @Override
    public void loadFromInput(DataInput input) throws IOException {
        mapElements = StringTrie.loadFromInput(input);
    }

    @Override
    public void saveToOutput(DataOutput output) throws IOException {
        mapElements.saveToOutput(output);
    }


    public boolean equals(Object other) {
        // TODO: dummy
        return true;
    }
}
