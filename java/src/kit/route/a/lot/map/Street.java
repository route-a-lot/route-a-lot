package kit.route.a.lot.map;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kit.route.a.lot.common.Coordinates;
import static kit.route.a.lot.common.Util.*;
import static kit.route.a.lot.map.Util.*;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.common.WayInfo;
import kit.route.a.lot.controller.State;
import kit.route.a.lot.map.infosupply.MapInfo;


public class Street extends MapElement {

    private Node[] nodes;

    private String name;

    private WayInfo wayInfo;

    public Street(String name, WayInfo wayInfo) {
        this.nodes = null;
        this.name = name;
        this.wayInfo = wayInfo;
    }

    public Street() {
        this(null, null);
    }

    @Override
    protected String getName() {
        return name;
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
            // TODO this is not very performant, as a new Coordinates object is created each time
            if (isEdgeInBounds(nodes[i - 1].getPos(), nodes[i].getPos(),
                    topLeft, bottomRight)) {
                inBounds = true;
            }
            i++;
        }
        return inBounds;
    }
    
    private boolean isEdgeInBounds(Coordinates node1, Coordinates node2,
                                   Coordinates topLeft, Coordinates bottomRight) {
        Line2D.Float edge = new Line2D.Float(node1.getLongitude(), node1.getLatitude(),
                                             node2.getLongitude(), node2.getLatitude());
        //coord.sys. begins in upper left corner 
        Rectangle2D.Float box = new Rectangle2D.Float(Math.min(topLeft.getLongitude(), bottomRight.getLongitude()),
                                                      Math.min(topLeft.getLatitude(), bottomRight.getLatitude()),    
                                                      Math.abs(bottomRight.getLongitude() - topLeft.getLongitude()),
                                                      Math.abs(topLeft.getLatitude() - bottomRight.getLatitude()));
        return box.contains(node1.getLongitude(), node1.getLatitude())
            || box.contains(node2.getLongitude(), node2.getLatitude())
            || box.intersectsLine(edge);
        //TODO pos -> neg (e.g. -180° -> 180°)
    }

    
    public Selection getSelection(Coordinates pos) {
        int start = getClosestEdgeStartPosition(pos);
        return new Selection(nodes[start].getID(),
                nodes[start + 1].getID(),
                getRatio(start, start + 1, pos),
                pos);
    }

    
    public float getDistanceTo(Coordinates pos) {
        int startNode = getClosestEdgeStartPosition(pos);   
        return getDistanceFromNodeToEdge(startNode, startNode + 1, pos);
    }
    
    /*
     * returns the position in the array nodes of the startNode of the edge of this street, which is the closest to the given Coordinate
     */
    private int getClosestEdgeStartPosition(Coordinates pos) {
        float nearestEdgeDistance = Float.MAX_VALUE;
        int nearestEdgeStartNode = 0;
        for(int i = 0; i < nodes.length - 1; i++) {
            float currentDistance = getDistanceFromNodeToEdge(i, i + 1, pos);
            if (currentDistance < nearestEdgeDistance) {
                nearestEdgeDistance = currentDistance;
                nearestEdgeStartNode = i;
            }
        }
        return nearestEdgeStartNode;
    }
    
    /*
     * returns the distance from the given coordinate to the given edge
     * edge is given by the position of the start - and endNode in the array nodes 
     */
    private float getDistanceFromNodeToEdge(int start, int end, Coordinates pos) {
       double b = getDistance(nodes[start].getPos(), pos);
       double a = getDistance(nodes[end].getPos(), pos);
       double c = getDistance(nodes[start].getPos(), nodes[end].getPos());
       float distance = (float) getTriangleCHeight(a, b, c);
       if (!(distance < 0)) {
           return distance;
       }
       return (float) Math.min(getDistance(nodes[start].getPos(), pos), getDistance(nodes[end].getPos(), pos));
    }
    
    // a= side between end and pos, b = s. between pos and start, c = s. between start and end
    private double getTriangleCHeight(double a, double b, double c) {
        double angleBC= Math.acos((b*b + c*c - a*a) / (2 * b * c));
        double angleAB = Math.acos((a*a + c*c - b*b) / (2 * a * c));
        if (angleBC > Math.PI / 2 || angleAB > Math.PI / 2) {  //
            /*
             * Not everything is in lot. No, seriously the min distance isn't between pos and a point ON the line
             */
            return - 1;   // if failure --> wrong angles, try: if(angleAB > Math.PI / 4 || angleAB < Math.PI / 8)
        }
        return Math.sin(angleBC) * b;   //height of triangle  (a = "hypothenuse" and h = "gegenkathete")
    }
    
    /*
     * returns the distance between the given node and coordinate in meter
     */
    private static double getDistance(Coordinates pos1, Coordinates pos2) {
        double pos1LongRad = Math.abs(pos1.getLongitude()) / 180 * Math.PI;    //coordinates in deg
        double pos1LalRad = Math.abs(pos1.getLatitude()) / 180 * Math.PI;
        double pos2LongRad = Math.abs(pos2.getLongitude()) / 180 * Math.PI;
        double pos2LalRad = Math.abs(pos2.getLatitude()) / 180 * Math.PI;
        
        double distanceRad = Math.acos(Math.sin(pos1LalRad) * Math.sin(pos2LalRad)
                                        + Math.cos(pos1LalRad) * Math.cos(pos2LalRad) * Math.cos(pos1LongRad - pos2LongRad)); //distance in deg 
        return distanceRad * 6378137;   //6378137 is the "äquatorradius" in meter
        
    }
    
    private float getRatio(int startNode, int endNode, Coordinates pos) {
        double b = getDistance(nodes[startNode].getPos(), pos);
        double a = getDistance(nodes[endNode].getPos(), pos);
        double c = getDistance(nodes[startNode].getPos(), nodes[endNode].getPos());
        float h = (float)getTriangleCHeight(a, b, c);
        double angleBC = Math.acos((b*b + c*c - a*a) / (2 * b * c)); 
        double angleAB = Math.acos((a*a + c*c - b*b) / (2 * a * c));
       if (angleBC > Math.PI / 4 || angleAB > Math.PI / 2 ) {
            if (getDistance(nodes[startNode].getPos(), pos) < getDistance(nodes[endNode].getPos(), pos)) {
                return 0.0f;
            } else {
                return 1.0f;
            }
        }
        double fromStartToLot = Math.cos(angleBC) * b; //cos(AngleAB) * "hypothenuse"
        return (float) (fromStartToLot / c);   
    }
    
    @Override
    protected void load(DataInputStream stream) throws IOException {
        this.name = stream.readUTF();
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
        stream.writeUTF(this.name);
        stream.writeInt(this.nodes.length);
        for (Node node: this.nodes) {
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
        result.setNodes(simplify(nodes, range));
        return result;
    }
}
