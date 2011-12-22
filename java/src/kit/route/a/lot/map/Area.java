package kit.route.a.lot.map;

import java.io.InputStream;import java.io.OutputStream;
import java.util.ArrayList;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Selection;


public class Area extends MapElement {

    /** Attributes */
    /**
     * 
     */
    private int type;
    /** Associations */
    private ArrayList<Node> nodes;
    
    private String name;

    public Area(int type, String name) {
        this.type = type;
        this.name = name;
        nodes = new ArrayList<Node>();
    }

    
    
    
    public int getType() {
        return type;
    }
    
    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public void addNode(Node node) {
        nodes.add(node);
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
        // TODO overlap
        for(Node node: nodes) {
            if (node.isInBounds(topLeft, bottomRight)){
                return true;
            }
        }
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
