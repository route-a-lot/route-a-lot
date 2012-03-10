package kit.ral.common.event;

import java.util.ArrayList;


/**
 * Interface for our own listener
 */
@SuppressWarnings("unchecked")
public abstract class Listener {
    
    public static final int
        IMPORT_OSM = 0, IMPORT_HEIGHTMAP = 1,
        LOAD_MAP = 2, EXPORT_ROUTE = 3,
        LOAD_ROUTE = 4, SAVE_ROUTE = 5, 
        POSITION_CLICKED = 6, OPTIMIZE_ROUTE = 7,
        SHOW_POI_DESCRIPTION = 8, NEW_ROUTE = 9,       
        ADD_FAVORITE = 10, DELETE_FAVORITE = 11,
        ADD_NAVNODE = 12, DELETE_NAVNODE = 13,
        SET_SPEED = 15,      
        VIEW_CHANGED = 16, MAP_RESIZED = 17, TILE_RENDERED = 18,
        SWITCH_MAP_MODE = 19, RENDER = 20,
        SET_HIGHWAY_MALUS = 21, SET_HEIGHT_MALUS = 22,
        LIST_IMPORTED_MAPS = 23, DELETE_IMPORTED_MAP = 24,
        PROGRESS = 25, CANCEL_OPERATION = 26, CLOSE_APPLICATION = 27,
        LIST_SEARCH_COMPLETIONS = 28, SWITCH_NAV_NODES = 29;
    
    private static final int TYPE_COUNT = 30;
    
    private static ArrayList<Listener>[] lists;
    static {
        lists = (ArrayList<Listener>[]) new ArrayList[TYPE_COUNT];
        for (int i = 0; i < lists.length; i++) {
            lists[i] = new ArrayList<Listener>(1);
        }
    }
    
    public static void addListener(int eventType, Listener listener) {
        lists[eventType].add(listener);
    }
    
    public static void fireEvent(int eventType, Event event) {
        for (Listener listener: lists[eventType]) {
            listener.handleEvent(event);
        }  
    }
    
    abstract public void handleEvent(Event event);  
}
