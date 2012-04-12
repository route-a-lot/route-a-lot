package kit.ral.map;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.MappedByteBuffer;

import kit.ral.common.Bounds;
import kit.ral.common.Coordinates;
import kit.ral.common.Selection;
import kit.ral.controller.State;

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
    
    float getLatitude() {
        return lat;
    }

    float getLongitude() {
        return lon;
    }

    @Override
    public String getName() {
        return null;
    }
    
    @Override
    public String getFullName() {
        return getName();
    }

    @Override
    public boolean isInBounds(Bounds bounds) {
        return (bounds.getTop() < lat) && (bounds.getBottom() > lat) 
            && (bounds.getLeft() < lon) && (bounds.getRight() > lon);
    }

    @Override
    public Selection getSelection() {
        Selection sel = State.getInstance().getMapInfo().select(getPos());
        sel.setName(getFullName());
        return sel;
    }


    @Override
    public MapElement getReduced(int detail, float range) {
        return (detail > 2) ? null : this;
    }

    @Override
    protected void load(DataInput input) throws IOException {
        this.id = input.readInt();
        this.lat = input.readFloat();
        this.lon = input.readFloat();
    }
    
    @Override
    protected void load(MappedByteBuffer mmap) throws IOException {
        this.id = mmap.getInt();
        this.lat = mmap.getFloat();
        this.lon = mmap.getFloat();
    }

    @Override
    protected void save(DataOutput output) throws IOException {
        output.writeInt(id);
        output.writeFloat(this.lat);
        output.writeFloat(this.lon);
    }
    
    @Override
    public String toString() {
        return "Node " + id + " at " + lat + " , " + lon;
    }

}
