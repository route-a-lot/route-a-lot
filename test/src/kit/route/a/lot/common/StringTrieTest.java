package kit.route.a.lot.common;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import kit.route.a.lot.map.MapElement;

import org.junit.Test;


public class StringTrieTest {
    
    
    
    private String text = "";
    private String line;
    private StringTrie trie = new StringTrie();
    private String[] words = null;
   
    
    @Test
    public void testInsert() {
        
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
            next.setId(count);
            count++;
            trie.insert(words[i], next);               
        }
        
        /*Testet ob alle Worte in den Trie eingefügt wurden*/
        for(int i = 0; i < words.length; i++){
               ArrayList<MapElement> list = trie.select(words[i]);
               assertTrue(list.size() > 0);
               // System.out.println(list.remove(0).getName());
            }
     }
    

    @Test
    public void testSearch() {
        
            /*--------------Worte generieren-----------------------------------------*/
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
            
            int count = 0;
            /*Aufbau des Trie mit dem generierten Text*/ 
            //System.out.println("Anzahl eingefügter Worte:" + words.length);
            for(int i = 0; i < words.length; i++){
                MapElementMock next = new MapElementMock(words[i]);
                next.setId(count);
                count++;
                trie.insert(words[i], next);               
            }
            
            
        
        /*-----------------------------------------------------------------------*/
        /*Testet ob alle Worte in den Trie eingefügt wurden*/
        ArrayList<MapElement>  list = new  ArrayList<MapElement>();
        int auswahl = 0;
        for(int i = 0; i < words.length; i++){
                if(words[i].length() > 0){
                  list = trie.select(words[i]);
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
                    // System.out.println("gefunden: "+possibleTarget.getName());
                    // System.out.println("gesucht: " + words[i]);
                }
            }
         
           assertTrue(auswahl == 0);
            
    }

 
    @Test
    public void testCompactify() {
        
        /*--------------Worte generieren-----------------------------------------*/
        try{
            File file = new File("test/resources/Validierung");
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
                String missingWords = "";
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
                       // System.out.println(words[i]);
                    }
                        if(bool == false){
                        
                        missingWords = missingWords + words[i];
                        
                            anzahl++;
                        }
                    }
                }
                System.out.println("nicht gefundene Worte: " + anzahl);
                System.out.println("missing Words konkateniert"+ missingWords);
                System.out.println("gefundene Worte: " + counter);
                System.out.println("Größe des Wordarrays: " + words.length);
                /*MissingWords enthält nur Worte die keinen Strings entsprechen
                  und hat nach Normalisierung die Länge null */
        
        assertTrue(StringTrie.normalize(missingWords).length() == 0);
        
       
    }
    
    

}
