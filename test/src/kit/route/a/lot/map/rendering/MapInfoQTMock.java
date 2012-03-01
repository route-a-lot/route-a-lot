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
        operator.addToBaseLayer(element);
    }
    
    public void addToOverlay(MapElement element) {
        operator.addToOverlay(element);
    }
    
    @Override
    public Set<MapElement> getBaseLayer(int zoomlevel, Coordinates upLeft, Coordinates bottomRight, boolean exact) {
        return operator.getBaseLayer(zoomlevel, upLeft, bottomRight, exact);
    }
    
    @Override
    public Set<MapElement> getOverlay(int zoomlevel, Coordinates upLeft, Coordinates bottomRight, boolean exact) {  
        return operator.getOverlay(zoomlevel, upLeft, bottomRight, exact);
    }
    
}
