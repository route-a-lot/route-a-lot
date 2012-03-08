package kit.route.a.lot.map.infosupply;


import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.map.MapElement;

public class TrieAddressOperator implements AddressOperator {

    private MapElementTrie mapElements;
    
    public TrieAddressOperator(){
        this.mapElements = new MapElementTrie();
    }

    @Override
    public ArrayList<String> getCompletions(String expression) {
        if(expression.length() < 3) {
            return null;
        }
        ArrayList<String> completions = new ArrayList<String>();
       
        for (MapElement element : mapElements.search(expression)) {
            String name = element.getName();
            if(!(name == null || name.length() == 0) )
                completions.add(element.getName());
            }       
        return completions;
    }

    @Override
    public Selection select(String address) {
            ArrayList<MapElement> targets = mapElements.search(address);
            if(targets == null || targets.size() == 0){
                return null;
            }
           return targets.remove(0).getSelection();
    }
 
    @Override
    public void add(MapElement element) {
           mapElements.insert(element.getName(), element);
    }
    @Override
    public void compactify(){
        mapElements.compactify();
    }
    
   @Override
    public void loadFromInput(DataInput input) throws IOException {
       mapElements = MapElementTrie.loadFromInput(input);
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
