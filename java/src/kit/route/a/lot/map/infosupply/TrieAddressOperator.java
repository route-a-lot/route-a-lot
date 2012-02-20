package kit.route.a.lot.map.infosupply;


import java.io.DataInputStream;
import java.io.DataOutputStream;
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
        ArrayList<String> completions = mapElements.search(expression);
        return completions;
    }

    @Override
    public Selection select(String address) {
        // TODO Auto-generated method stub
            ArrayList<MapElement> tree = mapElements.getTree();
            Street [] tmp = new Street[1];
            Street[] elements = tree.toArray(tmp);
            Street item = new Street(address,null);
            int index = Arrays.binarySearch(elements,item);
            Street foundItem = (Street)elements[index];
            Node[] nodes = foundItem.getNodes(); 
            index = (nodes.length)/2;
            Selection selection = new Selection(nodes[index].getID(),nodes[index+1].getID(),0.0f,null);
            return selection;     

    }
 
    @Override
    public void add(MapElement element) {
        if(element instanceof Street){
            mapElements.insert(null,element);
        }
    }
    
    @Override
    public void buildTrie(){
        /*sortieren mit radixSort*/
        mapElements.radixSort();
    }

    @Override
    public void loadFromStream(DataInputStream stream) throws IOException {
        mapElements = StringTrie.loadFromStream(stream);
    }

    @Override
    public void saveToStream(DataOutputStream stream) throws IOException {
        mapElements.saveToStream(stream);
    }
    
    public boolean equals(Object other) {
        // TODO: dummy
        return true;
    }
}
