package kit.ral.map.AddressOperatorTest;

import java.util.ArrayList;
import java.util.Arrays;
import kit.ral.common.Selection;
import kit.ral.map.MapElement;
import kit.ral.map.Node;
import kit.ral.map.Street;

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
            ArrayList<MapElement> tree = mapElements.getTree();
            Street [] tmp = new Street[1];
            Street[] elements = tree.toArray(tmp);
            Street item = new Street(address,null);
            
            
            int index = Arrays.binarySearch(elements,item);
            if(index < 0){return null;}
            System.out.println("Index"+ index);
            Street foundItem = (Street)elements[index];
            Node[] nodes = foundItem.getNodes(); 
            index = (nodes.length)/2;
            Selection selection = new Selection(null,nodes[index].getID(),nodes[index+1].getID(),0.0f,"");
            return selection;     

    }

    
    public void add(MapElement element) {
        if(element instanceof Street){
            mapElements.insert(null,element);
        }
    }

 
}

