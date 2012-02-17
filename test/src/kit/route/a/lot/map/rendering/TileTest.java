package kit.route.a.lot.map.rendering;

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
        assertArrayEquals(allNodes, bigTile.getRelevantNodesForStreet(allNodes));
        assertArrayEquals(expectedNodesMedium, mediumTile.getRelevantNodesForStreet(allNodes));
        assertArrayEquals(expectedNodesSmall, smallTile.getRelevantNodesForStreet(allNodes));
        assertArrayEquals(expectedNodesOther, otherTile.getRelevantNodesForStreet(allNodes));
    }

}
