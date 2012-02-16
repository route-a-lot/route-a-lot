package kit.route.a.lot.gui.event;

import kit.route.a.lot.common.Context;

public class ChangeViewEvent extends GeneralEvent { 
    
    /*
     * the context which view has changed
     */ 
    private Context context;
   
    public ChangeViewEvent(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }
}
