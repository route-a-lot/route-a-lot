package kit.route.a.lot.map;

import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import kit.route.a.lot.common.Bounds;
import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.OSMType;
import kit.route.a.lot.common.Projection;
import kit.route.a.lot.common.ProjectionFactory;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.common.WayInfo;
import kit.route.a.lot.controller.State;
import kit.route.a.lot.map.infosupply.MapInfo;

public class Street extends MapElement implements Comparable<Street> {

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
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Street)) {
            return false;
        }
        Street comparee = (Street) other;
        return nodes.equals(comparee.nodes) && name == comparee.name && wayInfo.equals(comparee.wayInfo);
    }

    @Override
    public String getName() {
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
    public boolean isInBounds(Bounds bounds) {
        boolean inBounds = false;
        Bounds extendedBounds = bounds.clone().extend(getDrawingSize() + 2);
        for (int i = 1; i < nodes.length && !inBounds; i++) {
            inBounds = isEdgeInBounds(nodes[i - 1].getPos(), nodes[i].getPos(), extendedBounds);
        }
        return inBounds;
    }

    public static boolean isEdgeInBounds(Coordinates node1, Coordinates node2, Bounds bounds) {
        Line2D.Float edge = new Line2D.Float(node1.getLongitude(), node1.getLatitude(),
                                             node2.getLongitude(), node2.getLatitude());
        // coord.sys. begins in upper left corner
        Rectangle2D.Float box = new Rectangle2D.Float(
                bounds.getLeft(), bounds.getTop(),
                bounds.getWidth(), bounds.getHeight());
        return box.contains(node1.getLongitude(), node1.getLatitude())
                || box.contains(node2.getLongitude(), node2.getLatitude())
                || box.intersectsLine(edge);
        // TODO pos -> neg (e.g. -180° -> 180°)
    }


    public Selection getSelection(Coordinates pos) {
        int start = getClosestEdgeStartPosition(pos);
        Coordinates geoPos = ProjectionFactory.getCurrentProjection().getGeoCoordinates(pos);
        return new Selection(pos, nodes[start].getID(), nodes[start + 1].getID(), getRatio(start, start + 1,
                pos), (name != null) ? name : geoPos.toString());
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
        double b = Coordinates.getDistance(nodes[start].getPos(), pos);
        double a = Coordinates.getDistance(nodes[end].getPos(), pos);
        double c = Coordinates.getDistance(nodes[start].getPos(), nodes[end].getPos());
        if (c == 0) {
            return (float) Coordinates.getDistance(nodes[start].getPos(), pos);
        }
        float distance = (float) getTriangleCHeight(a, b, c);
        if (!(distance < 0)) {
            return distance;
        }
        return (float) Math.min(Coordinates.getDistance(nodes[start].getPos(), pos),
                Coordinates.getDistance(nodes[end].getPos(), pos));
    }

    // a= side between end and pos, b = s. between pos and start, c = s. between start and end
    private static double getTriangleCHeight(double a, double b, double c) {
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

    /**
     * returns the distance between the given node and coordinate in meter
     * 
     * params are projected coordinates on the current map
     */
    public static double getDistanceInMeter(Coordinates pos1, Coordinates pos2) {
        Projection projection = ProjectionFactory.getCurrentProjection();
        Coordinates geoPos1 = projection.getGeoCoordinates(pos1);
        Coordinates geoPos2 = projection.getGeoCoordinates(pos2);
        double pos1LongRad = Math.toRadians(Math.abs(geoPos1.getLongitude())); // coordinates in deg
        double pos1LalRad = Math.toRadians(Math.abs(geoPos1.getLatitude()));
        double pos2LongRad = Math.toRadians(Math.abs(geoPos2.getLongitude()));
        double pos2LalRad = Math.toRadians(Math.abs(geoPos2.getLatitude()));

        double distanceRad =
                Math.acos(Math.sin(pos1LalRad) * Math.sin(pos2LalRad) + Math.cos(pos1LalRad)
                        * Math.cos(pos2LalRad) * Math.cos(pos1LongRad - pos2LongRad)); // distance in deg

        return distanceRad * 6371001; // 6371001 is the mean earth radius in meter
    }

    private float getRatio(int startNode, int endNode, Coordinates pos) {
        double b = Coordinates.getDistance(nodes[startNode].getPos(), pos);
        double a = Coordinates.getDistance(nodes[endNode].getPos(), pos);
        double c = Coordinates.getDistance(nodes[startNode].getPos(), nodes[endNode].getPos());
        if (b == 0) {
            return 0.f;
        }
        if (a == 0) {
            return 1.f;
        }
        // float h = (float)getTriangleCHeight(a, b, c);
        double angleBC = Math.acos((b * b + c * c - a * a) / (2 * b * c));
        double angleAC = Math.acos((a * a + c * c - b * b) / (2 * a * c));
        if (angleBC > Math.PI / 2 || angleAC > Math.PI / 2) {
            if (Coordinates.getDistance(nodes[startNode].getPos(), pos) < Coordinates.getDistance(
                    nodes[endNode].getPos(), pos)) {
                return 0.0f;
            } else {
                return 1.0f;
            }
        }
        double fromStartToLot = Math.cos(angleBC) * b; // cos(AngleAB) * "hypothenuse"
        return (float) (fromStartToLot / c);
    }

    @Override
    protected void load(DataInput input) throws IOException {
        setName(input.readUTF());
        int len = input.readInt();
        nodes = new Node[len];
        MapInfo mapInfo = State.getInstance().getMapInfo();
        for (int i = 0; i < len; i++) {
            nodes[i] = mapInfo.getNode(input.readInt());
        }
        this.wayInfo = WayInfo.loadFromInput(input);
    }

    @Override
    protected void save(DataOutput output) throws IOException {
        output.writeUTF(getName());
        output.writeInt(this.nodes.length);
        for (Node node : this.nodes) {
            output.writeInt(node.getID());
        }
        this.wayInfo.saveToOutput(output);
    }

    @Override
    public Selection getSelection() {
        if (nodes.length < 2) {
            return null;
        }
        /*int start = nodes[nodes.length / 2 - 1].getID();
        int end = nodes[nodes.length / 2].getID();
        return new Selection(Coordinates.interpolate(nodes[nodes.length / 2].getPos(),
                   nodes[nodes.length / 2 + 1].getPos(), 0.5f), start, end, 0.5f, (name != null) ? name : 
                       Coordinates.interpolate(nodes[nodes.length / 2].getPos(),
                               nodes[nodes.length / 2 + 1].getPos(), 0.5f).toString());*/
        Selection sel = State.getInstance().getMapInfo().select(nodes[nodes.length / 2 - 1].getPos());
        sel.setName(name);
        return sel;
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
        if (nodes.length <= 1) {
            return nodes;
        }
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
            length += Coordinates.getDistance(street.nodes[i - 1].getPos(), street.nodes[i].getPos());
        }
        return length;
    }

    public boolean equals(Street other) {

        if (name.equals(other.getName())) {
            return true;
        }
        return false;
    }

    public int compareTo(Street other) {
        int value;
        int otherValue;
        String otherName = other.getName();
        int minlength = otherName.length();
        if (name.length() < minlength) {
            minlength = name.length();
        }

        for (int i = 0; i < minlength; i++) {
            value = Character.getNumericValue(name.charAt(i));
            if (value < 0 || value > 25) {
                value = 25;
            }
            otherValue = Character.getNumericValue(otherName.charAt(i));
            if (value < 0 || otherValue > 25) {
                otherValue = 25;
            }
            if (value > otherValue) {
                return value - otherValue;
            } else if (value < otherValue) {
                return value - otherValue;
            }
        }

        /*
         * wenn Präfix gleich aber dieser String kürzer steht er lexikographisch weiter vorne
         */
        return name.length() - otherName.length();
    }

    public static int getStreetDrawingSize(WayInfo wayInfo) {
        int basicSize = 10;
        switch (wayInfo.getType()) {
            case OSMType.HIGHWAY_MOTORWAY:
            case OSMType.HIGHWAY_MOTORWAY_JUNCTION:
            case OSMType.HIGHWAY_MOTORWAY_LINK:
                basicSize = 40;
                break;
            case OSMType.HIGHWAY_PRIMARY:
            case OSMType.HIGHWAY_PRIMARY_LINK:
                basicSize = 30;
                break;
            case OSMType.HIGHWAY_SECONDARY:
            case OSMType.HIGHWAY_SECONDARY_LINK:
                basicSize = 22;
                break;
            case OSMType.HIGHWAY_TERTIARY:
            case OSMType.HIGHWAY_TERTIARY_LINK:
                basicSize = 20;
                break;
            case OSMType.HIGHWAY_RESIDENTIAL:
            case OSMType.HIGHWAY_LIVING_STREET:
            case OSMType.HIGHWAY_UNCLASSIFIED:
                basicSize = 18;
                break;
            case OSMType.HIGHWAY_CYCLEWAY:
                basicSize = 15;
                break;
        }
        return basicSize;
    }

    public int getDrawingSize() {
        return getStreetDrawingSize(wayInfo);
    }

}
