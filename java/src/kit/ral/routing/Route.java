
/**
Copyright (c) 2012, Matthias Grundmann, Jan Jacob, Daniel Krauß, Josua Stabenow
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

import java.util.ArrayList;
import java.util.List;

public class Route {
    /*
     * 
     * Basically, a "Route" is an inverted tree of nodes and weights with the start-id as the root.
     * This greatly reduces Heap-size as a lot of routes share a great part of their way
     * 
     */
    private int to;
    private int length;
    private Route from;
    
    public Route(int to, int weight) {
        this.to = to;
        this.length = weight;
    }

    public Route(int to, int weight, Route route) {
        this.to = to;
        if (route == null) {
            length = weight;
        } else {
            length = route.getLength() + weight;
        }
        from = route;
    }

    public Route() {
        to = -1;
        length = 0;
        from = null;
    }

    public int getNode() {
        return to;
    }
    
    public Route getRoute() {
        if (from == null) {
            // Quick 'n' dirty
            return this;
        }
        return from;
    }

    public List<Integer> toList() {
        List<Integer> result = new ArrayList<Integer>();
        result.add((Integer) to);
        if (from == null) {
            return result;
        }
        else {
            List<Integer> tmp = from.toList();
            tmp.addAll(result);
            return tmp;
        }
    }

    public int getLength() {
        return length;
    }
    
    public String toString() {
        if (from == null) {
            return "" + to;
        }
        return to + " - " + from.toString();
    }
}
