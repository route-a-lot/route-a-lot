package kit.route.a.lot.io;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.controller.State;


public class MapIOTest {

    private static Logger logger = Logger.getLogger(MapIOTest.class);
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        PropertyConfigurator.configure("config/log4j.conf");
        File sralMap = new File("test/resources/karlsruhe_small_current.sral");
        if (!sralMap.exists()) {
            logger.info("TEST: Import OSM map.");
            File karlsruheMap = new File("test/resources/karlsruhe_small_current.osm");
            OSMLoader osmLoader = new OSMLoader();
            osmLoader.importMap(karlsruheMap);
            State state = State.getInstance();
            state.getLoadedMapInfo().buildZoomlevels();
            
            // Controller.setViewToMapCenter() externalized:
            Coordinates upLeft = new Coordinates();
            Coordinates bottomRight = new Coordinates();
            state.getLoadedMapInfo().getBounds(upLeft, bottomRight);
            Coordinates center = new Coordinates();
            center.setLatitude((upLeft.getLatitude() + bottomRight.getLatitude()) / 2);
            center.setLongitude((upLeft.getLongitude() + bottomRight.getLongitude()) / 2);
            state.setCenterCoordinates(center);
            
            logger.info("TEST: Save SRAL map.");
            try {
                MapIO.saveMap(sralMap);
            } catch (IOException e) {
                logger.error("TEST: SRAL save error.");
            }
        } else {
            logger.info("TEST: Load SRAL map.");
            try {
                MapIO.loadMap(sralMap);
            } catch (IOException e) {
                logger.error("TEST: SRAL load error: " + e.getMessage());
            }
        }
        
    }

}
