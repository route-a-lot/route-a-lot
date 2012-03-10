package kit.ral.map.rendering;

import java.util.ArrayList;
import java.util.List;

import kit.ral.common.Selection;
import kit.ral.controller.State;


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
