package kit.route.a.lot.map.rendering;

import kit.route.a.lot.common.Bounds;
import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.map.Node;

import org.junit.Test;
import static org.junit.Assert.*;


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
