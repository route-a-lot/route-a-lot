package kit.route.a.lot.common;

import java.util.ArrayList;
import java.util.List;

import kit.route.a.lot.map.MapElement;


public class StringTrie<T> {
    
    private ArrayList<MapElement> tree;
    
    private StringTrie(){
        tree = new ArrayList();
    }
    
    
    public ArrayList<MapElement> getTree(){
        return tree;
    }
    
    public void insert(String name, MapElement element) {
                tree.add(element);
    }

    /**
     * Operation search
     * 
     * @param name
     *            -
     * @return List<T>
     */
    public List<T> search(String name) {
        /*Textvervollständigung ist nicht implementiert*/
        return null;
    }
}
