package kit.route.a.lot.map;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Selection;

public class Node extends MapElement {

    /** Attributes */
    /**
     * 
     */
    private Coordinates pos;
    /** Associations */
    private ArrayList<Edge> outgoingEdges;

    public Node(int id, Coordinates pos) {
        this.id = id;
        this.pos = pos;
    }
    

    public Coordinates getPos() {
        return pos;
    }
    
    public void addOutgoingEdge(Edge edge) {
        outgoingEdges.add(edge);
    }

    public ArrayList<Edge> getOutgoingEdges() {
        return outgoingEdges;
    }



    @Override
    protected String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Selection getSelection() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected boolean isInBounds(Coordinates topLeft, Coordinates bottomRight) {
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
