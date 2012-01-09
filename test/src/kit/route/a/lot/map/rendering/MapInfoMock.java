package kit.route.a.lot.map.rendering;

import java.util.Collection;
import java.util.LinkedList;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.map.MapElement;
import kit.route.a.lot.map.infosupply.MapInfo;


public class MapInfoMock extends MapInfo {
    
    private Collection<MapElement> elements = new LinkedList<MapElement>();

    public Collection<MapElement> getBaseLayer(int zoomlevel, Coordinates upLeft, Coordinates bottomRight) {
        return elements;
    }
    
    public void addMapElement(MapElement element) {
        elements.add(element);
    }
}
