package kit.route.a.lot.map;

import java.awt.Polygon;
import java.awt.geom.Rectangle2D;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import kit.route.a.lot.common.Bounds;
import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.common.WayInfo;
import kit.route.a.lot.controller.State;
import kit.route.a.lot.map.infosupply.MapInfo;


public class Area extends MapElement {

    private Node[] nodes;

    private String name;

    private WayInfo wayInfo;
    
    private static final String EMPTY = "";

    public Area(String name, WayInfo wayInfo) {
        this.name = name;
        this.nodes = new Node[0];
        this.wayInfo = wayInfo;
    }

    public Area() {
        this(null, null);
    }

    @Override
    public boolean equals(Object other) {
        if(other == this) {
            return true;
        }
        if(!(other instanceof Area)) {
            return false;
        }
        Area comparee = (Area) other;
        return nodes.equals(comparee.nodes)
                && name == comparee.name
                && wayInfo.equals(comparee.wayInfo);
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
    public String getName() {
        return (this.name != null) ? this.name : "";
    }

    @Override
    public boolean isInBounds(Bounds bounds) {
        // TODO there is no float polygon, so I have to think about s.th. else (or leave it the way it is now)
        int x[] = new int[nodes.length];
        int y[] = new int[nodes.length];
        int i = 0;
        for (Node node : nodes) {
            x[i] = (int) (node.getPos().getLongitude() * 1000); // 100000000 is a random factor, can be changed
            i++;
        }
        i = 0;
        for (Node node : nodes) {
            y[i] = (int) (node.getPos().getLatitude() * 1000);
            i++;
        }
        Polygon area = new Polygon(x, y, nodes.length);
        Rectangle2D.Double box =
                new Rectangle2D.Double(Math.min(bounds.getLeft(), bounds.getRight()) * 1000 - 1, 
                        Math.min(bounds.getTop(), bounds.getBottom()) * 1000 - 1,
                        (Math.abs(bounds.getWidth())) * 1000 + 1,
                        (Math.abs(bounds.getHeight())) * 1000 + 1);
        boolean inside = false;
        for (Node node : nodes) {
            if (node.isInBounds(bounds)) {
                inside = true;
            }
        }
        return inside || area.contains(box) || area.intersects(box);

    }

    /**
     * Returns a selection with pos = the center of the area and from and to as normal (routable edge).
     */
    @Override
    public Selection getSelection() {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override //TODO: attribs, load() and save() are identical to methods of same name in Street
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
    protected void save(DataOutput output) throws IOException {
        output.writeUTF(getName());
        output.writeInt(this.nodes.length);
        for (Node node: this.nodes) {
            output.writeInt(node.getID());
        }
        this.wayInfo.saveToOutput(output);
    }

    @Override
    public MapElement getReduced(int detail, float range) {
        Coordinates topLeft = new Coordinates(nodes[0].getPos().getLatitude(), nodes[0].getPos().getLongitude());
        Coordinates bottomRight = new Coordinates(nodes[0].getPos().getLatitude(), nodes[0].getPos().getLongitude());
        Coordinates position;
        for (Node node: nodes) {
            position = node.getPos();
            topLeft.setLatitude(Math.min(topLeft.getLatitude(), position.getLatitude()));
            topLeft.setLongitude(Math.min(topLeft.getLongitude(), position.getLongitude()));
            bottomRight.setLatitude(Math.max(bottomRight.getLatitude(), position.getLatitude()));
            bottomRight.setLongitude(Math.max(bottomRight.getLongitude(), position.getLongitude()));
        }
        if (Math.abs(topLeft.getLatitude() - bottomRight.getLatitude()) > range ||
                Math.abs(topLeft.getLongitude() - bottomRight.getLongitude()) > range) {
            Area result = new Area(name, wayInfo);
            result.setNodes(Street.simplifyNodes(nodes, range / 2));
            return result;
        } else {
            return null;
        }
    }
    
    public boolean equals(MapElement other){

        if(name.equals(other.getName()) ){
            return true;
        }
        return false;
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

        /*wenn Präfix gleich aber dieser String kürzer 
                  steht er lexikographisch weiter vorne*/
        return name.length() - otherName.length();
    }

    

}
