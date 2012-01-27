package kit.route.a.lot.map.AddressOperatorTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kit.route.a.lot.common.Selection;
import kit.route.a.lot.common.StringTrie;
import kit.route.a.lot.map.MapElement;
import kit.route.a.lot.map.Node;
import kit.route.a.lot.map.Street;

public class StringOperatorTester {

    private StringTrieTest mapElements;
    
    public StringOperatorTester(){
        this.mapElements = new StringTrieTest();
    }

    public StringTrieTest getTrie(){
        return mapElements;
    }
    
    public ArrayList<String> suggestCompletions(String expression) {
        ArrayList<String> completions = mapElements.search(expression);
        return completions;
    }
    

    
    public Selection select(String address) {
        // TODO Auto-generated method stub
            ArrayList<MapElement> tree = mapElements.getTree();
            Street [] tmp = new Street[1];
            Street[] elements = tree.toArray(tmp);
            Street item = new Street(address,null);
            
            int index = Arrays.binarySearch(elements,item);
            System.out.println("Index"+ index);
            Street foundItem = (Street)elements[index];
            Node[] nodes = foundItem.getNodes(); 
            index = (nodes.length)/2;
            Selection selection = new Selection(nodes[index].getID(),nodes[index+1].getID(),0.0f,null);
            return selection;     

    }

    
    public void add(MapElement element) {
        if(element instanceof Street){
            mapElements.insert(null,element);
        }
    }

 
}

