package kit.route.a.lot.map.AddressOperatorTest;


import java.util.ArrayList;
import kit.route.a.lot.map.MapElement;
import kit.route.a.lot.map.Street;

import java.util.Iterator;
import java.lang.Character;
import java.lang.String;


public class StringTrieTest {
    
    private ArrayList<MapElement> tree;
    private Iterator<MapElement> iterator;
    private int maxLength;
    
    public StringTrieTest(){
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

    /**
     * Operation search
     * 
     * @param name
     *            -
     * @return List<T>
     */
    public ArrayList<String> search(String name) {
        int first = Character.getNumericValue(name.charAt(0));
        String tmpName;
        ArrayList<String> completions = new ArrayList<String>(10);
        Street [] tmp = new Street[1];
        Street[] elements = tree.toArray(tmp);
        for(int i = 0; i < elements.length;i++){
            tmpName = elements[i].getName();
            if(!(tmpName == null)){
                if(first < Character.getNumericValue(tmpName.charAt(0))){
                    /*bricht ab wenn erster Buchstabe lexikographisch größer ist*/
                    return completions;
                }else if(tmpName.startsWith(name)){
                    completions.add(elements[i].getName());
                }
            }
        }
        return completions;
    }
    
    public void RadixSort(){
        for(int i = (maxLength-1); i >=0; i--){
            iterator = tree.iterator();
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
        }//end for

    }//radixsort
    
    public void kSort(){
        ArrayList<MapElement> [] buckets = new ArrayList[26];
        for(int i =0; i<26; i++){
            buckets[i] = new ArrayList<MapElement>();
        }//end for
        iterator =  tree.iterator();
        while(iterator.hasNext()){
            MapElement element = iterator.next();
            System.out.println(element.getID());
            ArrayList<MapElement> list = buckets[element.getID()];
            buckets[element.getID()].add(element);
        }//end while
        tree = buckets[0];
        for(int i = 1; i < 26; i++){
            tree.addAll(buckets[i]);
            if(buckets[i] == null){
                System.out.println("bucket ist null");
            };
        }//end for

    }//end ksort

}

