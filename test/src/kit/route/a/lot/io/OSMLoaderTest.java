package kit.route.a.lot.io;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class OSMLoaderTest {
    
    OSMLoader loader = new OSMLoader();

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() {
        loader.importMap(new File("test/resources/karlsruhe_small.osm"));
    }

}
