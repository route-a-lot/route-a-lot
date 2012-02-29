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

    private StringTrie<MapElement> mapElements;
    private StringTrie<String> adressDict;
    
    public TrieAddressOperator(){
        this.mapElements = new StringTrie<MapElement>();
        this.adressDict = new StringTrie<String>();
    }

    @Override
    public ArrayList<String> suggestCompletions(String expression) {
        ArrayList<String> completions = adressDict.search(expression);
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
            int index = (nodes.length)/2;
            if(nodes == null || nodes.length == 0){
                return null;
            } else if (nodes.length > 1) {
                Selection selection = new Selection(null,nodes[index-1].getID(),nodes[index].getID(),0.0f,"");
                System.out.println(nodes[index-1].getID());
                System.out.println(nodes[index].getID());
                return selection;
            } else {
                return(new Selection(null,nodes[0].getID(),nodes[0].getID(),0.0f,""));
            }

    }
 
    @Override
    public void add(MapElement element) {
        if(element instanceof Street){
            mapElements.insert(element.getName(),element);
            System.out.println(element.getName());
            adressDict.insert(element.getName(), element.getName());
        }
    }
    

   @Override
    public void loadFromInput(DataInput input) throws IOException {
    //    mapElements = StringTrie.loadFromInput(input);
    }

    @Override
    public void saveToOutput(DataOutput output) throws IOException {
      //  mapElements.saveToOutput(output);
    }
    
    
    public boolean equals(Object other) {
        // TODO: dummy
        return true;
    }
}
