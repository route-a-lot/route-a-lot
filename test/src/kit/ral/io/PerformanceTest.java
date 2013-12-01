
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

import kit.ral.common.Bounds;
import kit.ral.common.Coordinates;
import kit.ral.common.description.Address;
import kit.ral.common.description.WayInfo;
import kit.ral.map.info.MapInfo;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.BeforeClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static kit.ral.common.util.Util.printMemoryInformation;

public class PerformanceTest {
    
    MapInfo mapInfo;
    
    @BeforeClass
    public static void setUpClass() {
        PropertyConfigurator.configure("config/log4j.conf");
    }
    
    @Before
    public void setUp() {
        mapInfo = new MapInfo();
    }
    
//    @Test
    public void testMemoryPerformanceWithNodes() {
        printMemoryInformation();
        mapInfo.setBounds(new Bounds(new Coordinates(100, 10), new Coordinates(10, 100)));
        Random rnd = new Random();
        for (int i = 0; i < 33000000; i++) {
            mapInfo.addNode(new Coordinates(10 + rnd.nextFloat()*90, 10 + rnd.nextFloat()*90), i, new Address());
            if (i > 25500000 && i % 1000 == 0) { 
                System.gc();
                System.out.println("current node = " + i);
                printMemoryInformation();
            }
            if (i % 300000 == 0) {
                 System.out.println("current node = " + i);
                 printMemoryInformation();
            }
        }
         System.out.println("Fertig");
    }
    
//    @Test
    public void testMemoryPerformanceWithWays() {
        mapInfo.setBounds(new Bounds(new Coordinates(100, 10), new Coordinates(10, 100)));
        Random rnd = new Random();
        
        List<Integer> ids;
        WayInfo wayInfo;
        int type = 0;
        int nodeId = 0;

        for (int i = 0; i < 33000000; i++) {
            int size = 2 + rnd.nextInt(5);
            ids = new ArrayList<Integer>(size);
            float startLon = 10 + rnd.nextFloat()*80;
            float startLat = 10 + rnd.nextFloat()*80;
            for (int j = 0; j < size; j++) {
                mapInfo.addNode(new Coordinates(startLon, startLat), nodeId, null);
                startLon += rnd.nextFloat()*2;
                startLat += rnd.nextFloat()*2;
                ids.add(nodeId);
                nodeId++;
            }

            wayInfo = new WayInfo();
            switch (type) {
                case 0:
                    wayInfo.setStreet(true);
                    type = 1;
                    break;
                case 1:
                    wayInfo.setArea(true);
                    type = 0;
                    break;
            }
            mapInfo.addWay(ids, "", wayInfo);

            if (i > 33000 && i % 10 == 0) { 
                System.gc();
                // System.out.println("current way = " + i);
            }
            if (i % 1000 == 0) {
                // System.out.println("current way = " + i);
            }
        }
        // System.out.println("Fertig");
    }
    
    //@Test
    public void testStackOverflow() {
        mapInfo.setBounds(new Bounds(new Coordinates(100, 10), new Coordinates(10, 100)));
        try {
            for(int i = 0; i < 65; i++) {
                mapInfo.addNode(new Coordinates(40, 40), i, null);
            }
        } catch (Error e) {
            mapInfo.printQuadTree();
        }
    }
    
}