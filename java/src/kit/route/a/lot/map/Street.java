package kit.route.a.lot.map;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.common.WayInfo;


public class Street extends MapElement {

    private Edge[] edges;

    private String name;

    private WayInfo wayInfo;

    public Street(String name, WayInfo wayInfo) {
        edges = new Edge[0];
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

    public Edge[] getEdges() {
        return edges;
    }

    public WayInfo getWayInfo() {
        return wayInfo;
    }

    public void setEdges(Edge[] edges) {
        this.edges = edges;
    }


    @Override
    protected Selection getSelection() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isInBounds(Coordinates topLeft, Coordinates bottomRight) { // not necessary, cause we draw
        // edges
        boolean inBounds = false;
        int i = 0;
        while (inBounds == false && i < edges.length) {
            inBounds = edges[i].isInBounds(topLeft, bottomRight);
            i++;
        }
        return inBounds;
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
