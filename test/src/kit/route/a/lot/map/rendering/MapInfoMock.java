package kit.route.a.lot.map.rendering;

import java.util.HashSet;
import java.util.Set;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.map.MapElement;
import kit.route.a.lot.map.infosupply.MapInfo;


public class MapInfoMock extends MapInfo {
    
    private Set<MapElement> baseLayer = new HashSet<MapElement>();
    
    @Override
    public Set<MapElement> queryElements(int zoomlevel, Coordinates upLeft, Coordinates bottomRight, boolean exact) {
        return baseLayer;
    }
    
    public void addMapElement(MapElement element) {
        baseLayer.add(element);
    }
    
    @Override
    public void lastElementAdded() {
    }
}
