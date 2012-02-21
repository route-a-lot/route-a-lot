package kit.route.a.lot.map.rendering;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.OSMType;
import kit.route.a.lot.common.WayInfo;
import kit.route.a.lot.map.Area;
import kit.route.a.lot.map.Node;
import kit.route.a.lot.map.Street;


public class MapElementGenerator {
    
    private WayInfo wayinfo;
    
    public MapElementGenerator(){
        wayinfo = null;
    }
    
    public Street generateStreet(){
        //TODO
       return null; 
    }
    
    public Area generateArea(){
    
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

}
