package kit.route.a.lot.map;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.POIDescription;

public class POINode extends Node {

    private POIDescription info;

    public POINode(int id, Coordinates position, POIDescription description){
        super(id, position);
        this.info = description;
    }
    
    public POINode() {
        this(-1, null, null);
    }

    public POIDescription getInfo() {
        return info;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof POINode) {
            return ((POINode) obj).getID() == this.getID();
        }
        return false;
    }
}
