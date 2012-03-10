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
