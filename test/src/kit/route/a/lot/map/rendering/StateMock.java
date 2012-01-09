package kit.route.a.lot.map.rendering;

import kit.route.a.lot.controller.State;
import kit.route.a.lot.map.infosupply.MapInfo;


public class StateMock extends State {
    
    private MapInfoMock mapInfo = new MapInfoMock();
    
    public MapInfo getLoadedMapInfo() {
        return mapInfo;
    }

}
