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
import kit.route.a.lot.gui.event.AddNavNodeEvent;
import kit.route.a.lot.gui.event.TextEvent;
import kit.route.a.lot.gui.event.NavNodeNameEvent;
import kit.route.a.lot.io.HeightLoader;
import kit.route.a.lot.io.MapIO;
import kit.route.a.lot.io.OSMLoader;
import kit.route.a.lot.io.RouteIO;
import kit.route.a.lot.io.SRTMLoaderRetarded;
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
        DEFAULT_OSM_MAP = new File("./test/resources/karlsruhe_small_current.osm");
    
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
        // IMPORT HEIGHT DATA
        Util.startTimer();
        importHeightmaps(SRTM_DIRECTORY);
        logger.info("### Loaded heightmaps in " + Util.stopTimer() + " ###");
        
        // LOAD STATE
        loadState();
        logger.info("### Loaded state in " + Util.stopTimer() + " ###");
        
        // LOAD SRAL MAP FROM STATE or IMPORT DEFAULT OSM MAP
        if (state.getLoadedMapFile() != null && state.getLoadedMapFile().exists()) {
            loadMap(state.getLoadedMapFile());
            logger.info("### Loaded map in " + Util.stopTimer() + " ###");
        } else if (DEFAULT_OSM_MAP.exists()) {
            logger.info("import default map...");
            importMap(DEFAULT_OSM_MAP);
            setViewToMapCenter();
            logger.info("### Imported default map in " + Util.stopTimer() + " ###");
        } else {
            logger.warn("no map loaded");
        }      
    
        // SET GUI INITIAL SETTINGS
        addGUIListeners();
        guiHandler.setView(state.getCenterCoordinates());
        guiHandler.setSpeed(state.getSpeed());
        guiHandler.setActive(true);
    }   
    
    private void addGUIListeners() {
        guiHandler.addListener(ADD_NAVNODE, new Listener() {
            public void handleEvent(Event e) {
                if (e instanceof AddNavNodeEvent) {
                    AddNavNodeEvent event = (AddNavNodeEvent) e;
                    addNavNode(event.getPosition(), event.getIndex());
                } else {  
                    NavNodeNameEvent event = (NavNodeNameEvent) e;
                    addNavNode(event.getName(), event.getIndex());
                }
            }            
        });
        guiHandler.addListener(VIEW_CHANGED, new Listener() {
            public void handleEvent(Event e) {
                render(((RenderEvent) e).getContext());
            }   
        });
        guiHandler.addListener(IMPORT_OSM, new Listener() {
            public void handleEvent(Event e) {
                importMap(new File(((TextEvent) e).getText()));  
            } 
        });  
        guiHandler.addListener(OPTIMIZE_ROUTE, new Listener() {
            public void handleEvent(Event e) {
                optimizeRoute();
            } 
        });
        guiHandler.addListener(DELETE_NAVNODE, new Listener() {
            public void handleEvent(Event e) {
                if (e instanceof PositionEvent) {
                    deleteNavNode(((PositionEvent) e).getPosition());
                } else {
                    deleteNavNode(((NumberEvent) e).getNumber());
                }
            }    
        });
        guiHandler.addListener(LOAD_MAP, new Listener() {
            public void handleEvent(Event e) {
                loadMap(new File(SRAL_DIRECTORY + "/" + ((TextEvent) e).getText() + ".sral"));
            }    
        });
        guiHandler.addListener(ADD_FAVORITE, new Listener() {
            public void handleEvent(Event e) {
                AddFavoriteEvent event = (AddFavoriteEvent) e;
                addFavorite(event.getPosition(), event.getName(), event.getDescription());
            }  
        });
        guiHandler.addListener(SAVE_ROUTE, new Listener() {
            public void handleEvent(Event e) {
                saveRoute(((TextEvent) e).getText());
            }    
        });
        guiHandler.addListener(LOAD_ROUTE, new Listener() {
            public void handleEvent(Event e) {
                loadRoute(((TextEvent) e).getText());
            }           
        });
        guiHandler.addListener(EXPORT_ROUTE, new Listener() {
            public void handleEvent(Event e) {
                exportRoute(((TextEvent) e).getText());
            }          
        });
        guiHandler.addListener(DELETE_FAVORITE, new Listener() {
            public void handleEvent(Event e) {
                deleteFavorite(((PositionEvent) e).getPosition());
            }           
        });
        guiHandler.addListener(SET_SPEED, new Listener() {
            public void handleEvent(Event e) {
                setSpeed(((NumberEvent) e).getNumber());
            }
        });
        guiHandler.addListener(POSITION_CLICKED, new Listener() {
            public void handleEvent(Event e) {
                passElementType(((PositionEvent) e).getPosition());
            }       
        });
        guiHandler.addListener(SET_HEIGHT_MALUS, new Listener() {
            public void handleEvent(Event e) {
                setHeightMalus(((NumberEvent) e).getNumber());
            }           
        });
        guiHandler.addListener(SET_HIGHWAY_MALUS, new Listener() {
            public void handleEvent(Event e) {
                setHighwayMalus(((NumberEvent) e).getNumber());
            }        
        });
        guiHandler.addListener(CLOSE_APPLICATION, new Listener() {
            public void handleEvent(Event e) {
                prepareForShutdown();
            } 
        });
        guiHandler.addListener(SHOW_POI_DESCRIPTION, new Listener() {
            public void handleEvent(Event e) {
                passDescription(((PositionEvent) e).getPosition());
            } 
        });
        guiHandler.addListener(SWITCH_MAP_MODE, new Listener() {
            public void handleEvent(Event e) {
                setMapMode(!(state.getActiveRenderer() instanceof Renderer3D));
            }         
        });
        guiHandler.addListener(LIST_SEARCH_COMPLETIONS, new Listener() {
            public void handleEvent(Event e) {
                passSearchCompletion(((TextEvent) e).getText());
            }      
        });
        guiHandler.addListener(SHOW_NAVNODE_DESCRIPTION, new Listener() {
            public void handleEvent(Event e) {
                getNavNodeFromText(((TextEvent) e).getText());
            }  
        });
        guiHandler.addListener(DELETE_IMPORTED_MAP, new Listener() {
            public void handleEvent(Event e) {
                deleteMap(((TextEvent) e).getText());
            }         
        });
        guiHandler.addListener(LIST_IMPORTED_MAPS, new Listener() {
            public void handleEvent(Event e) {
                updateImportedMapsList();
            }      
        });
    }
    
    private static void loadState() {
        File stateFile = new File("./state.state");
        if (stateFile.exists()) {   
            logger.info("load state file..."); 
            try { 
                StateIO.loadState(stateFile); 
            } catch (IOException e) {
                logger.error("state loading: Read error occurred.");
                e.printStackTrace();
            } 
        }
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
            guiHandler.setView(state.getCenterCoordinates());
            updateImportedMapsList();
        }    
    }
  
    private void loadMap(File file) {
        File mapFile = file;
        if(!mapFile.exists()) {
            logger.error("map File doesn't exist");
        } else {
            state.resetMap();
            try {
                MapIO.loadMap(mapFile);
                state.setLoadedMapFile(mapFile);
            } catch (IOException e) {
                logger.fatal("loadMap: IO Exception in MapIO");
            }
            setViewToMapCenter(); 
            guiHandler.setView(state.getCenterCoordinates());
        }
    }

    private void deleteMap(String path){
        File file = new File(path);
        if(file.exists()) {
            file.delete();
        }
        updateImportedMapsList();
    }
           
    private void importHeightmaps(File directory) {
        HeightLoader loader = new SRTMLoaderRetarded();
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
    }

    private void exportRoute(String path) {
        String kmlPath = path;
        if (!kmlPath.endsWith(".kml")) {
            kmlPath += ".kml";
        }
        File routeFile = new File(kmlPath);
        if (state.getCurrentRoute().size() != 0) {
            RouteIO.exportCurrentRouteToKML(routeFile);
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
        guiHandler.updateNavPointsList(state.getNavigationNodes());
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
        guiHandler.updateNavPointsList(state.getNavigationNodes());
        calculateRoute();
        // for (int i = 0; i < state.getNavigationNodes().size(); i++) {
        //     guiHandler.showNavNodeDescription(state.getNavigationNodes().get(i).getName(), i);    // TODO error in GUI
        // }
    }

    private void getNavNodeFromText(String str) {
        Selection sel = state.getLoadedMapInfo().select(str);
        if (sel != null) {
            state.getNavigationNodes().add(state.getNavigationNodes().size() - 1, sel);
            guiHandler.updateNavPointsList(state.getNavigationNodes());
            calculateRoute();
        }
    }
    
    private void deleteNavNode(int pos) {
        if (pos < state.getNavigationNodes().size()) {
            state.getNavigationNodes().remove(pos);
            state.setCurrentRoute(new ArrayList<Integer>());
            guiHandler.updateNavPointsList(state.getNavigationNodes());
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
                guiHandler.updateNavPointsList(state.getNavigationNodes());
                calculateRoute();
            }
        }
    }

    private void optimizeRoute() {  
        Router.optimizeRoute(state.getNavigationNodes());
        guiHandler.updateNavPointsList(state.getNavigationNodes());
        calculateRoute();
    }
    
    private void calculateRoute() {
        if (state.getNavigationNodes().size() >= 2) {
            state.setCurrentRoute(Router.calculateRoute(state.getNavigationNodes()));
            int duration = ComplexInfoSupplier.getDuration(state.getCurrentRoute(), state.getSpeed()); 
            int length = ComplexInfoSupplier.getLength(state.getCurrentRoute());
            guiHandler.showRouteValues(length, duration);
            guiHandler.updateGUI();
        }
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
        if(SRAL_DIRECTORY.isDirectory()) {
            String files[] = SRAL_DIRECTORY.list();
            for(String file : files) {
                if (file.endsWith(".sral")) {
                    maps.add(Util.removeExtension(file));
                }
            }
        }
        guiHandler.updateMapList(maps);
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
        Coordinates center = new Coordinates();
        center.setLatitude((upLeft.getLatitude() + bottomRight.getLatitude()) / 2);
        center.setLongitude((upLeft.getLongitude() + bottomRight.getLongitude()) / 2);
        state.setCenterCoordinates(center);
    }
    
    private void render(Context context) {
        state.setDetailLevel(context.getZoomlevel());
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
            System.setProperty("java.library.path", System.getProperty("java.library.path")
                        + File.pathSeparator + dir);
        } catch (IllegalAccessException e) {
                throw new IOException("Failed to get permissions to set library path.");
        } catch (NoSuchFieldException e) {
                throw new IOException("Failed to get field handle to set library path.");
        }
    }

}
