package kit.route.a.lot.io;

import java.util.List;

import kit.route.a.lot.common.Address;
import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.POIDescription;
import kit.route.a.lot.common.WayInfo;
import kit.route.a.lot.map.infosupply.MapInfo;


class MapInfoMock extends MapInfo {
    
    long nodeCount, wayCount;
    
    
    public MapInfoMock() {
        nodeCount = 0;
        wayCount = 0;
    }
    
    public boolean equals(Object other) {
        if(other == this) {
            return true;
        }
        if(!(other instanceof MapInfoMock)) {
            return false;
        }
        MapInfoMock comparee = (MapInfoMock) other;
        return nodeCount == comparee.nodeCount
                && wayCount == comparee.wayCount;
    }
    
    @Override
    public void addWay(List<Integer> ids, String name, WayInfo wayInfo) {
        wayCount++;
    }
    
    @Override
    public void addNode(Coordinates position, int id, Address address) {
        nodeCount++;
    }
    
    @Override
    public void addPOI(Coordinates position, POIDescription description, Address address) {
        addNode(position, -1, address);
    }
    
    @Override
    public void setBounds(Coordinates upLeft, Coordinates bottomRight) {
    }

    @Override
    public void swapNodeIds(int id1, int id2) {
    }
}
