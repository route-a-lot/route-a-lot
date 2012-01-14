package kit.route.a.lot.map;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Selection;

public class Node extends MapElement {

    private int id;
    private float lat;
    private float lon;

    public Node(int id, Coordinates pos) {
        this.id = id;
        lat = pos.getLatitude();
        lon = pos.getLongitude();
    }

    public Node() {
        this(-1, null);
    }


    public Coordinates getPos() {
        return new Coordinates(lat, lon);
    }

    public ArrayList<Edge> getOutgoingEdges() {
        throw new UnsupportedOperationException("Unsupported due to memory performance.");
    }

    @Override
    protected String getName() {
        return null;
    }

    @Override
    protected Selection getSelection() {
        // TODO Auto-generated method stub
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
    protected void load(DataInputStream stream) {
        // TODO Auto-generated method stub

    }


    @Override
    protected void save(DataOutputStream stream) {
        // TODO Auto-generated method stub

    }


    public int getID() {
        return this.id;
    }

}
