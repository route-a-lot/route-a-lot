package kit.route.a.lot.io;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.RouteDescription;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.heightinfo.IHeightmap;
import kit.route.a.lot.map.infosupply.MapInfo;
import kit.route.a.lot.routing.RoutingGraph;

import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class StateIOTest extends StateIO {
    
    OSMLoader loader;
    StateMock state;
    
    @BeforeClass
    public static void setUpClass() {
        PropertyConfigurator.configure("config/log4j.conf");
    }
    
    @Before
    public void setUp() throws Exception {
        loader = new OSMLoader();
        state = new StateMock();
        loader.state = state;
        loader.weightCalculator = new WeightCalculatorMock();
    }
    
    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void saveAndLoad() throws Exception {
        File file = new File("mock.state");
        loader.importMap(new File("test/resources/karlsruhe_small.osm"));
        File loadedMapFile = state.getLoadedMapFile();
        MapInfo loadedMapInfo = state.getLoadedMapInfo();
        RoutingGraph loadedGraph = state.getLoadedGraph();
        IHeightmap loadedHeightmap = state.getLoadedHeightmap();
        Coordinates centerCoordinates = state.getCenterCoordinates();
        int detailLevel = state.getDetailLevel();
        int clickRadius = state.getClickRadius();
        RouteDescription routeDescription = state.getRouteDescription();
        int speed = state.getSpeed();
        int duration = state.getDuration();
        int heightMalus = state.getHeightMalus();
        int highwayMalus = state.getHighwayMalus();
        
        // test deterministic importing
        setUp();
        loader.importMap(new File("test/resources/karlsruhe_small.osm"));
        assertEquals(loadedMapFile, state.getLoadedMapFile());
        assertTrue(loadedMapInfo.equals(state.getLoadedMapInfo()));
        assertTrue(loadedGraph.equals(state.getLoadedGraph()));
        assertTrue(loadedHeightmap.equals(state.getLoadedHeightmap()));
        assertTrue(centerCoordinates.equals(state.getCenterCoordinates()));
        assertEquals(detailLevel, state.getDetailLevel());
        assertEquals(clickRadius, state.getClickRadius());
        assertTrue(routeDescription.equals(state.getRouteDescription()));
        assertEquals(speed, state.getSpeed());
        assertEquals(duration, state.getDuration());
        assertEquals(heightMalus, state.getHeightMalus());
        assertEquals(highwayMalus, state.getHighwayMalus());
        
        // Test proper saving/loading
        saveState(file);
        setUp();
        loadState(file);
        assertEquals(loadedMapFile, state.getLoadedMapFile());
        assertEquals(loadedMapInfo, state.getLoadedMapInfo());
        assertEquals(loadedGraph, state.getLoadedGraph());
        assertEquals(loadedHeightmap, state.getLoadedHeightmap());
        assertEquals(centerCoordinates, state.getCenterCoordinates());
        assertEquals(detailLevel, state.getDetailLevel());
        assertEquals(clickRadius, state.getClickRadius());
        assertEquals(routeDescription, state.getRouteDescription());
        assertEquals(speed, state.getSpeed());
        assertEquals(duration, state.getDuration());
        assertEquals(heightMalus, state.getHeightMalus());
        assertEquals(highwayMalus, state.getHighwayMalus());
        
    }
}
