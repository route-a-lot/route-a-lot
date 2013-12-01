
/**
Copyright (c) 2012, Matthias Grundmann, Jan Jacob, Josua Stabenow
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

package kit.ral.io;

import kit.ral.common.Progress;
import org.apache.log4j.PropertyConfigurator;
import org.junit.*;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class OSMLoaderTest {

    OSMLoader loader;
    StateMock state;

    @BeforeClass
    public static void setUpClass() {
        PropertyConfigurator.configure("config/log4j.conf");
    }

    @Before
    public void setUp() throws Exception {
        state = new StateMock();
        loader = new OSMLoader(state, new WeightCalculatorMock(state));
    }

    @After
    public void tearDown() throws Exception {
    }

    @Ignore
    // another testfile
    public void testImportSimple() {
        loader.importMap(new File("test/resources/simple.osm"), new Progress());

        assertEquals(3, state.getNodeCount());
        assertEquals(2, state.getGraphStartIDsSize());
        assertEquals(2, state.getGraphEndIDsSize());
        assertEquals(2, state.getGraphWeightsSize());
    }

    @Test
    public void testImportKarlsruheSmall() {
        loader.importMap(new File("test/resources/karlsruhe_small.osm"), new Progress());

        assertEquals(11093, state.getNodeCount());
        assertTrue(0 < state.getWayCount());
    }

    @Test
    public void testImportKarlsruheBig() {
        loader.importMap(new File("test/resources/karlsruhe_big.osm"), new Progress());

        assertEquals(36678, state.getNodeCount());
    }

    @Test
    public void testPerformanceImportKarlsruheBig() {
        long start = System.currentTimeMillis();
        loader.importMap(new File("test/resources/karlsruhe_big.osm"), new Progress());
        long duration = System.currentTimeMillis() - start;

        // System.out.println("Duration: " + duration + " ms");

        assertTrue(duration < 3000);
    }

    @Ignore
    // test file will not be committed to the repository because it is too big ~19G
    public void testImportGermany() {
        loader.importMap(new File("test/resources/germany.osm"), new Progress());

        assertEquals(89049038, state.getNodeCount());
    }

    @Ignore
    // test file will not be committed to the repository because it is too big ~2.8G
    public void testImportBadenWuerttemberg() {
        loader.importMap(new File("test/resources/baden-wuerttemberg.osm"), new Progress());

        assertEquals(13410554, state.getNodeCount());
    }
    
    @Test
    public void testMinimal() {
        loader.importMap(new File("test/resources/testmaps/minimal.osm"), new Progress());
        assertEquals(0, state.getNodeCount());
        assertEquals(0, state.getWayCount());
    }
    
    @Test
    public void testGibberish() {
        loader.importMap(new File("test/resources/testmaps/gibberish.osm"), new Progress());
        assertEquals(0, state.getNodeCount());
        assertEquals(0, state.getWayCount());
    }
    
    @Test
    public void testMissingNode() {
        loader.importMap(new File("test/resources/testmaps/missing-node.osm"), new Progress());
        assertEquals(1, state.getNodeCount());
        assertEquals(0, state.getWayCount());
        
    }
    
    @Test
    public void testNonContinuous() {
        loader.importMap(new File("test/resources/testmaps/non-continuous.osm"), new Progress());
        assertEquals(2, state.getNodeCount());
        assertEquals(0, state.getWayCount());
    }
    
    @Test
    public void testNot() {
        loader.importMap(new File("test/resources/testmaps/not.osm"), new Progress());
        assertEquals(0, state.getNodeCount());
        assertEquals(0, state.getWayCount());
    }
    
    @Test
    public void testNoNodes() {
        loader.importMap(new File("test/resources/testmaps/no-nodes.osm"), new Progress());
        assertEquals(0, state.getNodeCount());
        assertEquals(0, state.getWayCount());
    }

}
