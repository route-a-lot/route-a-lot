
/**
Copyright (c) 2012, Daniel Krauß, Josua Stabenow
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

package kit.ral.routing;

import java.util.Random;

import kit.ral.common.Selection;
import kit.ral.controller.State;


public class SelectMock {
    
    public static Selection getRandomSelection() {
        int numberOfNodes = State.getInstance().getLoadedGraph().getIDCount() - 1;
        
        Random random = new Random();
        int start = 0;
        boolean found = false;
        while(!found) {
            start = random.nextInt(numberOfNodes);
            if (State.getInstance().getLoadedGraph().getAllNeighbors(start).size() != 0) {
                found = true;
            }
        }
        int target = 0;
        int randomTargetNeighbourNumber = random.nextInt(State.getInstance().getLoadedGraph().getAllNeighbors(start).size()); 
        int i = 0; //there is no get-Method for collection, so we have to go with the Iterator through the list
        for (Integer neighbour : State.getInstance().getLoadedGraph().getAllNeighbors(start)) {
            if (i == randomTargetNeighbourNumber) {
                target = neighbour;
                break;
            }
            i++;
        }
        
        return new Selection(null, start, target, random.nextFloat(), "");
    }
        
}
