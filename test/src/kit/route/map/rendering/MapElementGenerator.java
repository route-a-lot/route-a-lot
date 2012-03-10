package kit.route.a.lot.map.rendering;

import kit.route.a.lot.common.Bounds;
import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.description.OSMType;
import kit.route.a.lot.common.description.WayInfo;
import kit.route.a.lot.map.Area;
import kit.route.a.lot.map.Node;
import kit.route.a.lot.map.Street;


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
