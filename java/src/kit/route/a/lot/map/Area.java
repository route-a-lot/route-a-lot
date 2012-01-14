package kit.route.a.lot.map;

import java.awt.Polygon;
import java.awt.geom.Rectangle2D;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.common.WayInfo;


public class Area extends MapElement {

    private Node[] nodes;

    private String name;

    private WayInfo wayInfo;
    
    


    public Area(String name, WayInfo wayInfo) {
        this.name = name;
        nodes = new Node[0];
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
        return this.name;
    }

    @Override
    protected Selection getSelection() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isInBounds(Coordinates topLeft, Coordinates bottomRight) {
        // TODO there is no float polygon, so I have to think about s.th. else (or leave it the way it is now)
        int x[] = new int[nodes.length];
        int y[] = new int[nodes.length];
        int i = 0;
        for (Node node : nodes) {
            x[i] = (int) (node.getPos().getLongitude() * 10000000); // 100000000 is a random factor, can be changed
            i++;
        }
        i = 0;
        for (Node node : nodes) {
            y[i] = (int) (node.getPos().getLatitude() * 10000000);
            i++;
        }
        Polygon area = new Polygon(x, y, nodes.length);
        Rectangle2D.Double box =
                new Rectangle2D.Double(topLeft.getLongitude() * 10000000 - 1, bottomRight.getLatitude() * 10000000 - 1,
                        (bottomRight.getLongitude() - topLeft.getLongitude()) * 10000000 + 1,
                        (topLeft.getLatitude() - bottomRight.getLatitude()) * 10000000 + 1);
        boolean inside = false;
        for (Node node : nodes) {
            if (node.isInBounds(topLeft, bottomRight)) {
                inside = true;
            }
        }
        return inside || area.contains(box) || area.intersects(box);

    }

    @Override
    protected void load(DataInputStream stream) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void save(DataOutputStream stream) {
        // TODO Auto-generated method stub
    }

}
