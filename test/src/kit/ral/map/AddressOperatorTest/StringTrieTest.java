
/**
Copyright (c) 2012, Matthias Grundmann, Yvonne Braun, Josua Stabenow
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

package kit.ral.map.AddressOperatorTest;


import java.util.ArrayList;
import kit.ral.map.MapElement;
import kit.ral.map.Street;

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
        @SuppressWarnings("unchecked")
        ArrayList<MapElement> [] buckets = new ArrayList[26];
        for(int i =0; i<26; i++){
            buckets[i] = new ArrayList<MapElement>();
        }//end for
        iterator =  tree.iterator();
        while(iterator.hasNext()){
            MapElement element = iterator.next();
            System.out.println(element.getID());
            // ArrayList<MapElement> list = buckets[element.getID()];
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

