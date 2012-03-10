package kit.ral.map.rendering;

import java.util.Set;

import kit.ral.common.Bounds;
import kit.ral.map.MapElement;
import kit.ral.map.info.ArrayElementDB;
import kit.ral.map.info.QTGeographicalOperator;


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
