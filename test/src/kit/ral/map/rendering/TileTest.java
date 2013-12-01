
/**
Copyright (c) 2012, Matthias Grundmann, Josua Stabenow
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

import kit.ral.common.Bounds;
import kit.ral.common.Coordinates;
import kit.ral.map.Node;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;


public class TileTest {
    
    @Test
    public void testGetRelevantNodesForStreet() {
        Tile bigTile = new Tile(new Coordinates(0.f, 0.f), 400, 0);
        Tile mediumTile = new Tile(new Coordinates(100.f, 100.f), 100, 0);
        Tile smallTile = new Tile(new Coordinates(0.f, 0.f), 100, 0);
        Tile otherTile = new Tile(new Coordinates(500.f, 500.f), 10, 0);
        Node[] allNodes = new Node[5];
        allNodes[0] = new Node(new Coordinates(50, 50));
        allNodes[1] = new Node(new Coordinates(150, 50));
        allNodes[2] = new Node(new Coordinates(150, 150));
        allNodes[3] = new Node(new Coordinates(150, 300));
        allNodes[4] = new Node(new Coordinates(300, 300));
        Node[] expectedNodesMedium = new Node[3];
        expectedNodesMedium[0] = new Node(new Coordinates(150, 50));
        expectedNodesMedium[1] = new Node(new Coordinates(150, 150));
        expectedNodesMedium[2] = new Node(new Coordinates(150, 300));
        Node[] expectedNodesSmall = new Node[2];
        expectedNodesSmall[0] = new Node(new Coordinates(50, 50));
        expectedNodesSmall[1] = new Node(new Coordinates(150, 50));
        Node[] expectedNodesOther = new Node[0];
        assertArrayEquals(allNodes, bigTile.getRelevantNodesForStreet(allNodes, 0));
        assertArrayEquals(expectedNodesMedium, mediumTile.getRelevantNodesForStreet(allNodes, 0));
        assertArrayEquals(expectedNodesSmall, smallTile.getRelevantNodesForStreet(allNodes, 0));
        assertArrayEquals(expectedNodesOther, otherTile.getRelevantNodesForStreet(allNodes, 0));
    }
    
    @Test
    public void testPrerenderPerformance() {
        MapInfoMock mapInfoMock = new MapInfoMock();
        Tile myTile = new Tile(new Coordinates(0, 0), 100, 0, mapInfoMock);
        long start;
        long duration;
        for (int i = 0; i < 10; i++) {       // warm up (because of cache effects)
            mapInfoMock = new MapInfoQTMock(new Bounds(0, 140, -20, 130));
            myTile = new Tile(new Coordinates(0, 0), 100, 0, mapInfoMock);
            fillMapInfoMock(mapInfoMock, 15, new Bounds(0, 140, -20, 130));
            myTile.prerender();
        }
        System.out.println("Elemente\tBenötigte Zeit [ms]");
        for (int count = 1; count < 1000000; count *= Math.sqrt(10)) {
//            mapInfoMock = new MapInfoMock();
            mapInfoMock = new MapInfoQTMock(new Bounds(0, 140, -20, 130));
            myTile = new Tile(new Coordinates(0, 0), 100, 0, mapInfoMock);
            fillMapInfoMock(mapInfoMock, count, new Bounds(0, 140, -20, 130));
            start = System.nanoTime();
            myTile.prerender();
            duration = System.nanoTime() - start;
//            System.out.println("Prerendering " + count + " elements took " + Util.formatNanoSeconds(duration));
            System.out.println(count + "\t" + ((double) duration / 1000000));
        }
    }
    
    private void fillMapInfoMock(MapInfoMock mapInfoMock, int nElements, Bounds bounds) {
        MapElementGenerator generator = new MapElementGenerator();
        for (int i = 0; i < nElements; i++) {
            if (Math.random() < 0.73) {
                mapInfoMock.addMapElement(generator.generateStreetInBounds(bounds));
            } else {
                mapInfoMock.addMapElement(generator.generateBuildingInBounds(bounds));
            }
        }
        mapInfoMock.lastElementAdded();
    }

}
