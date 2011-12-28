package kit.route.a.lot.gui;


public class ViewChangedEvent extends java.util.EventObject {
    
    /*describes the vertical movement of an mapObject 
     *in a number between -1 (western movement) and one 1 (eastern movement)
     */ 
    private float longitude;
    
    /*
     * describes the horizontal movement of an mapObject 
     * in a number between -1 (norther movement) and one 1 (south movement)
    */ 
    private float laltitude;
    
    /*
     * describes the factor, the zoomlevel was changed:
     * -1: zoom out
     * 0: no change
     * 1: zoom in
     */
    private int zoomLevelChange;
   
    public ViewChangedEvent(Object source, float latitude, float longitude, int zoomLevelChange) {
        super(source);
        this.laltitude = latitude;
        this.longitude = longitude;
        this.zoomLevelChange = zoomLevelChange;
    }

    
    
    public int getZoomLevelChange() {
        return zoomLevelChange;
    }

    public float getLongitude() {
        return longitude;
    }

    public float getLaltitude() {
        return laltitude;
    }
}
