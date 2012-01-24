package kit.route.a.lot.gui.event;

import kit.route.a.lot.common.Context;


public class ViewChangedEvent extends java.util.EventObject {
    
    private static final long serialVersionUID = 1L;

    /*
     * the context which view has changed
     */ 
    private Context context;
    
    /*
     * describes the factor, the zoomlevel was changed:
     * -1: zoom out
     * 0: no change
     * 1: zoom in
     */
    private int zoomLevel;
   
    public ViewChangedEvent(Context context, int zoomLevel) {
        super(null);
        this.context = context;
        this.zoomLevel = zoomLevel;
    }

    
    
    public int getZoomLevel() {
        return zoomLevel;
    }

    public Context getContext() {
        return context;
    }
}
