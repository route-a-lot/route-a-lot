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
               ArrayList<MapElement> list = trie.search(words[i]);
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
                  list = trie.search(words[i]);
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
       /*-------------kompaktifizieren-----------------------------------------*/ 
             trie.compactify(); 
     /*-----------------------------------------------------------------------*/
     /*Testet ob alle Worte in den Trie eingefügt wurden*/
     ArrayList<MapElement>  list = new  ArrayList<MapElement>();
     int auswahl = 0;
     for(int i = 0; i < words.length; i++){
             if(words[i].length() > 0){
               list = trie.search(words[i]);
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
                  //System.out.println("gefunden: "+possibleTarget.getName());
                 // System.out.println("gesucht: " + words[i]);
             }
         }
        System.out.println("auswahl: "+auswahl);
        assertTrue(auswahl == 0);
        
       
    }
    
    

}
