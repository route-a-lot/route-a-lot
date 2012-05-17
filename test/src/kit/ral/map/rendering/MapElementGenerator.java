
/**
Copyright (c) 2012, Matthias Grundmann, Yvonne Braun, Josua Stabenow
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

package kit.ral.map.rendering;

import kit.ral.common.Bounds;
import kit.ral.common.Coordinates;
import kit.ral.common.description.OSMType;
import kit.ral.common.description.WayInfo;
import kit.ral.map.Area;
import kit.ral.map.Node;
import kit.ral.map.Street;


public class MapElementGenerator {
    
    private WayInfo wayinfo;
    
    public MapElementGenerator(){
        wayinfo = null;
    }
    
    public Street generateStreet(){
        wayinfo = new WayInfo();
        double choose = Math.random() * 2;
        if(choose < 0.15){
            wayinfo.setStreet(false);
        } else {
            wayinfo.setStreet(true);
            wayinfo.setBicycle(WayInfo.BICYCLE_YES);
        }
        choose = Math.random() * 5;
        if(choose <= 1){
            wayinfo.setType(OSMType.HIGHWAY_RESIDENTIAL);
        } else if(1 < choose && choose <= 2){
            wayinfo.setType(OSMType.HIGHWAY_TERTIARY);
        } else if(2 < choose && choose <= 3){
            wayinfo.setType(OSMType.HIGHWAY_SECONDARY);
        } else if(3 < choose && choose <= 4){
            wayinfo.setType(OSMType.HIGHWAY_PRIMARY);   
        } else if(4 < choose && choose <= 5){
            wayinfo.setType(OSMType.HIGHWAY_MOTORWAY);   
        }
        Street street = new Street(null, wayinfo);
        return street; 
    }
    
    public Area generateArea(){
        wayinfo = new WayInfo();
        double choose = Math.random() * 2;
        if(choose > 1){
            wayinfo.setBuilding(true);
        } else {
            wayinfo.setArea(true);
            choose = Math.random() * 2;
            if(choose > 1){
                wayinfo.setType(OSMType.NATURAL_WOOD);
            } else {
                wayinfo.setType(OSMType.NATURAL_WATER);
            }
            
        }
        
        Area area = new Area(null, wayinfo);
        Node[] nodes = new Node[11];
        
        for(int i = 0; i < 10; i++){
                nodes[i] = generateNode();
        }
        nodes[10] = nodes[0];
        return area;
    }
    
    
    
    public Node generateNode(){
        
        float lat = (float)(Math.random()*0.5);
        float lon = (float)(Math.random()*0.5);
        Coordinates coordinates = new Coordinates(lat, lon);
        
        Node node = new Node(coordinates);
        return node;
    }
    
    public Node generateNodeInBounds(Bounds bounds) {
        double latitude = bounds.getHeight() * Math.random() + bounds.getTop();
        double longitude = bounds.getWidth() * Math.random() + bounds.getLeft();
        return new Node(new Coordinates((float) latitude, (float) longitude));
    }
    
    public Street generateStreetInBounds(Bounds bounds) {
        Street street = generateStreet();
        Node[] nodes = new Node[(int) Math.random() * 12];
        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = generateNodeInBounds(bounds);
        }
        street.setNodes(nodes);
        return street;
    }
    
    public Area generateBuildingInBounds(Bounds bounds) {
        WayInfo wayInfo = new WayInfo();
        wayInfo.setBuilding(true);
        Area area = new Area(null, wayInfo);
        Node[] nodes = new Node[(int) Math.random() * 8 + 1];
        nodes[0] = generateNodeInBounds(bounds);
        for (int i = 1; i < nodes.length - 1; i++) {
            nodes[i] = generateNodeInBounds(new Bounds(nodes[i - 1].getPos(),
                    nodes[i - 1].getPos().clone().add((float) (10 * (Math.random() - 0.5)), (float) (10 * (Math.random() - 0.5)))));
        }
        nodes[nodes.length - 1] = nodes[0];
        area.setNodes(nodes);
        return area;
    }

}
