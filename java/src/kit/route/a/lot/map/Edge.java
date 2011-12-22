package kit.route.a.lot.map;

import java.io.InputStream;import java.io.OutputStream;
import java.awt.geom.Rectangle2D;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Selection;


public class Edge extends MapElement {
    
    /**
     * Constructor
     */
    private Node start;
    private Node end;
    private Street street;
    
    
    public Edge(Node start, Node end, Street street){
        this.start = start;
        this.end = end;
        this.street = street;
    }
    
    public Node getStart() {
        return start;
    }

    public Node getEnd() {
        return end;
    }
    
    public Street geStreet(){
        return street;
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
    public boolean isInBounds(Coordinates topLeft, Coordinates bottomRight) {
        // TODO overlap
        return (this.start.isInBounds(topLeft, bottomRight) || this.end.isInBounds(topLeft, bottomRight));
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
