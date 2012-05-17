
/**
Copyright (c) 2012, Matthias Grundmann, Jan Jacob, Yvonne Braun, Josua Stabenow
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

package kit.ral.map.info;


import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import kit.ral.common.Selection;
import kit.ral.common.StringTrie;
import kit.ral.controller.State;
import kit.ral.map.MapElement;

public class TrieAddressOperator implements AddressOperator {

    private StringTrie mapElements;

    public TrieAddressOperator() {
        this.mapElements = new StringTrie();
    }

    @Override
    public ArrayList<String> getCompletions(String expression) {
        if(expression.length() < 3) {
            return null;
        }
        ArrayList<String> completions = new ArrayList<String>();
        MapInfo mapInfo = State.getInstance().getMapInfo();
        for (int id : mapElements.search(expression)) {
            completions.add(mapInfo.getMapElement(id).getFullName());     
        }
        return completions;
    }

    @Override
    public Selection select(String address) {
        MapInfo mapInfo = State.getInstance().getMapInfo();
        for (int id : mapElements.search(address)) {
            MapElement element = mapInfo.getMapElement(id);
            if (element.getFullName().equals(address)) {
                return element.getSelection();
            }
        }
        return null;
    }

    @Override
    public void add(MapElement element) {
        if (element.getName().length() > 0 && element.getID() >= 0) {
            mapElements.insert(element.getFullName(), element.getID());
        }
    }

    @Override
    public void compactify() {
        mapElements.compactify();
    }

    @Override
    public void loadFromInput(DataInput input) throws IOException {
        mapElements = StringTrie.loadFromInput(input);
    }

    @Override
    public void saveToOutput(DataOutput output) throws IOException {
        mapElements.saveToOutput(output);
    }


    public boolean equals(Object other) {
        return (other != null) && (other instanceof TrieAddressOperator) 
                 && (((TrieAddressOperator) other).mapElements.equals(mapElements));
    }
}
