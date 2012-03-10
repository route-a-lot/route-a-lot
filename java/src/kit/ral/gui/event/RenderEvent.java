package kit.route.a.lot.gui.event;

import kit.route.a.lot.common.Context;

public class RenderEvent extends Event { 
    
    private Context context;
   
    public RenderEvent(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }
}
