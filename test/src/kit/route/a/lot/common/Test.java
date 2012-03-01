package kit.route.a.lot.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import kit.route.a.lot.map.MapElement;

class Test{ 
    
    
 

public static void main(String[]args){
    
   String text = "";
   String line;
   StringTrie trie = new StringTrie();
   String[] words = null;
   ArrayList<MapElement> list = new ArrayList<MapElement>();

    try{
        File file = new File("test/resources/Text");
        BufferedReader reader = new BufferedReader(new FileReader(file));
        while( !( (line = reader.readLine() ) == null) ) {
            String[] split = line.split(" ");
            for(int i = 0; i < split.length; i++){
                System.out.println(split[i]);
                text = text +"FFF"+ split[i];
            }   
        }
        } catch (IOException e){
            e.printStackTrace();
        }

     
        String regex = "FFF";
        words = text.split(regex);
        System.out.println("");
        System.out.println("-------------------------------------");
        System.out.println("eingefügte Worte:");
        System.out.println(words.length);
        int count = 0;
        /*Aufbau des Trie mit dem generierten Text*/ 
        System.out.println("Anzahl eingefügter Worte:" + words.length);
        for(int i = 0; i < words.length; i++){
            MapElementMock next = new MapElementMock(words[i]);
           // System.out.println(next.getName() == words[i]);
            next.setId(count);
            count++;
            trie.insert(words[i], next);               
        }
            trie.compactify();
        /*Testet ob alle Worte in den Trie eingefügt wurden*/
        int auswahl = 0;
        for(int i = 0; i < words.length; i++){
                System.out.println(words[i]);
                
                if(words[i].length() > 0){
                    list = trie.search(words[i]);
                    System.out.println(list);
                } else {
                    list = new ArrayList<MapElement>();
                }
                Iterator<MapElement> iterator = list.iterator();
                MapElement possibleTarget = new MapElementMock("");
                boolean found = false;
                while(iterator.hasNext() && !found){
                    possibleTarget = iterator.next();
                    if(possibleTarget.getName().toLowerCase().equals(words[i].toLowerCase())){
                       // System.out.println("gefunden: "+possibleTarget.getName());
                       // System.out.println("gesucht: " + words[i]);
                        found = true;
                    }
                }
               
                if( !(possibleTarget.getName().toLowerCase().equals(words[i].toLowerCase()) ) ){
                    auswahl++;
                     System.out.println("gefunden: "+possibleTarget.getName());
                     System.out.println("gesucht: " + words[i]);
                }
            }
        System.out.println("nicht passende Elemente ausgewählt " + auswahl);


}//end main

	

}//end class
