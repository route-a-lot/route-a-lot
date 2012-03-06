package kit.route.a.lot.map.rendering;

import java.util.Set;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.map.MapElement;
import kit.route.a.lot.map.infosupply.QTGeographicalOperator;


public class MapInfoQTMock extends MapInfoMock {
    
    QTGeographicalOperator operator;
    
    MapInfoQTMock(Coordinates topLeft, Coordinates bottomRight) {
        operator = new QTGeographicalOperator();
        operator.setBounds(topLeft, bottomRight);
    }

    public void addToBaseLayer(MapElement element) {
        operator.addElement(element);
    }
    
    @Override
    public Set<MapElement> queryElements(int zoomlevel, Coordinates upLeft, Coordinates bottomRight, boolean exact) {
        return operator.queryElements(zoomlevel, upLeft, bottomRight, exact);
    }

}
