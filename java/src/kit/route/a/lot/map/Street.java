package kit.route.a.lot.map;

import java.io.InputStream;import java.io.OutputStream;
import java.util.ArrayList;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.common.WayInfo;


public class Street extends MapElement {

    private int type;
   
    private ArrayList<Edge> edges;
    
    private String name;
    
    private WayInfo wayInfo;

    
    
    public Street(int type, String name, WayInfo wayInfo) {
        this.type = type;
        edges = new ArrayList<Edge>();
        this.name = name;
        this.wayInfo = wayInfo;
    }

    @Override
    protected String getName() {
        return name;
    }
    
    public int getType() {
        return type;
    }
    
    public ArrayList<Edge> getEdges() {
        return edges;
    }
    
    public WayInfo getWayInfo() {
        return wayInfo;
    }
    
    public void addEdge(Edge edge) {
        edges.add(edge);
    }



    @Override
    protected Selection getSelection() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isInBounds(Coordinates topLeft, Coordinates bottomRight) { //not necessary, cause we draw edges
        boolean inBounds = false;
        int i = 0;
        while(inBounds == false && i < edges.size()) {
            inBounds = edges.get(i).isInBounds(topLeft, bottomRight);
            i++;
        }
        return inBounds;
    }

    @Override
    protected void load(InputStream stream) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void save(OutputStream stream) {
        // TODO Auto-generated method stub

    }
}
