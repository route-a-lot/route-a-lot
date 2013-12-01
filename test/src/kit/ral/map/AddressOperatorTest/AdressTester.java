
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

import kit.ral.common.Coordinates;
import kit.ral.common.Selection;
import kit.ral.map.MapElement;
import kit.ral.map.Node;
import kit.ral.map.Street;

import java.util.ArrayList;
import java.util.Iterator;


public class AdressTester {

    /**
     * @param args
     */
    public static void main(String[] args) {
        StringOperatorTester tester = new StringOperatorTester();
        Coordinates c1 = new Coordinates(1,1);
        Coordinates c2 = new Coordinates(2,2);
        Coordinates c3 = new Coordinates(3,3);
        Coordinates c4 = new Coordinates(4,4);
        Coordinates c5 = new Coordinates(5,5);
        Coordinates c6 = new Coordinates(6,6);
        Coordinates c7 = new Coordinates(7,7);
        Coordinates c8 = new Coordinates(8,8);
        Coordinates c9 = new Coordinates(9,9);
        Coordinates c10 = new Coordinates(10,10);
        Coordinates c11 = new Coordinates(11,11);
        Coordinates c12 = new Coordinates(12,12);
        Node n1 = new Node(c1, 32540907);
        Node n2 = new Node(c2, 20456894);
        Node n3 = new Node(c3, 20456890);
        Node n4 = new Node(c4, 20456891);
        Node n5 = new Node(c5, 335900880);
        Node n6 = new Node(c6, 335902584);
        Node n7 = new Node(c7, 335900882);
        Node n8 = new Node(c8, 335900883);
        Node n9 = new Node(c9, 335900930);
        Node n10 = new Node(c10, 335900928);
        Node n11 = new Node(c11, 335900923);
        Node n12 = new Node(c12, 335900916);
        
        Node[]node1 = {n1,n2,n3,n4};
        Node[]node2 = {n5,n6,n7,n8};
        Node[]node3 = {n9,n10,n11,n12};
        
        Street s1 = new Street("Bannwaldallee",null);
        s1.setNodes(node1);
        Street s2 = new Street("Hertzstraße",null);
        s2.setNodes(node2);
        Street s3 = new Street("Karlsruher Weg",null);
        s3.setNodes(node3);
        /*zum testen von Suggest Completions*/
        Street s4 = new Street("Bellheimer Straße",null);
        Street s5 = new Street("Hagenbacher Straße",null);
        Street s6 = new Street("Bergzaberner Straße",null);
        Street s7= new Street("Kaiserslauterner Straße",null);
        Street s8 = new Street("Ballstrasse",null);
        Street s9 = new Street("Ballonweg",null);
        
        String scharf = "ß";
        System.out.println("scharf: "+Character.getNumericValue(scharf.charAt(0)));
        tester.add(s1);
        tester.add(s2);
        tester.add(s3);
        tester.add(s4);
        tester.add(s5);
        tester.add(s6);
        tester.add(s7);
        tester.add(s8);
        tester.add(s9);
        
        ArrayList<MapElement> elements = tester.getTrie().getTree();
        System.out.println("alte Reihenfolge");
        System.out.println(elements.get(0).getName());
        System.out.println(elements.get(1).getName());
        System.out.println(elements.get(2).getName());
        System.out.println(elements.get(3).getName());
        System.out.println(elements.get(4).getName());
        System.out.println(elements.get(5).getName());
        System.out.println(elements.get(6).getName());
        System.out.println(elements.get(7).getName());
        System.out.println(elements.get(8).getName());
        
        tester.getTrie().RadixSort();
        elements = tester.getTrie().getTree();
        System.out.println("neue Reihenfolge");
        System.out.println(elements.get(0).getName());
        System.out.println(elements.get(1).getName());
        System.out.println(elements.get(2).getName());
        System.out.println(elements.get(3).getName());
        System.out.println(elements.get(4).getName());
        System.out.println(elements.get(5).getName());
        System.out.println(elements.get(6).getName());
        System.out.println(elements.get(7).getName());
        System.out.println(elements.get(8).getName());
        
        
        Selection selection = tester.select("Bannwaldallee");
        System.out.println("FromID: "+selection.getFrom()+"ToID: "+selection.getTo());
        
        /*Bannwaldallee
        ref="32540907"
        ref="20456894"
        ref="20456890"
        ref="20456891"
        Hertzstraße
        <nd ref="335900880"/>
        <nd ref="335902584"/>
        <nd ref="335900882"/>
        <nd ref="335900883"/>
        Karlsruher Weg
        <nd ref="335900930"/>
        <nd ref="335900928"/>
        <nd ref="335900923"/>
        <nd ref="335900916"/>*/
        
        /*TestSuggestCompletions*/
        /*Test1*/
        ArrayList<String> completions = tester.suggestCompletions("B");
        Iterator<String> iterator = completions.iterator();
        while(iterator.hasNext()){
            System.out.println("I would suggest:"+iterator.next());
        }//end while
        System.out.println("next test");
        /*Test2*/
        completions = tester.suggestCompletions("Be");
        iterator = completions.iterator();
        while(iterator.hasNext()){
            System.out.println("I would suggest:"+iterator.next());
        }//end while
        System.out.println("next test");
        /*Test3*/
        completions = tester.suggestCompletions("Ball");
        iterator = completions.iterator();
        while(iterator.hasNext()){
            System.out.println("I would suggest:"+iterator.next());
        }//end while
        System.out.println("next test");
        /*Test4*/
        completions = tester.suggestCompletions("K");
        iterator = completions.iterator();
        while(iterator.hasNext()){
            System.out.println("I would suggest:"+iterator.next());
        }//end while
        
    }//end main

}//end class
