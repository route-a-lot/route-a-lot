package kit.ral.map.info;


import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import kit.ral.common.Selection;
import kit.ral.common.StringTrie;
import kit.ral.controller.State;
import kit.ral.map.MapElement;

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
        MapInfo mapInfo = State.getInstance().getMapInfo();
        for (int id : mapElements.search(expression)) {
            completions.add(mapInfo.getMapElement(id).getFullName());     
        }
        return completions;
    }

    @Override
    public Selection select(String address) {
        MapInfo mapInfo = State.getInstance().getMapInfo();
        for (int id : mapElements.search(address)) {
            MapElement element = mapInfo.getMapElement(id);
            if (element.getFullName().equals(address)) {
                return element.getSelection();
            }
        }
        return null;
    }

    @Override
    public void add(MapElement element) {
        if (element.getName().length() > 0 && element.getID() >= 0) {
            mapElements.insert(element.getFullName(), element.getID());
        }
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
        return (other != null) && (other instanceof TrieAddressOperator) 
                 && (((TrieAddressOperator) other).mapElements.equals(mapElements));
    }
}
