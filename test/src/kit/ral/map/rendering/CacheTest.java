
/**
Copyright (c) 2012, Matthias Grundmann, Malte Wolff, Josua Stabenow
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

import static org.junit.Assert.*;

import kit.ral.common.Coordinates;

import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class CacheTest {
    
    HashRenderCache cache;
    
    @BeforeClass
    public static void setUpClass() {
        PropertyConfigurator.configure("config/log4j.conf");
    }

    @Before
    public void setUp() throws Exception {
        cache = new HashRenderCache();
        for(int i = 0; i < HashRenderCache.CACHE_SIZE; i++) {
            cache.addToCache(new Tile(new Coordinates(i+5.0f, i+5.0f), 50, 1));
        }
    }

    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void testCache() {
        Tile tile = cache.queryCache(new Coordinates(0.0f, 0.0f), 50, 1);
        assertEquals(tile, cache.queryCache(new Coordinates(0.0f, 0.0f), 50, 1));
        for(int i = 0; i < HashRenderCache.CACHE_SIZE - 1; i++) {
            cache.queryCache(new Coordinates(1+i+5.0f, 1+i+5.0f), 50, 1);
        }
        assertEquals(tile, cache.addToCache(new Tile(new Coordinates(128+5.0f, 128+5.0f), 50, 1)));
        cache.resetCache();
        assertEquals(null, cache.queryCache(new Coordinates(0.0f, 0.0f), 50, 1));
    }
}
