package kit.route.a.lot.map;

import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.common.WayInfo;
import kit.route.a.lot.controller.State;
import kit.route.a.lot.map.infosupply.MapInfo;
import kit.route.a.lot.map.rendering.Projection;


public class Street extends MapElement {

    private Node[] nodes;

    private String name;

    private WayInfo wayInfo;

    private static final String EMPTY = "";

    public Street(String name, WayInfo wayInfo) {
        this.nodes = null;
        setName(name);
        this.wayInfo = wayInfo;
    }

    public Street() {
        this(null, null);
    }

    @Override
    protected String getName() {
        return (this.name != null) ? this.name : "";
    }

    private void setName(String name) {
        this.name = EMPTY.equals(name) ? null : name;
    }

    public Node[] getNodes() {
        return nodes;
    }

    public WayInfo getWayInfo() {
        return wayInfo;
    }

    public void setNodes(Node[] nodes) {
        this.nodes = nodes;
    }

    @Override
    public boolean isInBounds(Coordinates topLeft, Coordinates bottomRight) {
        boolean inBounds = false;
        int i = 1;
        while (i < nodes.length && inBounds == false) {
            if (isEdgeInBounds(nodes[i - 1].getPos(), nodes[i].getPos(), topLeft, bottomRight)) {
                inBounds = true;
            }
            i++;
        }
        return inBounds;
    }

    private boolean isEdgeInBounds(Coordinates node1, Coordinates node2, Coordinates topLeft,
            Coordinates bottomRight) {
        Line2D.Float edge =
                new Line2D.Float(node1.getLongitude(), node1.getLatitude(), node2.getLongitude(),
                        node2.getLatitude());
        // coord.sys. begins in upper left corner
        Rectangle2D.Float box =
                new Rectangle2D.Float(Math.min(topLeft.getLongitude(), bottomRight.getLongitude()), Math.min(
                        topLeft.getLatitude(), bottomRight.getLatitude()), Math.abs(bottomRight
                        .getLongitude() - topLeft.getLongitude()), Math.abs(topLeft.getLatitude()
                        - bottomRight.getLatitude()));
        return box.contains(node1.getLongitude(), node1.getLatitude())
                || box.contains(node2.getLongitude(), node2.getLatitude()) || box.intersectsLine(edge);
        // TODO pos -> neg (e.g. -180° -> 180°)
    }


    public Selection getSelection(Coordinates pos) {
        int start = getClosestEdgeStartPosition(pos);
        return new Selection(nodes[start].getID(), nodes[start + 1].getID(), getRatio(start, start + 1, pos),
                pos);
    }


    public float getDistanceTo(Coordinates pos) {
        int startNode = getClosestEdgeStartPosition(pos);
        return getDistanceFromNodeToEdge(nodes, startNode, startNode + 1, pos);
    }

    /*
     * returns the position in the array nodes of the startNode of the edge of this street, which is the
     * closest to the given Coordinate
     */
    private int getClosestEdgeStartPosition(Coordinates pos) {
        float nearestEdgeDistance = Float.MAX_VALUE;
        int nearestEdgeStartNode = 0;
        for (int i = 0; i < nodes.length - 1; i++) {
            float currentDistance = getDistanceFromNodeToEdge(nodes, i, i + 1, pos);
            if (currentDistance < nearestEdgeDistance) {
                nearestEdgeDistance = currentDistance;
                nearestEdgeStartNode = i;
            }
        }
        return nearestEdgeStartNode;
    }

    /*
     * returns the distance from the given coordinate to the given edge edge is given by the position of the
     * start - and endNode in the array nodes
     */
    private static float getDistanceFromNodeToEdge(Node[] nodes, int start, int end, Coordinates pos) {
        double b = getDistanceProj(nodes[start].getPos(), pos);
        double a = getDistanceProj(nodes[end].getPos(), pos);
        double c = getDistanceProj(nodes[start].getPos(), nodes[end].getPos());
        if (c == 0) {
            return (float) Street.getDistanceProj(nodes[start].getPos(), pos);
        }
        float distance = (float) getTriangleCHeight(a, b, c);
        if (!(distance < 0)) {
            return distance;
        }
        return (float) Math.min(getDistanceProj(nodes[start].getPos(), pos),
                getDistanceProj(nodes[end].getPos(), pos));
    }

    // a= side between end and pos, b = s. between pos and start, c = s. between start and end
    public static double getTriangleCHeight(double a, double b, double c) {
        double angleBC = Math.acos((b * b + c * c - a * a) / (2 * b * c));
        double angleAB = Math.acos((a * a + c * c - b * b) / (2 * a * c));
        if (angleBC > Math.PI / 2 || angleAB > Math.PI / 2) { //
            /*
             * Not everything is in lot. No, seriously the min distance isn't between pos and a point ON the
             * line
             */
            return -1; // if failure --> wrong angles, try: if(angleAB > Math.PI / 4 || angleAB < Math.PI / 8)
        }
        return Math.sin(angleBC) * b; // height of triangle (a = "hypothenuse" and h = "gegenkathete")
    }

    public static double getDistanceProj(Coordinates pos1, Coordinates pos2) {
        return Math.sqrt(Math.pow(pos1.getLatitude() - pos2.getLatitude(), 2)
                + Math.pow(pos1.getLongitude() - pos2.getLongitude(), 2));
    }

    /**
     * returns the distance between the given node and coordinate in meter
     * 
     * params are projected coordinates on the current map
     */
    public static double getDistanceInMeter(Coordinates pos1, Coordinates pos2) {
        Projection projection = Projection.getProjectionForCurrentMap();
        Coordinates geoPos1 = projection.localCoordinatesToGeoCoordinates(pos1);
        Coordinates geoPos2 = projection.localCoordinatesToGeoCoordinates(pos2);
        double pos1LongRad = Math.abs(geoPos1.getLongitude()) / 180 * Math.PI; // coordinates in deg
        double pos1LalRad = Math.abs(geoPos1.getLatitude()) / 180 * Math.PI;
        double pos2LongRad = Math.abs(geoPos2.getLongitude()) / 180 * Math.PI;
        double pos2LalRad = Math.abs(geoPos2.getLatitude()) / 180 * Math.PI;
        
        double distanceRad =
                Math.acos(Math.sin(pos1LalRad) * Math.sin(pos2LalRad) + Math.cos(pos1LalRad)
                        * Math.cos(pos2LalRad) * Math.cos(pos1LongRad - pos2LongRad)); // distance in deg
        
        return distanceRad * 6371001; // 6371001 is the mean earth radius in meter

    }

    private float getRatio(int startNode, int endNode, Coordinates pos) {
        double b = getDistanceProj(nodes[startNode].getPos(), pos);
        double a = getDistanceProj(nodes[endNode].getPos(), pos);
        double c = getDistanceProj(nodes[startNode].getPos(), nodes[endNode].getPos());
        // float h = (float)getTriangleCHeight(a, b, c);
        double angleBC = Math.acos((b * b + c * c - a * a) / (2 * b * c));
        double angleAC = Math.acos((a * a + c * c - b * b) / (2 * a * c));
        if (angleBC > Math.PI / 2 || angleAC > Math.PI / 2) {
            if (getDistanceProj(nodes[startNode].getPos(), pos) < getDistanceProj(nodes[endNode].getPos(),
                    pos)) {
                return 0.0f;
            } else {
                return 1.0f;
            }
        }
        double fromStartToLot = Math.cos(angleBC) * b; // cos(AngleAB) * "hypothenuse"
        return (float) (fromStartToLot / c);
    }

    @Override
    protected void load(DataInputStream stream) throws IOException {
        setName(stream.readUTF());
        int len = stream.readInt();
        nodes = new Node[len];
        MapInfo mapInfo = State.getInstance().getLoadedMapInfo();
        for (int i = 0; i < len; i++) {
            nodes[i] = mapInfo.getNode(stream.readInt());
        }
        this.wayInfo = WayInfo.loadFromStream(stream);
    }

    @Override
    protected void save(DataOutputStream stream) throws IOException {
        stream.writeUTF(getName());
        stream.writeInt(this.nodes.length);
        for (Node node : this.nodes) {
            stream.writeInt(node.getID());
        }
        this.wayInfo.saveToStream(stream);
    }

    @Override
    public Selection getSelection() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MapElement getReduced(int detail, float range) {
        Street result = new Street(name, wayInfo);
        result.setNodes(simplifyNodes(nodes, range));
        if (getLengthOfStreet(result) < range) {
            return null;
        } else if (result.nodes.length == nodes.length) {
            return this;
        } else {
            return result;
        }
    }
    
    public static Node[] simplifyNodes(Node[] nodes, float range) {
        List<Node> newNodes = new ArrayList<Node>();
        newNodes.add(nodes[0]);
        adjustTube(nodes, newNodes, 0, nodes.length - 1, range);
        newNodes.add(nodes[nodes.length - 1]);
        return newNodes.toArray(new Node[newNodes.size()]);
    }

    private static void adjustTube(Node[] nodes, Collection<Node> outNodes, int start, int end, float range) {
        float maxDistance = Float.MIN_VALUE;
        int maxDistIndex = 0;
        for (int i = start + 1; i < end; i++) {
            float curDistance = getDistanceFromNodeToEdge(nodes, start, end, nodes[i].getPos());
            if (curDistance > maxDistance) {
                maxDistance = curDistance;
                maxDistIndex = i;
            }
        }
        if (maxDistance > range) {
            adjustTube(nodes, outNodes, start, maxDistIndex, range);
            outNodes.add(nodes[maxDistIndex]);
            adjustTube(nodes, outNodes, maxDistIndex, end, range);
        }
    }

    private static float getLengthOfStreet(Street street) {
        float length = 0;
        for (int i = 1; i < street.nodes.length; i++) {
            length += getDistanceProj(street.nodes[i - 1].getPos(), street.nodes[i].getPos());
        }
        return length;
    }
    
}
