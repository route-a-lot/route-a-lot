
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

import kit.ral.common.util.StringUtil;
import kit.ral.map.info.ArrayElementDB;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

class Test {


    public static void main(String[] args) {

        String text = "";
        String line;
        StringTrie trie = new StringTrie();
        String[] words = null;
        ArrayElementDB db = new ArrayElementDB();

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


        String regex = "FFF";
        words = text.split(regex);
        System.out.println("");
        System.out.println("-------------------------------------");
        System.out.println("eingefügte Worte:");
        System.out.println(words.length);
        /* Aufbau des Trie mit dem generierten Text */
        System.out.println("Anzahl eingefügter Worte:" + words.length);
        for (int i = 0; i < words.length; i++) {
            MapElementMock element = new MapElementMock(words[i]);
            // System.out.println(next.getName() == words[i]);
            element.setID(i);
            db.addMapElement(element);
            trie.insert(words[i], element.getID());
        }
        trie.compactify();
        /* Testet ob alle Worte in den Trie eingefügt wurden */
        ArrayList<Integer> list = new ArrayList<Integer>();
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
                    System.out.println(words[i]);
                }
                if (bool == false) {

                    System.out.println(words[i]);

                    anzahl++;
                }
            }
        }
        System.out.println("nicht gefundene Worte: " + anzahl);
        System.out.println("gefundene Worte: " + counter);
        System.out.println("Größe des Wordarrays: " + words.length);
    }// end main
}// end class
