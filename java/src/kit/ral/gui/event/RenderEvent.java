package kit.ral.gui.event;

import kit.ral.common.Context;

public class RenderEvent extends Event { 
    
    private Context context;
   
    public RenderEvent(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }
}
