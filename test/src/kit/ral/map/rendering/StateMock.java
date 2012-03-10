package kit.route.a.lot.map.rendering;

import java.util.ArrayList;
import java.util.List;

import kit.route.a.lot.common.Selection;
import kit.route.a.lot.controller.State;


public class StateMock extends State {
    
    private MapInfoMock mapInfo = new MapInfoMock();
    private List<Selection> navigationNodes = new ArrayList<Selection>();
    
    public MapInfoMock getMapInfo() {
        return mapInfo;
    }
    
    public List<Selection> getNavigationNodes() {
        return navigationNodes;
    }

    public List<Integer> getCurrentRoute() {
        return null;
    }

}
