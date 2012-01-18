package kit.route.a.lot.map;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Selection;

public class Node extends MapElement {

    private float lat;
    private float lon;

    public Node(Coordinates pos) {
        lat = pos.getLatitude();
        lon = pos.getLongitude();
    }

    public Node(float latitude, float longitude, int id) {
        lat = latitude;
        lon = longitude;
        this.id = id;
    }

    public Node() {
        this(null);
    }

    public Coordinates getPos() {
        return new Coordinates(lat, lon);
    }

    @Override
    protected String getName() {
        return null;
    }

    @Override
    public boolean isInBounds(Coordinates topLeft, Coordinates bottomRight) {
        float minLat = (float) Math.min(topLeft.getLatitude(), bottomRight.getLatitude());
        float maxLat = (float) Math.max(topLeft.getLatitude(), bottomRight.getLatitude());
        float minLon = (float) Math.min(topLeft.getLongitude(), bottomRight.getLongitude());
        float maxLon = (float) Math.max(topLeft.getLongitude(), bottomRight.getLongitude());
        return (lat <= maxLat && lat >= minLat && lon >= minLon && lon <= maxLon);
    }

    @Override
    public Selection getSelection() {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    protected void load(DataInputStream stream) throws IOException {
        this.lon = stream.readFloat();
        this.lat = stream.readFloat();
    }

    @Override
    protected void save(DataOutputStream stream) throws IOException {
        stream.writeFloat(this.lon);
        stream.writeFloat(this.lat);
    }

    @Override
    public MapElement getReduced(int detail, float rang) {
        // TODO Auto-generated method stub
        return null;
    }

}
