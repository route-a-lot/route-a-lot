package kit.ral.io;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.*;
import org.junit.Test;

import kit.ral.common.Progress;
import kit.ral.controller.State;
import kit.ral.map.info.MapInfo;


public class MapIOTest {

   static Logger logger = Logger.getLogger(MapIOTest.class);
   private File sralMap;
   private Progress p;    
    
    @BeforeClass
    public static void setUpClass() {
        PropertyConfigurator.configure("config/log4j.conf");
    }
   
    @Before
    public void setUp() throws Exception {
       sralMap = new File("test/resources/karlsruhe_small_current.sral");
       p = new Progress();
    }
     
    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void SaveAndLoad() {
        File karlsruheMap = new File("test/resources/karlsruhe_small_current.osm");
        OSMLoader osmLoader = new OSMLoader(State.getInstance());
        osmLoader.importMap(karlsruheMap, p.createSubProgress(0.6));
        State state = State.getInstance();

        // Controller.setViewToMapCenter() externalized:
        state.setCenterCoordinates(state.getMapInfo().getBounds().getCenter());
        try {
            MapIO.saveMap(sralMap, p.createSubProgress(0.3));
        } catch (IOException e) {
            fail();
        }
        //MapInfo original = state.getMapInfo();
        state.setMapInfo(new MapInfo());
        try {
            MapIO.loadMap(sralMap, p.createSubProgress(1));
        } catch (IOException e) {
            fail();
        }
        //assertTrue(original.equals(state.getMapInfo()));
    }
}
