package kit.route.a.lot.map.rendering;

import java.util.ArrayList;
import java.util.Collection;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.map.MapElement;
import kit.route.a.lot.map.infosupply.MapInfo;


public class MapInfoMock extends MapInfo {
    
    private Collection<MapElement> baseLayer = new ArrayList<MapElement>();
    private Collection<MapElement> overlay = new ArrayList<MapElement>();
    
    public void addToBaseLayer(MapElement element) {
        baseLayer.add(element);
    }
    
    public void addToOverlay(MapElement element) {
        overlay.add(element);
    }
    
    @Override
    public Collection<MapElement> getBaseLayer(int zoomlevel, Coordinates upLeft, Coordinates bottomRight, boolean exact) {
        return baseLayer;
    }
    
    @Override
    public Collection<MapElement> getOverlay(int zoomlevel, Coordinates upLeft, Coordinates bottomRight, boolean exakt) {  
        return overlay;
    }
}
