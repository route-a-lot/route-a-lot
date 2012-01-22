package kit.route.a.lot.map;

import java.awt.Polygon;
import java.awt.geom.Rectangle2D;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.common.WayInfo;
import kit.route.a.lot.controller.State;
import kit.route.a.lot.map.infosupply.MapInfo;
import static kit.route.a.lot.map.Util.*;


public class Area extends MapElement {

    private Node[] nodes;

    private String name;

    private WayInfo wayInfo;
    
    private static final String EMPTY = "";

    public Area(String name, WayInfo wayInfo) {
        this.name = name;
        this.nodes = new Node[0];
        this.wayInfo = wayInfo;
    }

    public Area() {
        this(null, null);
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
    protected String getName() {
        return (this.name != null) ? this.name : "";
    }

    @Override
    public boolean isInBounds(Coordinates topLeft, Coordinates bottomRight) {
        // TODO there is no float polygon, so I have to think about s.th. else (or leave it the way it is now)
        int x[] = new int[nodes.length];
        int y[] = new int[nodes.length];
        int i = 0;
        for (Node node : nodes) {
            x[i] = (int) (node.getPos().getLongitude() * 1000); // 100000000 is a random factor, can be changed
            i++;
        }
        i = 0;
        for (Node node : nodes) {
            y[i] = (int) (node.getPos().getLatitude() * 1000);
            i++;
        }
        Polygon area = new Polygon(x, y, nodes.length);
        Rectangle2D.Double box =
                new Rectangle2D.Double(Math.min(topLeft.getLongitude(), bottomRight.getLongitude()) * 1000 - 1, 
                        Math.min(topLeft.getLatitude(), bottomRight.getLatitude()) * 1000 - 1,
                        (Math.abs(bottomRight.getLongitude() - topLeft.getLongitude())) * 1000 + 1,
                        (Math.abs(topLeft.getLatitude() - bottomRight.getLatitude())) * 1000 + 1);
        boolean inside = false;
        for (Node node : nodes) {
            if (node.isInBounds(topLeft, bottomRight)) {
                inside = true;
            }
        }
        return inside || area.contains(box) || area.intersects(box);

    }
    
    @Override
    public Selection getSelection() {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override //TODO: attribs, load() and save() are identical to methods of same name in Street
    protected void load(DataInputStream stream) throws IOException {
        String name = stream.readUTF();
        this.name = EMPTY.equals(name) ? null : name;
        int len = stream.readInt();
        this.nodes = new Node[len];
        // TODO there must be some way without using state
        MapInfo mapInfo = State.getInstance().getLoadedMapInfo();
        for (int i = 0; i < len; i++) {        
            this.nodes[i] = mapInfo.getNode(stream.readInt());
        }
        this.wayInfo = WayInfo.loadFromStream(stream);
    }

    @Override
    protected void save(DataOutputStream stream) throws IOException {
        stream.writeUTF(getName());
        stream.writeInt(this.nodes.length);
        for (Node node: this.nodes) {
            stream.writeInt(node.getID());
        }
        this.wayInfo.saveToStream(stream);
    }

    @Override
    public MapElement getReduced(int detail, float range) {
        Coordinates topRight = new Coordinates(nodes[0].getPos().getLatitude(), nodes[0].getPos().getLongitude());
        Coordinates bottomLeft = topRight;
        Coordinates position;
        for (Node node: nodes) {
            position = node.getPos();
            topRight.setLatitude(Math.max(topRight.getLatitude(), position.getLatitude()));
            topRight.setLongitude(Math.max(topRight.getLongitude(), position.getLongitude()));
            bottomLeft.setLatitude(Math.min(topRight.getLatitude(), position.getLatitude()));
            bottomLeft.setLongitude(Math.min(topRight.getLongitude(), position.getLongitude()));
        }
        if (topRight.getLatitude() - bottomLeft.getLatitude() > range ||
                topRight.getLongitude() - bottomLeft.getLongitude() > range) {
            Area result = new Area(name, wayInfo);
            result.setNodes(simplify(nodes, range));
            return result;
        } else {
            return null;
        }
    }

}
