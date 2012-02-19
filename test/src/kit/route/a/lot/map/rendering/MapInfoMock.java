package kit.route.a.lot.map.rendering;

import java.util.ArrayList;
import java.util.Collection;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.map.MapElement;
import kit.route.a.lot.map.infosupply.MapInfo;


public class MapInfoMock extends MapInfo {
    
    private Collection<MapElement> elements = new ArrayList<MapElement>();

    public Collection<MapElement> getBaseLayer(int zoomlevel, Coordinates upLeft, Coordinates bottomRight) {
        return elements;
    }
    
    public void addMapElement(MapElement element) {
        System.out.println("vor addElement");
        System.out.println(elements.add(element));
        System.out.println("size: "+ elements.size());
    }
}
