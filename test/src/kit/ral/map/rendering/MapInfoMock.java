package kit.ral.map.rendering;

import java.util.HashSet;
import java.util.Set;

import kit.ral.common.Bounds;
import kit.ral.map.MapElement;
import kit.ral.map.info.MapInfo;


public class MapInfoMock extends MapInfo {
    
    private Set<MapElement> baseLayer = new HashSet<MapElement>();
    
    @Override
    public Set<MapElement> queryElements(int zoomlevel, Bounds area, boolean exact) {
        return baseLayer;
    }
    
    public void addMapElement(MapElement element) {
        baseLayer.add(element);
    }
    
    @Override
    public void lastElementAdded() {
    }
}
