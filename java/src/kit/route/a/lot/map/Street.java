package kit.route.a.lot.map;

import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.common.WayInfo;


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
        for (int i = 1; i < nodes.length; i++) {    
            // TODO this is not very performant, as a new Coordinates object is created each time
            if (isEdgeInBounds(nodes[i - 1].getPos(), nodes[i].getPos(),
                    topLeft, bottomRight)) {
                inBounds = true;
            }
        }
        return inBounds;
    }
    
    private boolean isEdgeInBounds(Coordinates node1, Coordinates node2,
                                   Coordinates topLeft, Coordinates bottomRight) {
        Line2D.Float edge = new Line2D.Float(node1.getLongitude(), node1.getLatitude(),
                                             node2.getLongitude(), node2.getLatitude());
        //coord.sys. begins in upper left corner 
        Rectangle2D.Float box = new Rectangle2D.Float(topLeft.getLongitude(), bottomRight.getLatitude(),    
                                                      bottomRight.getLongitude() - topLeft.getLongitude(),
                                                      topLeft.getLatitude() - bottomRight.getLatitude());
        return box.contains(node1.getLongitude(), node1.getLatitude())
            || box.contains(node2.getLongitude(), node2.getLatitude())
            || box.intersectsLine(edge);
        //TODO pos -> neg (e.g. -180° -> 180°)
    }

    @Override
    public Selection getSelection(Coordinates pos) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public float getDistanceTo(Coordinates pos) {
        // TODO Auto-generated method stub
        return 0;
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
