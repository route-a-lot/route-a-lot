package kit.route.a.lot.common;

import kit.route.a.lot.gui.event.GeneralEvent;

/**
 * Interface for our own listener
 */
public interface Listener {
    public static final int
        IMPORT_OSM = 0, IMPORT_HEIGHTMAP = 1,
        LOAD_MAP = 2, LOAD_ROUTE = 3, SAVE_ROUTE = 4, EXPORT_ROUTE = 5,  
        POSITION_CLICKED = 6, OPTIMIZE_ROUTE = 7,
        SHOW_POI_DESCRIPTION = 8, SHOW_NAVNODE_DESCRIPTION = 9,            
        ADD_FAVORITE = 10, DELETE_FAVORITE = 11, ADD_NAVNODE = 12, DELETE_NAVNODE = 13,
        LIST_SEARCH_COMPLETIONS = 14, SET_SPEED = 15,      
        VIEW_CHANGED = 16, SWITCH_MAP_MODE = 17,
        SET_HIGHWAY_MALUS = 18, SET_HEIGHT_MALUS = 19,
        LIST_IMPORTED_MAPS = 20, DELETE_IMPORTED_MAP = 21,
        CLOSE_APPLICATION = 22;
    public static final int TYPE_COUNT = 23;
    public void handleEvent(GeneralEvent event);   
}
