package kit.ral.map;

import java.awt.Polygon;
import java.awt.geom.Rectangle2D;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.MappedByteBuffer;

import kit.ral.common.Bounds;
import kit.ral.common.Coordinates;
import kit.ral.common.Selection;
import kit.ral.common.description.WayInfo;
import kit.ral.common.util.Util;
import kit.ral.controller.State;
import kit.ral.map.info.MapInfo;



public class Area extends MapElement {
    
    private static final int MIN_REDUCTION_NODES = 4;
    
    private String name;
    private Node[] nodes = new Node[0];
    private WayInfo wayInfo;

    
    // CONSTRUCTORS
    
    public Area() {
        this(null, null);
    }
    
    public Area(String name, WayInfo wayInfo) {
        this.name = name;
        this.wayInfo = wayInfo;
    }

    
    // GETTERS & SETTERS
    
    @Override
    public String getName() {
        return (this.name != null) ? this.name : "";
    }
    
    public Node[] getNodes() {
        return nodes;
    }

    public WayInfo getWayInfo() {
        return wayInfo;
    }

    public void setNodes(Node[] nodes) {
        if (nodes == null || nodes.length == 0) {
            throw new IllegalArgumentException();
        }
        this.nodes = nodes;
    }
    
    
    // GENERAL MAP ELEMENT OPERATIONS
    
    @Override
    public boolean isInBounds(Bounds bounds) {
        // TODO there is no float polygon, so I have to think about s.th. else (or leave it the way it is now)
        int x[] = new int[nodes.length];
        int y[] = new int[nodes.length];
        int i = 0;
        for (Node node : nodes) {
            x[i] = (int) (node.getPos().getLongitude() * 1000); // 1000 is a random factor, can be changed
            i++;
        }
        i = 0;
        for (Node node : nodes) {
            y[i] = (int) (node.getPos().getLatitude() * 1000);
            i++;
        }
        Polygon area = new Polygon(x, y, nodes.length);
        Rectangle2D.Double box = new Rectangle2D.Double(
                bounds.getLeft() * 1000 - 1, bounds.getTop() * 1000 - 1,
                bounds.getWidth() * 1000 + 1, bounds.getHeight() * 1000 + 1);
        boolean inside = false;
        for (Node node : nodes) {
            if (node.isInBounds(bounds)) {
                inside = true;
            }
        }
        return inside || area.contains(box) || area.intersects(box);
    }

    @Override
    public MapElement getReduced(int detail, float range) {
        // draw everything on detail 0
        if (detail == 0) {
            return this;
        }
        // determine bounding box, discard too small areas
        Bounds bounds = new Bounds(nodes[0].getPos(), 0);
        for (Node node: nodes) {
            bounds.extend(node.getLatitude(), node.getLongitude());
        }  
        if (bounds.getHeight() + bounds.getWidth() < range) {
            return null;
        }
        // return simplified area
        Area result = new Area(name, wayInfo);
        result.setNodes(Street.simplifyNodes(nodes, range / 2));
        if (result.nodes.length <= MIN_REDUCTION_NODES && nodes.length > MIN_REDUCTION_NODES) {
            return null;
        }
        return (result.nodes.length == nodes.length) ? this : result;
    }
      
    @Override
    public Selection getSelection() {
        Coordinates center = new Coordinates();
        for (Node node : nodes) {
            center.add(node.getPos());
        }
        return State.getInstance().getMapInfo().select(center.scale(1f / nodes.length));
    }
    
    
    // I/O OPERATIONS
    
    @Override
    protected void load(DataInput input) throws IOException {
        String name = input.readUTF();
        this.name = EMPTY.equals(name) ? null : name;
        int len = input.readInt();
        this.nodes = new Node[len];
        MapInfo mapInfo = State.getInstance().getMapInfo();
        for (int i = 0; i < len; i++) {        
            this.nodes[i] = mapInfo.getNode(input.readInt());
        }
        this.wayInfo = WayInfo.loadFromInput(input);
    }
    
    @Override
    protected void load(MappedByteBuffer mmap) throws IOException {
        String name = Util.readUTFString(mmap);
        this.name = EMPTY.equals(name) ? null : name;
        int len = mmap.getInt();
        this.nodes = new Node[len];
        MapInfo mapInfo = State.getInstance().getMapInfo();
        for (int i = 0; i < len; i++) {        
            this.nodes[i] = mapInfo.getNode(mmap.getInt());
        }
        this.wayInfo = WayInfo.loadFromInput(mmap);
    }

    @Override
    protected void save(DataOutput output) throws IOException {
        output.writeUTF(getName());
        output.writeInt(this.nodes.length);
        for (Node node: this.nodes) {
            output.writeInt(node.getID());
        }
        this.wayInfo.saveToOutput(output);
    }

    
    // MISCELLANEOUS
        
    @Override
    public boolean equals(Object other){
        return (other == this) || (
               (other != null) && (other instanceof Area)
                && getName().equals(((Area) other).getName())
                && nodes.equals(((Area) other).nodes)
                && wayInfo.equals(((Area) other).wayInfo));
    }

    public int compare(MapElement one, MapElement other){
        int value;
        int otherValue;
        String otherName = other.getName();
        int minlength = otherName.length();
        if(name.length() < minlength){
            minlength = name.length();
        }

        for(int i = 0; i < minlength; i++){
            value = Character.getNumericValue( name.charAt(i) );
            otherValue = Character.getNumericValue( otherName.charAt(i) );

            if(value > otherValue){
                return otherValue - value;
            } else if(value < otherValue) {
                return value - otherValue;
            }               
        }

        /*wenn PrÃ¤fix gleich aber dieser String kÃ¼rzer 
                  steht er lexikographisch weiter vorne*/
        return name.length() - otherName.length();
    }   

}
