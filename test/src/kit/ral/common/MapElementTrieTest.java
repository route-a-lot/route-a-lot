package kit.route.a.lot.common;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import kit.route.a.lot.map.infosupply.ArrayElementDB;

import org.junit.BeforeClass;
import org.junit.Test;


public class MapElementTrieTest {


    private String text = "";
    private String line;
    private StringTrie trie = new StringTrie();
    private String[] words = null;
    private ArrayElementDB db = new ArrayElementDB();

    @BeforeClass
    public void setUp() {
        /*--------------Worte generieren-----------------------------------------*/
        try {
            File file = new File("test/resources/Text");
            BufferedReader reader = new BufferedReader(new FileReader(file));
            while (!((line = reader.readLine()) == null)) {
                String[] split = line.split(" ");
                for (int i = 0; i < split.length; i++) {
                    System.out.println(split[i]);
                    text = text + "FFF" + split[i];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        words = text.split("FFF");

        /* Aufbau des Trie mit dem generierten Text */
        // System.out.println("Anzahl eingefügter Worte:" + words.length);
        for (int i = 0; i < words.length; i++) {
            MapElementMock element = new MapElementMock(words[i]);
            element.setID(i);
            db.addMapElement(element);
            trie.insert(words[i], element.getID());
        }
    }


    @Test
    public void testInsert() {
        /* Testet ob alle Worte in den Trie eingefügt wurden */
        for (int i = 0; i < words.length; i++) {
            ArrayList<Integer> list = trie.select(words[i]);
            assertTrue(list.size() > 0);
            // System.out.println(list.remove(0).getName());
        }
    }


    @Test
    public void testSearch() {
        /*-----------------------------------------------------------------------*/
        /* Testet ob alle Worte in den Trie eingefügt wurden */
        ArrayList<Integer> list = new ArrayList<Integer>();
        int auswahl = 0;
        for (int i = 0; i < words.length; i++) {
            if (words[i].length() > 0) {
                list = trie.select(words[i]);
            } else {
                list = new ArrayList<Integer>();
            }
            Integer possibleTarget = -1;
            for (Integer id : list) {
                possibleTarget = id;
                if (db.getMapElement(id).getName().equals(words[i])) {
                    // System.out.println("gefunden: "+possibleTarget.getName());
                    // System.out.println("gesucht: " + words[i]);
                    break;
                }
            }

            if (!(db.getMapElement(possibleTarget).getName().equals(words[i]))) {
                auswahl++;
                // System.out.println("gefunden: "+possibleTarget.getName());
                // System.out.println("gesucht: " + words[i]);
            }
        }
        assertTrue(auswahl == 0);
    }

    
    @Test
    public void testCompactify() {
        trie.compactify();
        /* Testet ob alle Worte in den Trie eingefügt wurden */
        ArrayList<Integer> list = new ArrayList<Integer>();
        String missingWords = "";
        int counter = 0;
        int anzahl = 0;
        for (int i = 0; i < words.length; i++) {
            if (words[i].length() > 0) {
                String word = StringTrie.normalize(words[i]);
                list = trie.select(words[i]);
                boolean bool = false;
                if (!(list == null)) {
                    // System.out.println("Size: " + wordArray.size() );
                    for (Integer id : list) {
                        // System.out.print( wordArray.get(j)+" ");
                        String found = StringTrie.normalize(db.getMapElement(id).getName());
                        // System.out.println("gesucht: " + words[i]);
                        // System.out.println("gefunden: " + wordArray.get(j) );
                        if (found.equals(word)) {
                            bool = true;
                            counter++;
                            /* Suche beenden wenn Schleife gefunden */
                            System.out.println(word);
                            break;
                        }
                    }
                } else {
                    anzahl++;
                    // System.out.println(words[i]);
                }
                if (bool == false) {

                    missingWords = missingWords + words[i];

                    anzahl++;
                }
            }
        }
        System.out.println("nicht gefundene Worte: " + anzahl);
        System.out.println("missing Words konkateniert" + missingWords);
        System.out.println("gefundene Worte: " + counter);
        System.out.println("Größe des Wordarrays: " + words.length);
        /*
         * MissingWords enthält nur Worte die keinen Strings entsprechen und hat nach Normalisierung die Länge null
         */

        assertTrue(StringTrie.normalize(missingWords).length() == 0);


    }


}
