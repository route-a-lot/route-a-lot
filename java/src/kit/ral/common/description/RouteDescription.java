
/**
Copyright (c) 2012, Daniel Krauß, Matthias Grundmann, Jan Jacob, Josua Stabenow
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

package kit.ral.common.description;

import java.util.ArrayList;


public class RouteDescription {
    public ArrayList<String> captions;
    public ArrayList<String> descriptions;
    public ArrayList<Integer> positionIDs;
    
    public RouteDescription() {
        captions = new ArrayList<String>();
        descriptions = new ArrayList<String>();
        positionIDs = new ArrayList<Integer>();
    }
    
    public boolean equals(Object other) {
        if(other == this) {
            return true;
        }
        if(!(other instanceof RouteDescription)) {
            return false;
        }
        RouteDescription comparee = (RouteDescription) other;
        return captions.equals(comparee.captions)
                && descriptions.equals(comparee.descriptions)
                && positionIDs.equals(comparee.positionIDs);
    }
    
    public ArrayList<String> getCaptions() {
        return captions;
    }
    
    public void setCaptions(ArrayList<String> captions) {
        this.captions = captions;
    }
    
    public ArrayList<String> getDescriptions() {
        return descriptions;
    }
    
    public void setDescriptions(ArrayList<String> descriptions) {
        this.descriptions = descriptions;
    }
    
    public ArrayList<Integer> getPositionIDs() {
        return positionIDs;
    }
    
    public void setPositionIDs(ArrayList<Integer> positionIDs) {
        this.positionIDs = positionIDs;
    }
    
    
}
