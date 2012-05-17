
/**
Copyright (c) 2012, Matthias Grundmann, Malte Wolff, Jan Jacob, Daniel Krauß, Josua Stabenow
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

package kit.ral.controller;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import kit.ral.common.Bounds;
import kit.ral.common.Context;
import kit.ral.common.Coordinates;
import kit.ral.common.Progress;
import kit.ral.common.Selection;
import kit.ral.common.WeightCalculator;
import kit.ral.common.description.OSMType;
import kit.ral.common.description.POIDescription;
import kit.ral.common.event.AddFavoriteEvent;
import kit.ral.common.event.Event;
import kit.ral.common.event.FloatEvent;
import kit.ral.common.event.Listener;
import kit.ral.common.event.NumberEvent;
import kit.ral.common.event.PositionEvent;
import kit.ral.common.event.PositionNumberEvent;
import kit.ral.common.event.RenderEvent;
import kit.ral.common.event.SwitchNavNodesEvent;
import kit.ral.common.event.TextEvent;
import kit.ral.common.event.TextNumberEvent;
import kit.ral.common.projection.Projection;
import kit.ral.common.util.StringUtil;
import kit.ral.common.util.Util;
import kit.ral.gui.GUIHandler;
import kit.ral.io.HeightLoader;
import kit.ral.io.MapIO;
import kit.ral.io.OSMLoader;
import kit.ral.io.RouteIO;
import kit.ral.io.SRTMLoaderDeferred;
import kit.ral.io.StateIO;
import kit.ral.map.Node;
import kit.ral.map.info.ComplexInfoSupplier;
import kit.ral.map.rendering.Renderer;
import kit.ral.map.rendering.Renderer3D;
import kit.ral.routing.Precalculator;
import kit.ral.routing.Router;
import static kit.ral.common.event.Listener.*;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Controller {
    // Same definition as in Map:
    private static final int FREEMAPSPACE = 0, POI = 1, FAVORITE = 2, NAVNODE = 3;

    private static final File
        SRAL_DIRECTORY = new File("./maps"),
        SRTM_DIRECTORY = new File("./srtm"),
        STATE_FILE = new File("./state"),
        DEFAULT_OSM_MAP = new File("./maps/karlsruhe_small_current.osm");
    
    private static final String SRAL_EXT = ".sral";
    
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Future<?> currentTask;
            
    private GUIHandler guiHandler;
    private State state;
    private static Logger logger = Logger.getLogger(Controller.class);
    
    private static int MAX_SEARCH_COMPLETIONS = 15;
    
    // command arguments
    private File file;
    private boolean gui = true;
    private boolean graphOnly = false;
    
    public static boolean mod = true;
    
    public static void main(String[] args) {
        // ENVIRONMENT SETUP
        File log4jConfigFile = new File("config/log4j.conf");
        if (log4jConfigFile.exists()) {
            PropertyConfigurator.configure("config/log4j.conf");
        } else {
            Properties properties = new Properties();
            properties.put("log4j.rootLogger", "FATAL");
            PropertyConfigurator.configure(properties);
        }
        String arch = System.getProperty("os.arch").toLowerCase();
        int archbits = (arch.equals("i386") || arch.equals("x86")) ? 32 : 64;
        try {
            addDirectoryToLibraryPath("./lib/lib" + archbits);
        } catch (IOException e) {
            logger.error("Could not load library path for " + arch + ".");
            return;
        }
        //System.setProperty("sun.java2d.opengl","True"); TODO make 3D mode compatible
        if (!SRAL_DIRECTORY.exists()) {
            SRAL_DIRECTORY.mkdir();
        }        
        new Controller(args);
    }
    
    private Controller(String[] cmdArgs) {
        state = State.getInstance();

        if (!interpretArguments(cmdArgs)) {
            return;
        }
        
        final Progress p = new Progress();

        if (gui) {
            guiHandler = new GUIHandler();
            addListeners(); 
        }
        
        // IMPORT HEIGHT DATA    
        Util.startTimer();
        importHeightmaps(SRTM_DIRECTORY, p.createSubProgress(0.05));
        logger.info("Heightmaps loaded: " + Util.stopTimer());
        
        if (gui) {
            // LOAD STATE
            Util.startTimer();
            loadState(p.createSubProgress(0.6));
                 
            // IMPORT DEFAULT OSM MAP
            if (state.getLoadedMapFile() == null) {
                if ((DEFAULT_OSM_MAP != null) && DEFAULT_OSM_MAP.exists()) {
                    Util.startTimer();
                    currentTask = executorService.submit(new Runnable() {
                        public void run() {
                            importMap(DEFAULT_OSM_MAP, p.createSubProgress(0.4));
                        }   
                    });
                    try {
                        currentTask.get();
                        logger.info("Imported default map: " + Util.stopTimer());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (CancellationException e) {
                        state.resetMap();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                } else {
                    logger.error("Default map does not exist.");
                }
            } else {
                logger.info("State loaded: " + Util.stopTimer());
            }
        } else {
            if (graphOnly) {
                state.setMapInfo(new kit.ral.io.MapInfoMock());
            }
            importMap(file, p.createSubProgress(0.95));
        }
        p.finish();
    }
    
    private boolean interpretArguments(String[] args) {
        boolean error = false;
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("--")) {
                if (args[i].equals("--nogui")) {
                    gui = false;
                } else if (args[i].equals("--gui")) {
                    gui = true;
                } else if (args[i].equals("--nomod")) {
                    mod = false;
                } else if (args[i].equals("--mod")) {
                    mod = true;
                } else if (args[i].equals("--nographOnly")) {
                    graphOnly = false;
                } else if (args[i].equals("--graphOnly")) {
                    graphOnly = true;
                } else if (args[i].equals("--height")) {
                    i++;
                    if (i == args.length) {
                        error = true;
                        break;
                    }
                    try {
                        state.setHeightMalus(Integer.parseInt(args[i]));
                    } catch (NumberFormatException e) {
                        System.out.println(e.getMessage());
                        error = true;
                    }
                } else if (args[i].equals("--highway")) {
                    i++;
                    if (i == args.length) {
                        error = true;
                        break;
                    }
                    try {
                        state.setHighwayMalus(Integer.parseInt(args[i]));
                    } catch (NumberFormatException e) {
                        System.out.println(e.getMessage());
                        return false;
                    }
                }
            } else {
                file = new File(args[i]);
                if (!file.exists()) {
                    System.out.println("Given file does not exist.");
                    error = true;
                }
            }
        }
        if (error) {
            System.out.println("Possible paramters:");
            System.out.println("--gui        or     --nogui");
            System.out.println("--mod        or     --nomod");
            System.out.println("--graphOnly  or     --nographOnly");
            System.out.println("--height <heightMalus>");
            System.out.println("--highway <highwayMalus>");
        }
        return !error;
    }
    
    private void addListeners() {
        Listener.addListener(ADD_NAVNODE, new Listener() {
            public void handleEvent(Event e) {
                if (e instanceof PositionNumberEvent) {
                    PositionNumberEvent event = (PositionNumberEvent) e;
                    addNavNode(event.getPosition(), event.getNumber());
                } else {  
                    TextNumberEvent event = (TextNumberEvent) e;
                    addNavNode(event.getText(), event.getNumber());
                    //getNavNodeFromText(((TextEvent) e).getText());
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
            public void handleEvent(final Event e) {
                final File file = new File(((TextEvent) e).getText());
                currentTask = executorService.submit(new Runnable() {
                    public void run() {
                        Progress p = new Progress();
                        importMap(file, p);
                        p.finish(); 
                    }   
                });
            } 
        });  
        Listener.addListener(OPTIMIZE_ROUTE, new Listener() {
            public void handleEvent(Event e) {
                currentTask = executorService.submit(new Runnable() {
                    public void run() {
                        Progress p = new Progress();
                        optimizeRoute(p.createSubProgress(1));
                        p.finish();
                    }   
                });
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
            public void handleEvent(final Event e) {
                currentTask = executorService.submit(new Runnable() {
                    public void run() {
                        String text = ((TextEvent) e).getText();
                        Progress p = new Progress();
                        loadMap((text.length() == 0) ? null : new File(
                                SRAL_DIRECTORY + "/" + text + SRAL_EXT), p);
                        p.finish(); 
                        setViewToMapCenter();
                    }   
                });          
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
                saveRoute(new File(((TextEvent) e).getText()));
            }    
        });
        Listener.addListener(LOAD_ROUTE, new Listener() {
            public void handleEvent(Event e) {
                loadRoute(new File(((TextEvent) e).getText()));
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
                TextNumberEvent event = (TextNumberEvent) e;
                passSearchCompletion(event.getText(), event.getNumber());
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
        Listener.addListener(CANCEL_OPERATION, new Listener() {
            public void handleEvent(Event e) {
                if (currentTask != null) {
                    currentTask.cancel(true);
                    currentTask = null;
                    Listener.fireEvent(Listener.PROGRESS, new FloatEvent(100));
                }
            }      
        });
        Listener.addListener(NEW_ROUTE, new Listener() {
            public void handleEvent(Event e) {
                calculateRoute();
                guiHandler.updateNavNodes(state.getNavigationNodes());
            }      
        });
        Listener.addListener(SWITCH_NAV_NODES, new Listener() {
            public void handleEvent(Event event) {
                switchNavNodes(((SwitchNavNodesEvent)event).getFirstID(), ((SwitchNavNodesEvent)event).getSecondID());
                calculateRoute();
            }
        });
    }
    
    private void switchNavNodes(int one, int two) {
        if (one >= state.getNavigationNodes().size() || two >= state.getNavigationNodes().size()) {
            return;
        }
        Collections.swap(state.getNavigationNodes(), one, two);
        calculateRoute();
        guiHandler.updateNavNodes(state.getNavigationNodes());
    }
    
    private void loadState(Progress p) {
        State state = State.getInstance(); 
        p.addProgress(0.01);
        if (STATE_FILE.exists()) {   
            try {
                StateIO.loadState(STATE_FILE); 
                //guiHandler.setMapMode(state.getActiveRenderer() instanceof Renderer3D);              
            } catch (IOException e) {
                logger.error("State loading: Read error occurred.");
                e.printStackTrace();
            } 
            p.addProgress(0.1);
            // LOAD SRAL MAP FROM STATE
            if ((state.getLoadedMapFile() != null) && state.getLoadedMapFile().exists()) {
                logger.info("Loading map file from state");
                loadMap(state.getLoadedMapFile(), p.createSubProgress(0.89));
            }   
        }
        guiHandler.setSpeed(state.getSpeed());  
        guiHandler.setView(state.getCenterCoordinates(), state.getDetailLevel());
        p.finish();
    }
    
    private void importMap(File osmFile, Progress p) {
        if(!osmFile.exists()) {
            logger.error("OSM File doesn't exist");
        } else {
            state.resetMap();
            if (graphOnly) {
                state.setMapInfo(new kit.ral.io.MapInfoMock());
            }
            System.gc();
            new OSMLoader(state, new WeightCalculator(state)).importMap(osmFile, p.createSubProgress(0.0004));
            Precalculator.precalculate(p.createSubProgress(0.9995));
            state.getMapInfo().compactify();
            state.setLoadedMapFile(new File(SRAL_DIRECTORY + "/" + StringUtil.removeExtension(osmFile.getName())
                    + " (" + state.getHeightMalus() + ", " + state.getHighwayMalus() + ")" + SRAL_EXT));    
            try {
                MapIO.saveMap(state.getLoadedMapFile(), p.createSubProgress(0.0001));
            } catch (IOException e) {
                logger.error("Could not save imported map to file.");
                logger.debug(e.toString());
                e.printStackTrace();
            }
            setViewToMapCenter();
            updateImportedMapsList();
            state.getActiveRenderer().resetCache();
        }    
        p.finish();
    }
  
    private void loadMap(File file, Progress p) {
        if (file == null) {
            state.resetMap();
            guiHandler.updateGUI();
        } else if (!file.exists()) {
            logger.error("Map file doesn't exist.");
        } else {
            state.resetMap();
            try {
                state.getActiveRenderer().resetCache();
                MapIO.loadMap(file, p);
                state.setLoadedMapFile(file);
            } catch (IOException e) {
                e.printStackTrace();
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
           
    private void importHeightmaps(File directory, Progress p) {
        HeightLoader loader = new SRTMLoaderDeferred();
        loader.load(directory, p);
    }
    
    
    private void saveRoute(File file) {
        if (state.getCurrentRoute().size() != 0) {
            try {
                RouteIO.saveCurrentRoute(file);
            } catch (IOException e) {
                logger.error("Could not save route to file '" + file + "'.");
            }
        }
    }

    private void loadRoute(File file) {
        if (!file.exists()) {
            logger.error("No such route file: " + file);
        } else if (state.getLoadedMapFile() != null) {
            try {
                RouteIO.loadCurrentRoute(file);
            } catch (IOException e) {
                logger.error("Could not load route from file '" + file + "'.");
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
        Selection newSel = state.getMapInfo().select(pos);
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
        Selection newSel = state.getMapInfo().select(name);
        if (newSel != null) {
            if (state.getNavigationNodes().size() == 0) {
             state.getNavigationNodes().add(newSel);   
            } else if (position == 0 && state.getNavigationNodes().size() >= 1) {
                state.getNavigationNodes().remove(0);
                state.getNavigationNodes().add(0, newSel);
            } else if (position == state.getNavigationNodes().size() && state.getNavigationNodes().size() > 1) {
                state.getNavigationNodes().remove(state.getNavigationNodes().remove(state.getNavigationNodes().size() - 1));
                state.getNavigationNodes().add(newSel);
            } else if (position == state.getNavigationNodes().size()) {
                state.getNavigationNodes().add(newSel);
            } else if (position >= state.getNavigationNodes().size()) {
                state.getNavigationNodes().add(state.getNavigationNodes().size() - 1, newSel);
            } else if (position == state.getNavigationNodes().size() - 1) {    
                state.getNavigationNodes().add(position, newSel);
            } else {
                state.getNavigationNodes().remove(position);
                state.getNavigationNodes().add(position, newSel);
            }
            setViewTo(newSel.getPosition());
        }
        guiHandler.updateNavNodes(state.getNavigationNodes());
        calculateRoute();
        // for (int i = 0; i < state.getNavigationNodes().size(); i++) {
        //     guiHandler.showNavNodeDescription(state.getNavigationNodes().get(i).getName(), i);    // TODO error in GUI
        // }        
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

    private void optimizeRoute(Progress p) {
        Router.optimizeRoute(state.getNavigationNodes(), p);
        guiHandler.updateNavNodes(state.getNavigationNodes());
        calculateRoute();
    }
    
    private void calculateRoute() {
        double startTime = System.currentTimeMillis();
        if (state.getNavigationNodes().size() >= 2) {
            state.setCurrentRoute(Router.calculateRoute(state.getNavigationNodes()));
            int duration = ComplexInfoSupplier.getDuration(state.getCurrentRoute(), state.getSpeed(), state.getNavigationNodes()); 
            int length = ComplexInfoSupplier.getLength(state.getCurrentRoute(), state.getNavigationNodes());
            guiHandler.showRouteValues(length, duration);
            guiHandler.updateGUI();
        }
        double duration = System.currentTimeMillis() - startTime;
        logger.info("Calculated route in " + (duration / 1000) + "s");
    }

    
    private void addFavorite(Coordinates pos, String name, String description) {  
        state.getMapInfo().addFavorite(pos, new POIDescription(name, OSMType.FAVOURITE, description));
        guiHandler.updateGUI();
    }

    private void deleteFavorite(Coordinates pos) {
        state.getMapInfo().deleteFavorite(pos, state.getDetailLevel(), state.getClickRadius());
        guiHandler.updateGUI();
    }


    private void passElementType(Coordinates pos) {
        float adaptedRadius = (Projection.getZoomFactor(state.getDetailLevel())) * state.getClickRadius();
        Bounds bounds = new Bounds(pos, adaptedRadius);
        int result = FREEMAPSPACE;
        for (Selection navNode: state.getNavigationNodes()) {
            Node node = new Node(navNode.getPosition());
            if (node.isInBounds(bounds)) {
                result = NAVNODE;
                break;
            }
        }   
        if (result == FREEMAPSPACE) {
            POIDescription description = state.getMapInfo().getPOIDescription(pos,
                                    state.getClickRadius(), state.getDetailLevel());
            if (description != null) {
                result = (description.getCategory() == OSMType.FAVOURITE) ?  FAVORITE : POI;
            }
        }
        guiHandler.passElementType(result);
    }
    
    private void passDescription(Coordinates pos) {   
        POIDescription description = state.getMapInfo().getPOIDescription(pos,
                        state.getClickRadius(), state.getDetailLevel());
        if (description != null) {
            guiHandler.passDescription(description);
        }
    }

    private void passSearchCompletion(String str, int iconNum) {
        List<String> allCompletions = state.getMapInfo().getCompletions(str);
        List<String> shownCompletions = new ArrayList<String>(MAX_SEARCH_COMPLETIONS);
        Iterator<String> it = allCompletions.iterator();
        while (it.hasNext() && shownCompletions.size() < MAX_SEARCH_COMPLETIONS) {
            shownCompletions.add(it.next());
        }
        guiHandler.showSearchCompletion(shownCompletions, iconNum);
    }
  
    private void updateImportedMapsList() {
        if (guiHandler == null) {
            return;
        }
        List<String> maps = new ArrayList<String>();
        int activeMapIndex = -1;
        if(SRAL_DIRECTORY.isDirectory()) {
            File[] files = SRAL_DIRECTORY.listFiles();
            int count = 0;
            for(File file : files) {
                if (file.getName().endsWith(".sral")) {
                    maps.add(StringUtil.removeExtension(file.getName()));
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
            guiHandler.showRouteValues(ComplexInfoSupplier.getLength(state.getCurrentRoute(), state.getNavigationNodes()),
                    ComplexInfoSupplier.getDuration(state.getCurrentRoute(), speed, state.getNavigationNodes()));
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
        if (state.getMapInfo() == null || guiHandler == null) {
            return;
        }
        Bounds bounds = state.getMapInfo().getBounds();
        state.setCenterCoordinates(Coordinates.interpolate(bounds.getTopLeft(),
                bounds.getBottomRight(), 0.5f));
        guiHandler.setView(state.getCenterCoordinates(), state.getDetailLevel());
    }
    
    private void setViewTo(Coordinates viewCenter) {
        if (state.getMapInfo() == null) {
            return;
        }
        Coordinates center = state.getCenterCoordinates();
        center.setLatitude(viewCenter.getLatitude());
        center.setLongitude(viewCenter.getLongitude());
        guiHandler.setView(center, state.getDetailLevel());
    }
    
    private void render(Context context) {
        state.getActiveRenderer().render(context); 
    }
    
    private void setMapMode(boolean render3D) {
        state.setActiveRenderer((render3D) ? new Renderer3D() : new Renderer());
        guiHandler.setMapMode(render3D);
    }
    
    private void prepareForShutdown() {
        try {
            StateIO.saveState(STATE_FILE);
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
