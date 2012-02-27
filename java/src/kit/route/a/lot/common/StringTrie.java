package kit.route.a.lot.common;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import kit.route.a.lot.map.MapElement;
import kit.route.a.lot.map.Street;


public class StringTrie {
    
    private ArrayList<MapElement> tree;
    private int maxLength;
    
    public StringTrie(){
        tree = new ArrayList<MapElement>();
        this.maxLength = 0;
    }
    
    
    public ArrayList<MapElement> getTree(){
        return tree;
    }
    
    public void insert(String name, MapElement element) {
        tree.add(element);
        if(element.getName().length() > maxLength){
            maxLength = element.getName().length();
        }
    }   

    public ArrayList<String> search(String name) {
        int first = Character.getNumericValue(name.charAt(0));
        String tmpName;
        ArrayList<String> completions = new ArrayList<String>(10);
        Street [] tmp = new Street[1];
        Street[] elements = tree.toArray(tmp);
        for(int i = 0; i < elements.length;i++){
            tmpName = elements[i].getName();
            if(first < Character.getNumericValue(tmpName.charAt(0))){
                /*bricht ab wenn erster Buchstabe lexikographisch größer ist*/
                return completions;
            }else if(tmpName.startsWith(name)){
                completions.add(elements[i].getName());
            }
        }
        return completions;
    }
    
    
    public void radixSort(){
        for(int i = (maxLength-1); i >=0; i--){
            Iterator<MapElement> iterator = tree.iterator();
            while(iterator.hasNext()){
                MapElement e = iterator.next();
                if(e.getName().length() < (i+1)){
                    e.setID(0);
                } else {
                    int id = Character.getNumericValue(e.getName().charAt(i)) - 10;
                    if((id > 25)|| (id < 0)){id = 25;} /*Sonderzeichen*/
                    e.setID(id);
                }
            }
                System.out.println("ksort");
                kSort();
        }

    }//radixsort
   
    
    public void kSort(){
        @SuppressWarnings("unchecked")
        ArrayList<MapElement> [] buckets = new ArrayList[26];
        for(int i =0; i<26; i++){
            buckets[i] = new ArrayList<MapElement>();
        }
        Iterator<MapElement> iterator =  tree.iterator();
        while(iterator.hasNext()){
            MapElement element = iterator.next();
            System.out.println(element.getID());
            //ArrayList<MapElement> list = buckets[element.getID()];
            buckets[element.getID()].add(element);
        }
        tree = buckets[0];
        for(int i = 1; i < 26; i++){
            tree.addAll(buckets[i]);
            if(buckets[i] == null){
                System.out.println("bucket ist null");
            };
        }

    }//end ksort 

    public static StringTrie loadFromInput(DataInput input) throws IOException {      
        StringTrie result = new StringTrie();
        int treeSize = input.readInt();
        for(int i = 0; i < treeSize; i++) {
            result.tree.add(MapElement.loadFromInput(input, true));
        }
        result.maxLength = input.readInt();
        return result;
    }
    
    public void saveToOutput(DataOutput output) throws IOException {
        output.writeInt(tree.size());
        for(MapElement element: tree) {
            MapElement.saveToOutput(output, element, true);
        }
        output.writeInt(maxLength);          
    }
    
}
