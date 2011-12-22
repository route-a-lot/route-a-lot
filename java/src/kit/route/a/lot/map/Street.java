package kit.route.a.lot.map;

import java.io.InputStream;import java.io.OutputStream;
import java.util.ArrayList;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Selection;


public class Street extends MapElement {

    /** Attributes */
    /**
     * 
     */
    private int type;
    /** Associations */
    private ArrayList<Edge> edges;
    private String name;

    
    
    public Street(int type, String name) {
        super();
        this.type = type;
        edges = new ArrayList<Edge>();
        this.name = name;
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
        // TODO Auto-generated method stub
        return false;       
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
