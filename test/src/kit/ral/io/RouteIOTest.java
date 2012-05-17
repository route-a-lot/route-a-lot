
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
import java.util.ArrayList;
import java.util.Random;

import kit.ral.common.Coordinates;
import kit.ral.common.Progress;
import kit.ral.common.Selection;
import kit.ral.controller.State;
import kit.ral.map.info.MapInfo;

import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class RouteIOTest extends RouteIO {
    OSMLoader loader;
    State state;

    @BeforeClass
    public static void setUpClass() {
        PropertyConfigurator.configure("config/log4j.conf");
    }

    @Before
    public void setUp() throws Exception {
        new State();
        state = State.getInstance();
        state.setMapInfo(new MapInfo());
        loader = new OSMLoader(state, new WeightCalculatorMock(state));
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void saveAndLoad() {
        loader.importMap(new File("test/resources/karlsruhe_small.osm"), new Progress());
        File file = new File("route.test");
        file.deleteOnExit();
        int size = 500000;
        Random randomGenerator = new Random();
        do {
            ArrayList<Selection> selections = new ArrayList<Selection>();
            selections.add(new Selection(new Coordinates(), randomGenerator.nextInt(size), randomGenerator.nextInt(size), randomGenerator.nextFloat(), ""));
            selections.add(new Selection(new Coordinates(), randomGenerator.nextInt(size), randomGenerator.nextInt(size), randomGenerator.nextFloat(), ""));
            state.setNavigationNodes(selections);
            state.setLoadedMapFile(file);   // Dummy
            assertEquals(state, State.getInstance());
            assertEquals(selections, state.getNavigationNodes());
            try {
                saveCurrentRoute(file);
            } catch (IOException e) {
                fail();
            }
            state.setNavigationNodes(null);
            try {
                loadCurrentRoute(file);
            } catch (IOException e) {
                fail();
            }
            assertEquals(selections.toString(), state.getNavigationNodes().toString());
            exportCurrentRouteToKML(file);
        } while (randomGenerator.nextInt(10000) > 100);
    }
}
