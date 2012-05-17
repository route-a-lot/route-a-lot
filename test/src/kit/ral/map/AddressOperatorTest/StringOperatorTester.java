
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
import java.util.Arrays;
import kit.ral.common.Selection;
import kit.ral.map.MapElement;
import kit.ral.map.Node;
import kit.ral.map.Street;

public class StringOperatorTester {

    private StringTrieTest mapElements;
    
    public StringOperatorTester(){
        this.mapElements = new StringTrieTest();
    }

    public StringTrieTest getTrie(){
        return mapElements;
    }
    
    public ArrayList<String> suggestCompletions(String expression) {
        ArrayList<String> completions = mapElements.search(expression);
        return completions;
    }
    

    
    public Selection select(String address) {
            ArrayList<MapElement> tree = mapElements.getTree();
            Street [] tmp = new Street[1];
            Street[] elements = tree.toArray(tmp);
            Street item = new Street(address,null);
            
            
            int index = Arrays.binarySearch(elements,item);
            if(index < 0){return null;}
            System.out.println("Index"+ index);
            Street foundItem = (Street)elements[index];
            Node[] nodes = foundItem.getNodes(); 
            index = (nodes.length)/2;
            Selection selection = new Selection(null,nodes[index].getID(),nodes[index+1].getID(),0.0f,"");
            return selection;     

    }

    
    public void add(MapElement element) {
        if(element instanceof Street){
            mapElements.insert(null,element);
        }
    }

 
}

