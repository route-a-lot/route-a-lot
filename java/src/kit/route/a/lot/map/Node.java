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
        this.lat = stream.readFloat();
        this.lon = stream.readFloat();
    }

    @Override
    protected void save(DataOutputStream stream) throws IOException {
        stream.writeFloat(this.lat);
        stream.writeFloat(this.lon);
    }

    @Override
    public MapElement getReduced(int detail, float rang) {
        if (detail > 2) {
            return null;
        } else {
            return this;
        }
    }
    
    public boolean equals(MapElement other){
        /*wird nicht verwendet*/
        return false;
    }

    public int compare(MapElement one, MapElement other){
        /*wird nicht verwendet*/
        return 0;
    }


}
