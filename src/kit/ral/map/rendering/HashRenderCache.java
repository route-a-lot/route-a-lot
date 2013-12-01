
/**
Copyright (c) 2012, Matthias Grundmann, Daniel Krauß, Josua Stabenow
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

package kit.ral.map.rendering;

import kit.ral.common.Coordinates;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

public class HashRenderCache implements RenderCache {

    /**
     * Map, mapping tile specifiers to tiles
     */
    private HashMap<Long, Tile> map = new HashMap<Long, Tile>();
    
    /**
     * a FIFO list for the cache replacement
     */
    // TODO EXTEND: more elaborate aging algorithm
    private LinkedList<Long> leastRecentlyUsed = new LinkedList<Long>();
   

    /**
     * Crates a new tile cache which internally uses a
     * hash map for storing / retrieving cache entries.
     */
    public HashRenderCache() {
        resetCache();
    }

    @Override
    public Tile queryCache(Coordinates topLeft, int tileSize, int detail) {
        long specifier = Tile.getSpecifier(topLeft.getLatitude(), topLeft.getLongitude(), tileSize, detail);
        if (leastRecentlyUsed.remove(specifier)) {
            leastRecentlyUsed.addLast(specifier);
        }
        return map.get(specifier);
    }
    
    @Override
    public Tile addToCache(Tile tile) {
        long specifier = tile.getSpecifier();
        Tile removedTile = null;
        map.put(specifier, tile);
        if (leastRecentlyUsed.size() > CACHE_SIZE) {
            removedTile = map.remove(leastRecentlyUsed.removeFirst());   
        }
        leastRecentlyUsed.addLast(specifier);
        return removedTile;        
    }

    @Override
    public Collection<Tile> resetCache() {
        Collection<Tile> cache = map.values();
        map = new HashMap<Long, Tile>();
        leastRecentlyUsed = new LinkedList<Long>();
        return cache;
    }
    
}
