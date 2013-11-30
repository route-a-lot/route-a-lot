
/**
Copyright (c) 2012, Yvonne Braun, Josua Stabenow
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
        * Redistributions of source code must retain the above copyright
          notice, this list of conditions and the following disclaimer.
        * Redistributions in binary form must reproduce the above copyright
          notice, this list of conditions and the following disclaimer in the
          documentation and/or other materials provided with the distribution.
        * The names of the contributors may not be used to endorse or promote products
          derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
**/

package kit.ral.common;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import kit.ral.common.util.StringUtil;
import kit.ral.map.info.ArrayElementDB;

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
            ArrayList<Integer> list = trie.search(words[i]);
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
                list = trie.search(words[i]);
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
                String word = StringUtil.normalize(words[i]);
                list = trie.search(words[i]);
                boolean bool = false;
                if (!(list == null)) {
                    // System.out.println("Size: " + wordArray.size() );
                    for (Integer id : list) {
                        // System.out.print( wordArray.get(j)+" ");
                        String found = StringUtil.normalize(db.getMapElement(id).getName());
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

        assertTrue(StringUtil.normalize(missingWords).length() == 0);


    }


}
