package kit.route.a.lot.gui;


import kit.route.a.lot.common.Context;
import kit.route.a.lot.common.Coordinates;


public class NavNodeSelectedEvent extends java.util.EventObject{
    /*
     *describes the Coordinates of the selected point
     */ 
    private Coordinates coordiantes;
    
    /*
     * describes the position of the navNode (1 for start, last pos. for end)
     */
    private int position;
    
    /*
     * Context which view has to be changed
     */
    private Context context;

    public NavNodeSelectedEvent(Object source, Coordinates coor, int position, Context context) {
        super(source);
        this.coordiantes = coor;
        this.position = position;
        this.context = context;
    }


    public Coordinates getCoordinates() {
        return coordiantes;
    }
    
    public int getposition() {
        return position;
    }
    
    public Context getContext(){
        return context;
    }
}
