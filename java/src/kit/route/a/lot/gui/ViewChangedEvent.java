package kit.route.a.lot.gui;

import kit.route.a.lot.common.Context;


public class ViewChangedEvent extends java.util.EventObject {
    
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
    private int zoomLevelChange;
   
    public ViewChangedEvent(Object source, Context context, int zoomLevelChange) {
        super(source);
        this.context = context;
        this.zoomLevelChange = zoomLevelChange;
    }

    
    
    public int getZoomLevelChange() {
        return zoomLevelChange;
    }

    public Context getContext() {
        return context;
    }
}
