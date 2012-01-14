package kit.route.a.lot.map;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Selection;

public class Node extends MapElement {

    private Coordinates pos;
    private ArrayList<Edge> outgoingEdges;
    private int id;

    public Node(int id, Coordinates pos) {
        this.id = id;
        this.pos = pos;
        outgoingEdges = new ArrayList<Edge>();
    }
    

    public Node() {
        this(-1, null);
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
        return null;
    }

    @Override
    protected Selection getSelection() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isInBounds(Coordinates topLeft, Coordinates bottomRight) {
        return (this.pos.getLatitude() <= topLeft.getLatitude() 
                && this.pos.getLatitude() >= bottomRight.getLatitude()                
                && this.pos.getLongitude() >= topLeft.getLongitude()       
                && this.pos.getLongitude() <= bottomRight.getLongitude());
                
      //TODO pos -> neg (e.g. -180° -> 180°), but this is to do for every isInBounds-Fkt. for the mapElements
    }

    @Override
    protected void load(DataInputStream stream) {
        // TODO Auto-generated method stub
        
    }


    @Override
    protected void save(DataOutputStream stream) {
        // TODO Auto-generated method stub
        
    }


    public int getID() {
        return this.id;
    }

}
