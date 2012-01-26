package kit.route.a.lot.map.infosupply;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import kit.route.a.lot.common.Selection;
import kit.route.a.lot.common.StringTrie;
import kit.route.a.lot.map.MapElement;

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
        public Selection select(String address){
            Object[] elements = tree.toArray();
            AdressItem item = new AdressItem(address, null);
            int index = Arrays.binarySearch(elements,item);
            AdressItem foundItem = elements[index];
            MapElement element = foundItem.getElement();
            Nodes[] nodes = element.getNodes(); 
            Selection selection = new Selection(nodes[0].getID(),nodes[1].getID(),0.0f,null);     
    }//end select

    }

    @Override
    public void add(MapElement element) {
        // TODO Auto-generated method stub

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
