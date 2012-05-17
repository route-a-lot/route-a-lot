
/**
Copyright (c) 2012, Matthias Grundmann, Jan Jacob, Josua Stabenow
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
        * Redistributions of source code must retain the above copyright
          notice, this list of conditions and the following disclaimer.
        * Redistributions in binary form must reproduce the above copyright
          notice, this list of conditions and the following disclaimer in the
          documentation and/or other materials provided with the distribution.
        * The names of the contributors may not be used to endorse or promote products
          derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
**/

package kit.ral.io;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import kit.ral.common.Bounds;
import kit.ral.common.Coordinates;
import kit.ral.common.description.Address;
import kit.ral.common.description.POIDescription;
import kit.ral.common.description.WayInfo;
import kit.ral.map.Node;
import kit.ral.map.info.MapInfo;


public class MapInfoMock extends MapInfo {
    
    long nodeCount, wayCount;
    List<Node> nodes = new ArrayList<Node>();
    
    private static Logger logger = Logger.getLogger(MapInfoMock.class);
    
    
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
        if (wayCount % 10000 == 0) {
            logger.debug("Added ways: " + wayCount);
        }
    }
    
    @Override
    public void addNode(Coordinates position, int id, Address address) {
        nodeCount++;
        if (nodeCount % 10000 == 0) {
            logger.debug("Added nodes: " + nodeCount);
        }
        if (id != nodes.size()) {
            logger.warn("Wrong id for node: " + id);
        }
        nodes.add(new Node(position, id));
    }
    
    @Override
    public void addPOI(Coordinates position, POIDescription description, Address address) {
//        addNode(position, -1, address);
    }
    
    @Override
    public void setBounds(Bounds bounds) {
    }
    
    @Override
    public Node getNode(int id) {
        return nodes.get(id);
    }
    
    @Override
    public Coordinates getNodePosition(int id) {
        return getNode(id).getPos();
    }

    @Override
    public void swapNodeIds(int id1, int id2) {
        nodes.get(id1).setID(id2);
        nodes.get(id2).setID(id1);
        Collections.swap(nodes, id1, id2);
    }
}
