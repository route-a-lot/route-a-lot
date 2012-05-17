
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

import java.util.Collection;

import kit.ral.common.Coordinates;

public interface RenderCache {
    
    /**
     * Maximum number of entries in the cache
     */
    public static final int CACHE_SIZE = 196;

    /**
     * Returns the tile for the given tile specifier if it is in the cache.
     * Otherwise returns null.
     * 
     * @param tileSpecifier
     *            an long as specifier for the tile (see Tile.getSpecifier(Coordinates, int))
     */
    public Tile queryCache(Coordinates topLeft, int tileSize, int detail);

    /**
     * Adds the given tile to the cache. If a tile was deleted from the cache
     * in the process, it is returned.
     */
    public Tile addToCache(Tile tile);

    /**
     * Resets the cache. All stored tiles are returned for finalization.
     */
    public Collection<Tile> resetCache();
}
