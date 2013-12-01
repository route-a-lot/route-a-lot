
/**
Copyright (c) 2012, Josua Stabenow
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

package kit.ral.heightinfo;

import kit.ral.common.Coordinates;

import java.util.HashMap;

public class HashHeightmap extends Heightmap {

    private HashMap<Long, HeightTile> map = new HashMap<Long, HeightTile>();
      
    // ADMINISTRATIVE FUNCTIONS
    
    @Override
    public void addHeightTile(HeightTile tile) {
        if (tile != null) {
            map.put(tile.getSpecifier(), tile);
        }
    }
    
    // RETRIEVAL FUNCTIONS

    @Override
    public float getHeight(Coordinates pos) {
        HeightTile tile = map.get(HeightTile.getSpecifier(
                pos.getLatitude(), pos.getLongitude()));
        return (tile != null) ? tile.getHeight(pos) : UNDEFINED_HEIGHT;
    }
        
    // MISCELLANEOUS
    
    public boolean equals(Object other) {
        if(other == this) {
            return true;
        }
        if(!(other instanceof HashHeightmap)) {
            return false;
        }
        HashHeightmap comparee = (HashHeightmap) other;
        return map.equals(comparee.map);
    }
    
}
