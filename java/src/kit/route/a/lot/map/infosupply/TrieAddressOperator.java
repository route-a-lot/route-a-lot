package kit.route.a.lot.map.infosupply;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    public List<String> suggestCompletions(String expression) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Selection select(String address) {
        // TODO Auto-generated method stub
            ArrayList<MapElement> tree = mapElements.getTree();
            MapElement [] tmp = new MapElement[8];
            MapElement[] elements = tree.toArray(tmp);
            Street item = new Street();
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
    public void loadFromStream(DataInputStream stream) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void saveToStream(DataOutputStream stream) throws IOException {
        // TODO Auto-generated method stub

    }
}
