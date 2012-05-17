
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

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.*;
import org.junit.Test;

import kit.ral.common.Progress;
import kit.ral.controller.State;
import kit.ral.map.info.MapInfo;


public class MapIOTest {

   static Logger logger = Logger.getLogger(MapIOTest.class);
   private File sralMap;
   private Progress p;    
    
    @BeforeClass
    public static void setUpClass() {
        PropertyConfigurator.configure("config/log4j.conf");
    }
   
    @Before
    public void setUp() throws Exception {
       sralMap = new File("test/resources/karlsruhe_small_current.sral");
       p = new Progress();
    }
     
    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void SaveAndLoad() {
        File karlsruheMap = new File("test/resources/karlsruhe_small_current.osm");
        OSMLoader osmLoader = new OSMLoader(State.getInstance(),
                new WeightCalculatorMock(State.getInstance()));
        osmLoader.importMap(karlsruheMap, p.createSubProgress(0.6));
        State state = State.getInstance();

        // Controller.setViewToMapCenter() externalized:
        state.setCenterCoordinates(state.getMapInfo().getBounds().getCenter());
        try {
            MapIO.saveMap(sralMap, p.createSubProgress(0.3));
        } catch (IOException e) {
            fail();
        }
        //MapInfo original = state.getMapInfo();
        state.setMapInfo(new MapInfo());
        try {
            MapIO.loadMap(sralMap, p.createSubProgress(1));
        } catch (IOException e) {
            fail();
        }
        //assertTrue(original.equals(state.getMapInfo()));
    }
}
