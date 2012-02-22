package kit.route.a.lot.controller;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import kit.route.a.lot.common.Context;
import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Listener;
import kit.route.a.lot.common.OSMType;
import kit.route.a.lot.common.POIDescription;
import kit.route.a.lot.common.Projection;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.common.Util;
import kit.route.a.lot.gui.GUIHandler;
import kit.route.a.lot.gui.event.AddFavoriteEvent;
import kit.route.a.lot.gui.event.RenderEvent;
import kit.route.a.lot.gui.event.Event;
import kit.route.a.lot.gui.event.NumberEvent;
import kit.route.a.lot.gui.event.PositionEvent;
import kit.route.a.lot.gui.event.PositionNumberEvent;
import kit.route.a.lot.gui.event.TextEvent;
import kit.route.a.lot.gui.event.NavNodeNameEvent;
import kit.route.a.lot.io.HeightLoader;
import kit.route.a.lot.io.MapIO;
import kit.route.a.lot.io.OSMLoader;
import kit.route.a.lot.io.RouteIO;
import kit.route.a.lot.io.SRTMLoaderDeferred;
import kit.route.a.lot.io.StateIO;
import kit.route.a.lot.map.Node;
import kit.route.a.lot.map.infosupply.ComplexInfoSupplier;
import kit.route.a.lot.map.rendering.Renderer;
import kit.route.a.lot.map.rendering.Renderer3D;
import kit.route.a.lot.routing.Precalculator;
import kit.route.a.lot.routing.Router;
import static kit.route.a.lot.common.Listener.*;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Controller {
    // Same definition as in Map:
    private static final int FREEMAPSPACE = 0, POI = 1, FAVORITE = 2, NAVNODE = 3;

    private static final File
        SRAL_DIRECTORY = new File("./sral"),
        SRTM_DIRECTORY = new File("./srtm"),
        STATE_FILE = new File("./state.state"),
        DEFAULT_OSM_MAP = new File("./test/resources/karlsruhe_small_current.osm");
    
    private static final String SRAL_EXT = ".sral";
    
    private GUIHandler guiHandler = new GUIHandler();
    private State state = State.getInstance();
    private static Logger logger = Logger.getLogger(Controller.class);
    
    public static void main(String[] args) {
        // ENVIRONMENT SETUP
        PropertyConfigurator.configure("config/log4j.conf");
        String arch = System.getProperty("os.arch").toLowerCase();
        int archbits = (arch.equals("i386") || arch.equals("x86")) ? 32 : 64;
        try {
            addDirectoryToLibraryPath("./lib/lib" + archbits);
        } catch (IOException e) {
            logger.error("Could not load library path for " + arch + ".");
            return;
        }       
        // SYSTEM SETUP
        new Controller();
    }
    
    private Controller() {
        state.setProgress(0);
        
        // REGISTER LISTENERS
        addGUIListeners();
        state.setProgress(30);
        
        // IMPORT HEIGHT DATA   
        Util.startTimer();
        importHeightmaps(SRTM_DIRECTORY);
        logger.info("Heightmaps loaded: " + Util.stopTimer());
        state.setProgress(60); 
        
        // LOAD STATE
        Util.startTimer();
        loadState();
        logger.info("State loaded: " + Util.stopTimer());
        state.setProgress(90);  
        
        // IMPORT DEFAULT OSM MAP
        if (state.getLoadedMapFile() == null) {
            if ((DEFAULT_OSM_MAP != null) && DEFAULT_OSM_MAP.exists()) {
                importMap(DEFAULT_OSM_MAP);
                logger.info("Imported default map: " + Util.stopTimer());
            } else {
                logger.warn("No map loaded");
            }      
        }
        
        state.setProgress(100);
    }   
    
    private void addGUIListeners() {
        Listener.addListener(ADD_NAVNODE, new Listener() {
            public void handleEvent(Event e) {
                if (e instanceof PositionNumberEvent) {
                    PositionNumberEvent event = (PositionNumberEvent) e;
                    addNavNode(event.getPosition(), event.getNumber());
                } else {  
                    NavNodeNameEvent event = (NavNodeNameEvent) e;
                    addNavNode(event.getName(), event.getIndex());
                }
            }            
        });
        Listener.addListener(VIEW_CHANGED, new Listener() {
            public void handleEvent(Event e) {
                PositionNumberEvent event = (PositionNumberEvent) e;
                state.setCenterCoordinates(event.getPosition());
                state.setDetailLevel(event.getNumber());   
            }
        });
        Listener.addListener(RENDER, new Listener() {
            public void handleEvent(Event e) {
                render(((RenderEvent) e).getContext());
            }   
        });
        Listener.addListener(IMPORT_OSM, new Listener() {
            public void handleEvent(Event e) {
                importMap(new File(((TextEvent) e).getText()));  
            } 
        });  
        Listener.addListener(OPTIMIZE_ROUTE, new Listener() {
            public void handleEvent(Event e) {
                optimizeRoute();
            } 
        });
        Listener.addListener(DELETE_NAVNODE, new Listener() {
            public void handleEvent(Event e) {
                if (e instanceof PositionEvent) {
                    deleteNavNode(((PositionEvent) e).getPosition());
                } else {
                    deleteNavNode(((NumberEvent) e).getNumber());
                }
            }    
        });
        Listener.addListener(LOAD_MAP, new Listener() {
            public void handleEvent(Event e) {
                String text = ((TextEvent) e).getText();
                loadMap((text.length() == 0) ? null
                        : new File(SRAL_DIRECTORY + "/" + text + SRAL_EXT));
                setViewToMapCenter();
            }    
        });
        Listener.addListener(ADD_FAVORITE, new Listener() {
            public void handleEvent(Event e) {
                AddFavoriteEvent event = (AddFavoriteEvent) e;
                addFavorite(event.getPosition(), event.getName(), event.getDescription());
            }  
        });
        Listener.addListener(SAVE_ROUTE, new Listener() {
            public void handleEvent(Event e) {
                saveRoute(((TextEvent) e).getText());
            }    
        });
        Listener.addListener(LOAD_ROUTE, new Listener() {
            public void handleEvent(Event e) {
                loadRoute(((TextEvent) e).getText());
            }           
        });
        Listener.addListener(EXPORT_ROUTE, new Listener() {
            public void handleEvent(Event e) {
                exportRoute(new File(((TextEvent) e).getText()));
            }          
        });
        Listener.addListener(DELETE_FAVORITE, new Listener() {
            public void handleEvent(Event e) {
                deleteFavorite(((PositionEvent) e).getPosition());
            }           
        });
        Listener.addListener(SET_SPEED, new Listener() {
            public void handleEvent(Event e) {
                setSpeed(((NumberEvent) e).getNumber());
            }
        });
        Listener.addListener(POSITION_CLICKED, new Listener() {
            public void handleEvent(Event e) {
                passElementType(((PositionEvent) e).getPosition());
            }       
        });
        Listener.addListener(SET_HEIGHT_MALUS, new Listener() {
            public void handleEvent(Event e) {
                setHeightMalus(((NumberEvent) e).getNumber());
            }           
        });
        Listener.addListener(SET_HIGHWAY_MALUS, new Listener() {
            public void handleEvent(Event e) {
                setHighwayMalus(((NumberEvent) e).getNumber());
            }        
        });
        Listener.addListener(CLOSE_APPLICATION, new Listener() {
            public void handleEvent(Event e) {
                prepareForShutdown();
            } 
        });
        Listener.addListener(SHOW_POI_DESCRIPTION, new Listener() {
            public void handleEvent(Event e) {
                passDescription(((PositionEvent) e).getPosition());
            } 
        });
        Listener.addListener(SWITCH_MAP_MODE, new Listener() {
            public void handleEvent(Event e) {
                setMapMode(!(state.getActiveRenderer() instanceof Renderer3D));
            }         
        });
        Listener.addListener(LIST_SEARCH_COMPLETIONS, new Listener() {
            public void handleEvent(Event e) {
                passSearchCompletion(((TextEvent) e).getText());
            }      
        });
        Listener.addListener(SHOW_NAVNODE_DESCRIPTION, new Listener() {
            public void handleEvent(Event e) {
                getNavNodeFromText(((TextEvent) e).getText());
            }  
        });
        Listener.addListener(DELETE_IMPORTED_MAP, new Listener() {
            public void handleEvent(Event e) {
                deleteMap(new File(SRAL_DIRECTORY + "/" + ((TextEvent) e).getText() + SRAL_EXT));
            }         
        });
        Listener.addListener(LIST_IMPORTED_MAPS, new Listener() {
            public void handleEvent(Event e) {
                updateImportedMapsList();
            }      
        });
        Listener.addListener(MAP_RESIZED, new Listener() {
            public void handleEvent(Event e) {
                state.getActiveRenderer().resetCache();
            }      
        });
    }
    
    private void loadState() {
        State state = State.getInstance(); 
        if (STATE_FILE.exists()) {   
            try { 
                StateIO.loadState(STATE_FILE); 
                //guiHandler.setMapMode(state.getActiveRenderer() instanceof Renderer3D);              
            } catch (IOException e) {
                logger.error("State loading: Read error occurred.");
                e.printStackTrace();
            } 
            // LOAD SRAL MAP FROM STATE
            if ((state.getLoadedMapFile() != null) && state.getLoadedMapFile().exists()) {
                logger.info("Loading map file from state");
                loadMap(state.getLoadedMapFile());
            }  
        }   
        guiHandler.setSpeed(state.getSpeed());  
        guiHandler.setView(state.getCenterCoordinates(), state.getDetailLevel());       
    }
    
    private void importMap(File osmFile) {
        if(!osmFile.exists()) {
            logger.error("osm File doesn't exist");
        } else {
            state.resetMap();
            new OSMLoader(State.getInstance()).importMap(osmFile);
            Precalculator.precalculate();
            state.getLoadedMapInfo().compactifyDatastructures();
            state.setLoadedMapFile(new File(SRAL_DIRECTORY + "/" + Util.removeExtension(osmFile.getName())
                    + " (" + state.getHeightMalus() + ", " + state.getHighwayMalus() + ").sral"));    
            try {
                MapIO.saveMap(state.getLoadedMapFile());
            } catch (IOException e) {
                logger.error("Could not save imported map to file.");
            }
            setViewToMapCenter();
            updateImportedMapsList();
        }    
    }
  
    private void loadMap(File file) {
        if (file == null) {
            state.resetMap();
            guiHandler.updateGUI();
        } else if (!file.exists()) {
            logger.error("Map file doesn't exist.");
        } else {
            state.resetMap();
            try {
                MapIO.loadMap(file);
                state.setLoadedMapFile(file);
            } catch (IOException e) {
                logger.error("Map could not be loaded.");
            }
        }
    }

    private void deleteMap(File file){
        if(file.exists()) {
            file.delete();
        }
        updateImportedMapsList();
    }
           
    private void importHeightmaps(File directory) {
        HeightLoader loader = new SRTMLoaderDeferred();
        loader.load(directory);
    }
    
    
    private void saveRoute(String path) {
        File routeFile = new File(path);
        if (state.getCurrentRoute().size() != 0) {
            try {
                RouteIO.saveCurrentRoute(routeFile);
            } catch (IOException e) {
                logger.error("Could not save route to file '" + path + "'.");
            }
        }
    }

    private void loadRoute(String path) {
        File routeFile = new File(path);
        if (!routeFile.exists()) {
            logger.error("RouteFile existiert nicht");
        } else {
            try {
                RouteIO.loadCurrentRoute(routeFile);
            } catch (IOException e) {
                logger.error("Could not load route from file '" + path + "'.");
            }
        }
        calculateRoute();
        guiHandler.updateNavNodes(state.getNavigationNodes());
    }

    private void exportRoute(File file) {
        if (state.getCurrentRoute().size() != 0) {
            RouteIO.exportCurrentRouteToKML(file);
        }
    }
      
    private void addNavNode(Coordinates pos, int position) {
        Selection newSel = state.getLoadedMapInfo().select(pos);
        if (newSel != null) {
            if (position == 0 && state.getNavigationNodes().size() > 1) {
                state.getNavigationNodes().remove(0);
                state.getNavigationNodes().add(0, newSel);
            } else if (position == state.getNavigationNodes().size() && state.getNavigationNodes().size() > 1) {
                state.getNavigationNodes().remove(state.getNavigationNodes().remove(state.getNavigationNodes().size() - 1));
                state.getNavigationNodes().add(newSel);
            } else {
                state.getNavigationNodes().add(position, newSel);
            }
        }
        guiHandler.updateNavNodes(state.getNavigationNodes());
        calculateRoute();
        // for (int i = 0; i < state.getNavigationNodes().size(); i++) {
        //     guiHandler.showNavNodeDescription(state.getNavigationNodes().get(i).getName(), i);    // TODO error in GUI
        // }
    }
    
    private void addNavNode(String name, int position) {
        Selection newSel = state.getLoadedMapInfo().select(name);
        if (newSel != null) {
            if (position == 0 && state.getNavigationNodes().size() > 1) {
                state.getNavigationNodes().remove(0);
                state.getNavigationNodes().add(0, newSel);
            } else if (position == state.getNavigationNodes().size() && state.getNavigationNodes().size() > 1) {
                state.getNavigationNodes().remove(state.getNavigationNodes().remove(state.getNavigationNodes().size() - 1));
                state.getNavigationNodes().add(newSel);
            } else {
                state.getNavigationNodes().add(position, newSel);
            }
        }
        guiHandler.updateNavNodes(state.getNavigationNodes());
        calculateRoute();
        // for (int i = 0; i < state.getNavigationNodes().size(); i++) {
        //     guiHandler.showNavNodeDescription(state.getNavigationNodes().get(i).getName(), i);    // TODO error in GUI
        // }
    }

    private void getNavNodeFromText(String str) {
        Selection sel = state.getLoadedMapInfo().select(str);
        if (sel != null) {
            state.getNavigationNodes().add(state.getNavigationNodes().size() - 1, sel);
            guiHandler.updateNavNodes(state.getNavigationNodes());
            calculateRoute();
        }
    }
    
    private void deleteNavNode(int pos) {
        if (pos < state.getNavigationNodes().size()) {
            state.getNavigationNodes().remove(pos);
            state.setCurrentRoute(new ArrayList<Integer>());
            guiHandler.updateNavNodes(state.getNavigationNodes());
            calculateRoute();
        }
    }
    
    private void deleteNavNode(Coordinates pos) {
        int radius = Projection.getZoomFactor(state.getDetailLevel()) * state.getClickRadius();
        for (int i = 0; i < state.getNavigationNodes().size(); i++) {
            Coordinates navNodePos = state.getNavigationNodes().get(i).getPosition();
            if (Coordinates.getDistance(navNodePos, pos) < radius) {
                state.getNavigationNodes().remove(i);
                state.getCurrentRoute().clear();
                guiHandler.updateNavNodes(state.getNavigationNodes());
                calculateRoute();
            }
        }
    }

    private void optimizeRoute() {  
        Router.optimizeRoute(state.getNavigationNodes());
        guiHandler.updateNavNodes(state.getNavigationNodes());
        calculateRoute();
    }
    
    private void calculateRoute() {
        double startTime = System.currentTimeMillis();
        if (state.getNavigationNodes().size() >= 2) {
            state.setCurrentRoute(Router.calculateRoute(state.getNavigationNodes()));
            int duration = ComplexInfoSupplier.getDuration(state.getCurrentRoute(), state.getSpeed()); 
            int length = ComplexInfoSupplier.getLength(state.getCurrentRoute());
            guiHandler.showRouteValues(length, duration);
            guiHandler.updateGUI();
        }
        double duration = System.currentTimeMillis() - startTime;
        logger.info("Calculated route in " + (duration / 1000) + "s");
    }

    
    private void addFavorite(Coordinates pos, String name, String description) {  
        state.getLoadedMapInfo().addFavorite(pos, new POIDescription(name, OSMType.FAVOURITE, description));
        guiHandler.updateGUI();
    }

    private void deleteFavorite(Coordinates pos) {
        state.getLoadedMapInfo().deleteFavorite(pos, state.getDetailLevel(), state.getClickRadius());
        guiHandler.updateGUI();
    }


    private void passElementType(Coordinates pos) {
        float adaptedRadius = (Projection.getZoomFactor(state.getDetailLevel())) * state.getClickRadius();
        Coordinates topLeft = new Coordinates(pos.getLatitude() - adaptedRadius,
                pos.getLongitude() - adaptedRadius);       
        Coordinates bottomRight = new Coordinates(pos.getLatitude() + adaptedRadius,
                pos.getLongitude() + adaptedRadius);
        for (Selection navNode: state.getNavigationNodes()) {
            Node node = new Node(navNode.getPosition());
            if (node.isInBounds(topLeft, bottomRight)) {
                guiHandler.passElementType(NAVNODE);
                return;
            }
        }    
        if (state.getLoadedMapInfo().getPOIDescription(pos,
                state.getClickRadius(), state.getDetailLevel()) != null) {
            guiHandler.passElementType(POI);
            return;
        } 
        if(state.getLoadedMapInfo().getFavoriteDescription(pos,
                state.getDetailLevel(), state.getClickRadius()) != null) {
            guiHandler.passElementType(FAVORITE);
            return;
        }
        guiHandler.passElementType(FREEMAPSPACE);
    }
    
    private void passDescription(Coordinates pos) {   
        POIDescription description = state.getLoadedMapInfo()
            .getFavoriteDescription(pos, state.getDetailLevel(), state.getClickRadius());
        if (description == null) {
            description = state.getLoadedMapInfo()
                .getPOIDescription(pos, state.getClickRadius(), state.getDetailLevel());
        }
        if (description != null) {
            guiHandler.passDescription(description);
        }
    }

    private void passSearchCompletion(String str) {
        guiHandler.showSearchCompletion(state.getLoadedMapInfo().suggestCompletions(str));
    }
    
    private void passTextRoute() {   //TODO
        if (state.getCurrentRoute().size() != 0) {
            ComplexInfoSupplier.getRouteDescription(state.getCurrentRoute());
        }
    }
  
    private void updateImportedMapsList() {
        List<String> maps = new ArrayList<String>();
        int activeMapIndex = -1;
        if(SRAL_DIRECTORY.isDirectory()) {
            File[] files = SRAL_DIRECTORY.listFiles();
            int count = 0;
            for(File file : files) {
                if (file.getName().endsWith(".sral")) {
                    maps.add(Util.removeExtension(file.getName()));
                    if(file.equals(state.getLoadedMapFile())) {
                        activeMapIndex = count;
                    }
                    count++;
                }                
            }
        }
        guiHandler.updateMapList(maps, activeMapIndex);
    }
    
    private void setSpeed(int speed) {
        if(speed >= 0) {
            state.setSpeed(speed);
            guiHandler.showRouteValues(ComplexInfoSupplier.getLength(state.getCurrentRoute()),
                    ComplexInfoSupplier.getDuration(state.getCurrentRoute(), speed));
        }
    }
    
    private void setHeightMalus(int newMalus) {
        if (newMalus >= 0) {
            state.setHeightMalus(newMalus);
        }
    }

    private void setHighwayMalus(int newMalus) {
        if (newMalus >= 0) {
            state.setHighwayMalus(newMalus);
        }
    }
    
    private void setViewToMapCenter() {
        if (state.getLoadedMapInfo() == null) {
            return;
        }
        Coordinates upLeft = new Coordinates();
        Coordinates bottomRight = new Coordinates();
        state.getLoadedMapInfo().getBounds(upLeft, bottomRight);
        state.setCenterCoordinates(Coordinates.interpolate(upLeft, bottomRight, 0.5f));
        guiHandler.setView(state.getCenterCoordinates(), state.getDetailLevel());
    }
    
    private void render(Context context) {
        state.getActiveRenderer().render(context); 
    }
    
    private void setMapMode(boolean render3D) {
        state.setActiveRenderer((render3D) ? new Renderer3D() : new Renderer());
        guiHandler.setMapMode(render3D);
    }
    
    private void prepareForShutdown() {
        File stateFile = new File("./state.state");
        try {
            StateIO.saveState(stateFile);
        } catch (IOException e) {
            logger.fatal("IO exception in StateIO");
        }
    }   
    
    /**
     * Dynamically specifies a Java library path location at runtime,
     * thus enabling the fitting native libraries to be chosen at startup.
     * @param s the library path to be added
     * @throws IOException on any access error
     */
    private static void addDirectoryToLibraryPath(String dir) throws IOException {
        try {
            Field field = ClassLoader.class.getDeclaredField("usr_paths");
            field.setAccessible(true);
            String[] paths = (String[]) field.get(null);
            for (String path: paths) {
                if (dir.equals(path)) {
                    return;
                }
            }
            String[] tmp = new String[paths.length + 1];
            System.arraycopy(paths, 0, tmp, 0, paths.length);
            tmp[paths.length] = dir;
            field.set(null, tmp);
        } catch (IllegalAccessException e) {
                throw new IOException("Failed to get permissions to set library path.");
        } catch (NoSuchFieldException e) {
                throw new IOException("Failed to get field handle to set library path.");
        }
    }

}
