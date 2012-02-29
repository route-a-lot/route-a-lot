package kit.route.a.lot.map;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.controller.State;

public class Node extends MapElement {

    private float lat;
    private float lon;

    public boolean equals(Object other) {
        if(other == this) {
            return true;
        }
        if(!(other instanceof Node)) {
            return false;
        }
        Node node = (Node) other;
        return lat == node.lat && lon == node.lon;
    }
    
    public Node(Coordinates pos, int id) {
        this.id = id;
        lat = pos.getLatitude();
        lon = pos.getLongitude();
    }
    
    public Node(Coordinates pos) {
        lat = pos.getLatitude();
        lon = pos.getLongitude();
    }

    public Node(int id) {
        this.id = id;
        lat = 0;
        lon = 0;
    }
    
    public Node() {
        lat = 0;
        lon = 0;
    }

    public Coordinates getPos() {
        return new Coordinates(lat, lon);
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean isInBounds(Coordinates topLeft, Coordinates bottomRight) {
        return getPos().isInBounds(topLeft, bottomRight);
    }

    @Override
    public Selection getSelection() { //TODO: verify
        return State.getInstance().getMapInfo().select(getPos());
    }


    @Override
    public MapElement getReduced(int detail, float rang) {
        if (detail > 2) {
            return null;
        } else {
            return this;
        }
    }

    @Override
    protected void load(DataInput input) throws IOException {
        this.id = input.readInt();
        this.lat = input.readFloat();
        this.lon = input.readFloat();
    }

    @Override
    protected void save(DataOutput output) throws IOException {
        output.writeInt(id);
        output.writeFloat(this.lat);
        output.writeFloat(this.lon);
    }
    
    @Override
    public String toString() {
        return "Node at " + lat + " , " + lon;
    }

}
