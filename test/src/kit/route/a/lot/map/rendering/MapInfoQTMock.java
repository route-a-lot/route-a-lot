package kit.route.a.lot.map.rendering;

import java.util.Set;

import kit.route.a.lot.common.Bounds;
import kit.route.a.lot.map.MapElement;
import kit.route.a.lot.map.infosupply.ArrayElementDB;
import kit.route.a.lot.map.infosupply.QTGeographicalOperator;


public class MapInfoQTMock extends MapInfoMock {
    
    QTGeographicalOperator operator;
    ArrayElementDB db = new ArrayElementDB();
    
    MapInfoQTMock(Bounds bounds) {
        operator = new QTGeographicalOperator();
        operator.setBounds(bounds);
    }

    @Override
    public void addMapElement(MapElement element) {
        db.addMapElement(element);
    }
    
    public void lastElementAdded() {
        operator.fill(db);
    }
    
    @Override
    public Set<MapElement> queryElements(int zoomlevel, Bounds area, boolean exact) {
        return operator.queryElements(area, zoomlevel, exact);
    }

}
