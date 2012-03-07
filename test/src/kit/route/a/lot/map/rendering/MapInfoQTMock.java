package kit.route.a.lot.map.rendering;

import java.util.Set;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.map.MapElement;
import kit.route.a.lot.map.infosupply.ArrayElementDB;
import kit.route.a.lot.map.infosupply.QTGeographicalOperator;


public class MapInfoQTMock extends MapInfoMock {
    
    QTGeographicalOperator operator;
    ArrayElementDB db = new ArrayElementDB();
    
    MapInfoQTMock(Coordinates topLeft, Coordinates bottomRight) {
        operator = new QTGeographicalOperator();
        operator.setBounds(topLeft, bottomRight);
    }

    @Override
    public void addMapElement(MapElement element) {
        db.addMapElement(element);
    }
    
    public void lastElementAdded() {
        operator.fill(db);
    }
    
    @Override
    public Set<MapElement> queryElements(int zoomlevel, Coordinates upLeft, Coordinates bottomRight, boolean exact) {
        return operator.queryElements(upLeft, bottomRight, zoomlevel, exact);
    }

}
