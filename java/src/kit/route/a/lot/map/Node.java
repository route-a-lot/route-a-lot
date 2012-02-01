package kit.route.a.lot.map;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.controller.State;

public class Node extends MapElement {

    private float lat;
    private float lon;

    public Node(Coordinates pos) {
        lat = pos.getLatitude();
        lon = pos.getLongitude();
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
        return State.getInstance().getLoadedMapInfo().select(getPos());
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
    protected void load(DataInputStream stream) throws IOException {
        this.lat = stream.readFloat();
        this.lon = stream.readFloat();
    }

    @Override
    protected void save(DataOutputStream stream) throws IOException {
        stream.writeFloat(this.lat);
        stream.writeFloat(this.lon);
    }

}
