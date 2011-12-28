package kit.route.a.lot.gui;


public class NavNodeSelectedEvent extends java.util.EventObject{
    /*
     *describes the vertical position of the selected Target (number between 0 and 1 as percent indicator) 
     */ 
    private float longitude;
    
    /*
     * describes the horizontal position of the selected Target (number between 0 and 1 as percent indicator) 
    */ 
    private float laltitude;
    
    /*
     * describes the position of the navNode (1 for start, last pos. for end)
     */
    private int position;
    

    public NavNodeSelectedEvent(Object source, float latitude, float longitude, int zoomLevelChange, int position) {
        super(source);
        this.laltitude = latitude;
        this.longitude = longitude;
        this.position = position;
    }


    public float getLongitude() {
        return longitude;
    }

    public float getLaltitude() {
        return laltitude;
    }
    
    public int getposition() {
        return position;
    }
}
