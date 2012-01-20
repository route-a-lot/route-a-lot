package kit.route.a.lot.map;


import java.util.ArrayList;
import java.util.List;

import kit.route.a.lot.common.Coordinates;


public class Util {

    public static Node[] simplify(Node[] nodes, float range) {
        Node current = nodes[0];
        List<Node> newNodes = new ArrayList<Node>();
        newNodes.add(current);
        Node last = nodes[nodes.length - 1];
        for (Node node: nodes) {
            if (!isInTube(current, last, node, range)) {
                newNodes.add(node);
                current = node;
            }
        }
        newNodes.add(last);
        Node[] resultArray = new Node[newNodes.size()];
        return newNodes.toArray(resultArray);
    }

    private static boolean isInTube(Node current, Node last, Node node, float range) {
        if (Math.pow(current.getPos().getLatitude() - node.getPos().getLatitude(), 2)
                + Math.pow(current.getPos().getLongitude() - node.getPos().getLongitude(), 2)
                < range * range) {
            return true;
        }
        if (Math.pow(last.getPos().getLatitude() - node.getPos().getLatitude(), 2)
                + Math.pow(last.getPos().getLongitude() - node.getPos().getLongitude(), 2)
                < range * range) {
            return true;
        }
        Coordinates topLeft, topRight, bottomLeft, bottomRight;
        Coordinates vector = new Coordinates(current.getPos().getLongitude() - last.getPos().getLongitude(),
                current.getPos().getLatitude() - last.getPos().getLatitude());
        float length = (float) Math.sqrt(Math.pow(vector.getLatitude(), 2) + Math.pow(vector.getLongitude(), 2));
        vector.setLatitude(vector.getLatitude() * range / length );
        vector.setLongitude(vector.getLongitude() * range /length);
        topLeft = new Coordinates(
                current.getPos().getLatitude() + vector.getLongitude(),
                current.getPos().getLongitude() - vector.getLatitude());
        bottomLeft = new Coordinates(
                current.getPos().getLatitude() - vector.getLongitude(),
                current.getPos().getLongitude() + vector.getLatitude());
        topRight = new Coordinates(
                last.getPos().getLatitude() + vector.getLongitude(),
                last.getPos().getLongitude() - vector.getLatitude());
        bottomRight = new Coordinates(
                last.getPos().getLatitude() - vector.getLongitude(),
                last.getPos().getLongitude() + vector.getLatitude());
        return (((isAbove(topLeft, topRight, node) && isBelow(bottomLeft, bottomRight, node)) ||
                    (isBelow(topLeft, topRight, node) && isAbove(bottomLeft, bottomRight, node))) &&
                ((isLeft(topLeft, bottomLeft, node) && isRight(topRight, bottomRight, node)) ||
                    (isRight(topLeft, bottomLeft, node) && isLeft(topRight, bottomRight, node))));
        
    }

    private static boolean isLeft(Coordinates from, Coordinates to, Node node) {
        if (from.getLatitude() == to.getLatitude()) {
            return false;
        }
        return node.getPos().getLatitude() * from.getLatitude() / from.getLongitude() < node.getPos().getLongitude();
    }

    private static boolean isRight(Coordinates from, Coordinates to, Node node) {
        if (from.getLatitude() == to.getLatitude()) {
            return false;
        }
        return node.getPos().getLatitude() * from.getLatitude() / from.getLongitude() > node.getPos().getLongitude();
    }

    private static boolean isBelow(Coordinates from, Coordinates to, Node node) {
        if (from.getLongitude() == to.getLongitude()) {
            return false;
        }
        return node.getPos().getLongitude() * from.getLongitude() / from.getLatitude() > node.getPos().getLatitude();
    }

    private static boolean isAbove(Coordinates from, Coordinates to, Node node) {
        if (from.getLongitude() == to.getLongitude()) {
            return false;
        }
        return node.getPos().getLongitude() * from.getLongitude() / from.getLatitude() < node.getPos().getLatitude();
    }
    
}
