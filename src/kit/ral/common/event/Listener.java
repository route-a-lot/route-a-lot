
/**
Copyright (c) 2012, Matthias Grundmann, Malte Wolff, Jan Jacob, Josua Stabenow
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
        * Redistributions of source code must retain the above copyright
          notice, this list of conditions and the following disclaimer.
        * Redistributions in binary form must reproduce the above copyright
          notice, this list of conditions and the following disclaimer in the
          documentation and/or other materials provided with the distribution.
        * The names of the contributors may not be used to endorse or promote products
          derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
**/

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
