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
            ArrayList<MapElement> wordArray = new ArrayList<MapElement>();
            int counter = 0;        
            int anzahl = 0;
            for(int i = 0; i < words.length; i++){
                if(words[i].length() > 0){
                String word = StringTrie.normalize(words[i]);
                wordArray = trie.select(words[i]);
                boolean bool = false;
                if(!(wordArray == null)){
                      // System.out.println("Size: " + wordArray.size() );
                               for(int j = 0; j < wordArray.size(); j++){
                                        //System.out.print( wordArray.get(j)+" ");
                        String found = StringTrie.normalize(wordArray.get(j).getName() );
                    //  System.out.println("gesucht: " + words[i]);
                    //  System.out.println("gefunden: " + wordArray.get(j) );
                        if( found.equals(word)){
                            bool = true;
                            counter++;
                            /*Suche beenden wenn Schleife gefunden*/
                            j = wordArray.size();   
                            System.out.println(word);
                        } 
                                }
                } else {
                    anzahl++;
                    System.out.println(words[i]);
                }
                    if(bool == false){
                    
                    System.out.println(words[i]);
                    
                        anzahl++;
                    }
                }
            }
            System.out.println("nicht gefundene Worte: " + anzahl);
            System.out.println("gefundene Worte: " + counter);
            System.out.println("Größe des Wordarrays: " + words.length);
}//end main

	

}//end class
