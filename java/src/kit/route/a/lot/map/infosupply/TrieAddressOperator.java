package kit.route.a.lot.map.infosupply;


import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.common.StringTrie;
import kit.route.a.lot.map.MapElement;
import kit.route.a.lot.map.Node;
import kit.route.a.lot.map.Street;

public class TrieAddressOperator implements AddressOperator {

    private StringTrie mapElements;
    
    public TrieAddressOperator(){
        this.mapElements = new StringTrie();
    }

    @Override
    public ArrayList<String> suggestCompletions(String expression) {
        if(expression.length() < 3) {
            return null;
        }
        ArrayList<String> completions = new ArrayList<String>();
        for (MapElement element : mapElements.search(expression)) {
            completions.add(element.getName());
        }        
        return completions;
    }

    @Override
    public Selection select(String address) {
        // TODO Auto-generated method stub
            ArrayList<MapElement> targets = mapElements.search(address);
            System.out.println("anzahl gefundener Strassen: " + targets.size() );
            if(targets == null || targets.size() == 0){
                return null;
            }
            
            Street target = (Street)targets.remove(0);
            Node[] nodes = target.getNodes(); 
            if(nodes == null || nodes.length == 0){
                return null;
            } else {
               int index = (nodes.length)/2;  
               return target.getSelection(target.getNodes()[index].getPos());
            }
    }
 
    @Override
    public void add(MapElement element) {
        if(element instanceof Street){
            mapElements.insert(element.getName(),element);
            System.out.println(element.getName());
        }
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
