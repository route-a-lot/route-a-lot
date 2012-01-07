package kit.route.a.lot.io;

import java.util.List;

import kit.route.a.lot.common.Address;
import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.POIDescription;
import kit.route.a.lot.common.WayInfo;
import kit.route.a.lot.map.infosupply.MapInfo;


class MapInfoMock extends MapInfo {
    
    long nodeCount;
    
    
    public MapInfoMock() {
        nodeCount = 0;
    }
    
    @Override
    public void addWay(List<Integer> ids, String name, WayInfo wayInfo) {
    }
    
    @Override
    public void addNode(Coordinates position, int id, Address address) {
        nodeCount++;
        if (nodeCount % 130000 == 0) {
            System.out.println("Current node count: " + nodeCount + " ~= " +
                    (nodeCount * 100 / 13395533) + "%");
        }
    }
    
    @Override
    public void addPOI(Coordinates position, int id, POIDescription description, Address address) {
        addNode(position, id, address);
    }
    
    @Override
    public void setBounds(Coordinates upLeft, Coordinates bottomRight) {
    }
}
