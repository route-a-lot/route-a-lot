package kit.route.a.lot.map.rendering;

import java.util.HashSet;
import java.util.Set;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.map.MapElement;
import kit.route.a.lot.map.infosupply.MapInfo;


public class MapInfoMock extends MapInfo {
    
    private Set<MapElement> baseLayer = new HashSet<MapElement>();
    private Set<MapElement> overlay = new HashSet<MapElement>();
    
    public void addToBaseLayer(MapElement element) {
        baseLayer.add(element);
    }
    
    public void addToOverlay(MapElement element) {
        overlay.add(element);
    }
    
    @Override
    public Set<MapElement> getBaseLayer(int zoomlevel, Coordinates upLeft, Coordinates bottomRight, boolean exact) {
        return baseLayer;
    }
    
    @Override
    public Set<MapElement> getOverlay(int zoomlevel, Coordinates upLeft, Coordinates bottomRight, boolean exact) {  
        return overlay;
    }
}
