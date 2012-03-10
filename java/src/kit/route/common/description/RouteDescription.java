package kit.route.a.lot.common.description;

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
    
    public boolean equals(Object other) {
        if(other == this) {
            return true;
        }
        if(!(other instanceof RouteDescription)) {
            return false;
        }
        RouteDescription comparee = (RouteDescription) other;
        return captions.equals(comparee.captions)
                && descriptions.equals(comparee.descriptions)
                && positionIDs.equals(comparee.positionIDs);
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
