package kit.route.a.lot.map.AddressOperatorTest;

import java.util.ArrayList;
import java.util.Iterator;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.map.MapElement;
import kit.route.a.lot.map.Node;
import kit.route.a.lot.map.Street;


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
        Node n1 = new Node(c1);
        Node n2 = new Node(c2);
        Node n3 = new Node(c3);
        Node n4 = new Node(c4);
        Node n5 = new Node(c5);
        Node n6 = new Node(c6);
        Node n7 = new Node(c7);
        Node n8 = new Node(c8);
        Node n9 = new Node(c9);
        Node n10 = new Node(c10);
        Node n11 = new Node(c11);
        Node n12 = new Node(c12);
        
        n1.setID(32540907);
        n2.setID(20456894);
        n3.setID(20456890);
        n4.setID(20456891);
        n5.setID(335900880);
        n6.setID(335902584);
        n7.setID(335900882);
        n8.setID(335900883);
        n9.setID(335900930);
        n10.setID(335900928);
        n11.setID(335900923);
        n12.setID(335900916);
        
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
