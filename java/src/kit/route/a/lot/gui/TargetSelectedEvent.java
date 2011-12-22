package kit.route.a.lot.gui;


public class TargetSelectedEvent extends java.util.EventObject{

    /*
     *describes the vertical position of the selected Target (number between 0 and 1 as percent indicator) 
     */ 
    private float longitude;
    
    /*
     * describes the horizontal position of the selected Target (number between 0 and 1 as percent indicator) 
    */ 
    private float laltitude;
    

    public TargetSelectedEvent(Object source, float latitude, float longitude, int zoomLevelChange) {
        super(source);
        this.laltitude = latitude;
        this.longitude = longitude;
    }


    public float getLongitude() {
        return longitude;
    }

    public float getLaltitude() {
        return laltitude;
    }

    
}
