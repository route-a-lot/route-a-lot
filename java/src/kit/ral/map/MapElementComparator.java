package kit.ral.map;

import java.util.Comparator;

import kit.ral.common.description.WayInfo;


public class MapElementComparator implements Comparator<MapElement> {

    @Override
    public int compare(MapElement a, MapElement b) {
        int result = getLayer(a) - getLayer(b);
        if ((result == 0) && (a != b)) {
            result = 1;
        }
        return result;
    }
    
    public int getLayer(MapElement element) {
        WayInfo info = null;
        if (element instanceof Area) {
            info = ((Area) element).getWayInfo();
        } else if (element instanceof Street) {
            info = ((Street) element).getWayInfo();
        }
        if (info != null) {
            return info.getType() + (info.getLayer() + 5) * 50;
        } else{
            return 6000;
        }
    }

}
