package kit.route.a.lot.controller;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import kit.route.a.lot.common.Context;
import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.OSMType;
import kit.route.a.lot.common.POIDescription;
import kit.route.a.lot.common.Projection;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.common.Util;
import kit.route.a.lot.controller.listener.AddFavoriteListener;
import kit.route.a.lot.controller.listener.ChangeViewListener;
import kit.route.a.lot.controller.listener.ClickPositionListener;
import kit.route.a.lot.controller.listener.CloseListener;
import kit.route.a.lot.controller.listener.DeleteFavoriteListener;
import kit.route.a.lot.controller.listener.DeleteMapListener;
import kit.route.a.lot.controller.listener.DeleteNavNodeListener;
import kit.route.a.lot.controller.listener.ExportRouteListener;
import kit.route.a.lot.controller.listener.GeneralListener;
import kit.route.a.lot.controller.listener.ShowPOIDescriptionListener;
import kit.route.a.lot.controller.listener.HeightMalusListener;
import kit.route.a.lot.controller.listener.HighwayMalusListener;
import kit.route.a.lot.controller.listener.ImportOsmFileListener;
import kit.route.a.lot.controller.listener.LoadMapListener;
import kit.route.a.lot.controller.listener.LoadRouteListener;
import kit.route.a.lot.controller.listener.OptimizeRouteListener;
import kit.route.a.lot.controller.listener.SaveRouteListner;
import kit.route.a.lot.controller.listener.SearchNameListener;
import kit.route.a.lot.controller.listener.AddNavNodeListener;
import kit.route.a.lot.controller.listener.SpeedListener;
import kit.route.a.lot.controller.listener.SuggestionListener;
import kit.route.a.lot.gui.GUIHandler;
import kit.route.a.lot.gui.event.GeneralEvent;
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

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Controller {
    // Same definition as in Map / Listeners (please synchronize when changing):
    private static final int FREEMAPSPACE = 0, POI = 1, FAVORITE = 2, NAVNODE = 3;
    private static final int
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
    
    private static final File
        SRAL_DIRECTORY = new File("./sral"),
        SRTM_DIRECTORY = new File("./srtm/"),
        DEFAULT_OSM_MAP = new File("./test/resources/karlsruhe_small_current.osm");
    
    private Renderer renderer = new Renderer();
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

        // SETUP GUI LISTENERS
        guiHandler.addListener(ADD_NAVNODE, new AddNavNodeListener(this));
        guiHandler.addListener(VIEW_CHANGED, new ChangeViewListener(this));
        guiHandler.addListener(IMPORT_OSM, new ImportOsmFileListener(this));  
        guiHandler.addListener(OPTIMIZE_ROUTE, new OptimizeRouteListener(this));
        guiHandler.addListener(DELETE_NAVNODE, new DeleteNavNodeListener(this));
        guiHandler.addListener(LOAD_MAP, new LoadMapListener(this));
        guiHandler.addListener(ADD_FAVORITE, new AddFavoriteListener(this));
        guiHandler.addListener(SAVE_ROUTE, new SaveRouteListner(this));
        guiHandler.addListener(LOAD_ROUTE, new LoadRouteListener(this));
        guiHandler.addListener(EXPORT_ROUTE, new ExportRouteListener(this));
        guiHandler.addListener(DELETE_FAVORITE, new DeleteFavoriteListener(this));
        guiHandler.addListener(SET_SPEED, new SpeedListener(this));
        guiHandler.addListener(POSITION_CLICKED, new ClickPositionListener(this));
        guiHandler.addListener(SET_HEIGHT_MALUS, new HeightMalusListener(this));
        guiHandler.addListener(SET_HIGHWAY_MALUS, new HighwayMalusListener(this));
        guiHandler.addListener(CLOSE_APPLICATION, new CloseListener(this));
        guiHandler.addListener(SHOW_POI_DESCRIPTION, new ShowPOIDescriptionListener(this));
        guiHandler.addListener(SWITCH_MAP_MODE, new GeneralListener() {
            @Override
            public void handleEvent(GeneralEvent event) {
                switchMapMode();
            }         
        });
        guiHandler.addListener(LIST_SEARCH_COMPLETIONS, new SuggestionListener(this));
        guiHandler.addListener(SHOW_NAVNODE_DESCRIPTION, new SearchNameListener(this));
        guiHandler.addListener(DELETE_IMPORTED_MAP, new DeleteMapListener(this));
        guiHandler.addListener(LIST_IMPORTED_MAPS, new GeneralListener() {
            @Override
            public void handleEvent(GeneralEvent e) {
                updateImportedMapsList();
            }      
        });
            
        // SET GUI INITIAL SETTINGS
        guiHandler.setView(state.getCenterCoordinates());
        guiHandler.setSpeed(state.getSpeed());
        guiHandler.setActive(true);
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
    
    public void importMap(File osmFile) {
        if(!osmFile.exists()) {
            logger.error("osm File doesn't exist");
        } else {
            state.resetMap();
            new OSMLoader().importMap(osmFile);
            Precalculator.precalculate();
            state.getLoadedMapInfo().compactifyDatastructures();
            renderer.resetCache();
            state.setLoadedMapFile(new File("./sral/" +
                    Util.removeExtension(osmFile.getName()) + "_" + state.getHeightMalus()
                    + "_" + state.getHighwayMalus() + ".sral"));    
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
    
    public void saveMap(String mapPath) {
        File mapFile = new File(mapPath);
        try {
            MapIO.saveMap(mapFile);
        } catch (IOException e) {
            logger.fatal("saveMap: IO Exception in MapIO");
        }
    }
    
    public void loadMap(File file) {
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
            renderer.resetCache();
        }
    }

    public void deleteMap(String path){
        File file = new File(path);
        if(file.exists()) {
            file.delete();
        }
        updateImportedMapsList();
    }
           
    public void importHeightmaps(File directory) {
        HeightLoader loader = new SRTMLoaderRetarded();
        loader.load(directory);
    }
    
    
    public void saveRoute(String path) {
        File routeFile = new File(path);
        if (state.getCurrentRoute().size() != 0) {
            try {
                RouteIO.saveCurrentRoute(routeFile);
            } catch (IOException e) {
                logger.error("Could not save route to file '" + path + "'.");
            }
        }
    }

    public void loadRoute(String path) {
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

    public void exportRoute(String path) {
        String kmlPath = path;
        if (!kmlPath.endsWith(".kml")) {
            kmlPath += ".kml";
        }
        File routeFile = new File(kmlPath);
        if (state.getCurrentRoute().size() != 0) {
            RouteIO.exportCurrentRouteToKML(routeFile);
        }
    }
      
    public void addNavNode(Coordinates pos, int position) {
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
//        for (int i = 0; i < state.getNavigationNodes().size(); i++) {
//            guiHandler.showNavNodeDescription(state.getNavigationNodes().get(i).getName(), i);    // TODO error in GUI
//        }
    }
    
    public void addNavNode(String name, int position) {
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
//        for (int i = 0; i < state.getNavigationNodes().size(); i++) {
//            guiHandler.showNavNodeDescription(state.getNavigationNodes().get(i).getName(), i);    // TODO error in GUI
//        }
    }

    public void getNavNodeFromText(String str) {
        Selection sel = state.getLoadedMapInfo().select(str);
        if (sel != null) {
            state.getNavigationNodes().add(state.getNavigationNodes().size() - 1, sel);
            guiHandler.updateNavPointsList(state.getNavigationNodes());
            calculateRoute();
        }
    }
    
    public void deleteNavNode(int pos) {
        if (pos < state.getNavigationNodes().size()) {
            state.getNavigationNodes().remove(pos);
            state.setCurrentRoute(new ArrayList<Integer>());
            guiHandler.updateNavPointsList(state.getNavigationNodes());
            calculateRoute();
        }
    }
    
    public void deleteNavNode(Coordinates pos) {
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

    public void switchNavNodes(int index1, int index2) {
        if (index1 < state.getNavigationNodes().size()
            && index2 < state.getNavigationNodes().size()) {
            Collections.swap(state.getNavigationNodes(), index1, index2);
            calculateRoute();
        }
    }

    public void optimizeRoute() {  
        Router.optimizeRoute(state.getNavigationNodes());
        guiHandler.updateNavPointsList(state.getNavigationNodes());
        calculateRoute();
    }
    
    public void calculateRoute() {
        if (state.getNavigationNodes().size() >= 2) {
            state.setCurrentRoute(Router.calculateRoute(state.getNavigationNodes()));
            int duration = ComplexInfoSupplier.getDuration(state.getCurrentRoute(), state.getSpeed()); 
            int length = ComplexInfoSupplier.getLength(state.getCurrentRoute());
            guiHandler.showRouteValues(length, duration);
            guiHandler.updateGUI();
        }
    }

    
    public void addFavorite(Coordinates pos, String name, String description) {  
        state.getLoadedMapInfo().addFavorite(pos, new POIDescription(name, OSMType.FAVOURITE, description));
        guiHandler.updateGUI();
    }

    public void deleteFavorite(Coordinates pos) {
        state.getLoadedMapInfo().deleteFavorite(pos, state.getDetailLevel(), state.getClickRadius());
        guiHandler.updateGUI();
    }


    public void passElementType(Coordinates pos) {
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
    
    public void passDescription(Coordinates pos) {   
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

    public void passSearchCompletion(String str) {
        guiHandler.showSearchCompletion(state.getLoadedMapInfo().suggestCompletions(str));
    }
    
    public void passTextRoute() {   //TODO
        if (state.getCurrentRoute().size() != 0) {
            ComplexInfoSupplier.getRouteDescription(state.getCurrentRoute());
        }
    }
  
    public void updateImportedMapsList() {
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
    
    public void setSpeed(int speed) {
        if(speed >= 0) {
            state.setSpeed(speed);
            guiHandler.showRouteValues(ComplexInfoSupplier.getLength(state.getCurrentRoute()),
                    ComplexInfoSupplier.getDuration(state.getCurrentRoute(), speed));
        }
    }
    
    public void setHeightMalus(int newMalus) {
        if (newMalus >= 0) {
            state.setHeightMalus(newMalus);
        }
    }

    public void setHighwayMalus(int newMalus) {
        if (newMalus >= 0) {
            state.setHighwayMalus(newMalus);
        }
    }
    
    public void setViewToMapCenter() {
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
    
    public void render(Context context) {
        state.setDetailLevel(context.getZoomlevel());
        renderer.render(context); 
    }
    
    public void switchMapMode() {
        renderer = (renderer instanceof Renderer3D) ? new Renderer() : new Renderer3D();
    }
    
    public void prepareForShutdown() {
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
