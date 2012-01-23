package kit.route.a.lot.common;

import java.util.ArrayList;


public class RouteDescription {
    public ArrayList<String> captions;
    public ArrayList<String> descriptions;
    public ArrayList<Integer> positionIDs;
    
    public RouteDescription() {
        captions = new ArrayList<String>();
        descriptions = new ArrayList<String>();
        positionIDs = new ArrayList<Integer>();
    }
    
    public ArrayList<String> getCaptions() {
        return captions;
    }
    
    public void setCaptions(ArrayList<String> captions) {
        this.captions = captions;
    }
    
    public ArrayList<String> getDescriptions() {
        return descriptions;
    }
    
    public void setDescriptions(ArrayList<String> descriptions) {
        this.descriptions = descriptions;
    }
    
    public ArrayList<Integer> getPositionIDs() {
        return positionIDs;
    }
    
    public void setPositionIDs(ArrayList<Integer> positionIDs) {
        this.positionIDs = positionIDs;
    }
    
    
}
