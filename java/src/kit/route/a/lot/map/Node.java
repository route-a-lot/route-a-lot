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
        return (lat <= topLeft.getLatitude() && lat >= bottomRight.getLatitude()
                && lon >= topLeft.getLongitude() && lon <= bottomRight.getLongitude());

        // TODO pos -> neg (e.g. -180° -> 180°), but this is to do for every isInBounds-Fkt. for the
        // mapElements
    }
        
    @Override
    public Selection getSelection(Coordinates pos) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public float getDistanceTo(Coordinates pos) {
        // TODO Auto-generated method stub
        return 0;
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

}
