package kit.route.a.lot.map;

import java.io.InputStream;import java.io.OutputStream;
import java.awt.geom.Path2D.Float;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Selection;


public class Edge extends MapElement {
    
    
    private Node start;
    private Node end;
    private Street street;
    
    /**
     * Constructor
     */
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
        Line2D.Float edge = new Line2D.Float(start.getPos().getLongitude(), start.getPos().getLatitude(),
                                            end.getPos().getLongitude(), end.getPos().getLatitude());
        Rectangle2D.Float box = new Rectangle2D.Float(topLeft.getLongitude(), bottomRight.getLatitude(),    //coord.sys. begins in upper left corner 
                                                bottomRight.getLongitude() - topLeft.getLongitude(),
                                                topLeft.getLatitude() - bottomRight.getLatitude());
        return box.intersectsLine(edge) || start.isInBounds(topLeft, bottomRight) || end.isInBounds(topLeft, bottomRight);
        //TODO pos -> neg (e.g. -180° -> 180°)
    }
    
    public float getRatio (Coordinates pos) {
        return 0.0f;  //TODO "fall lot"
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
