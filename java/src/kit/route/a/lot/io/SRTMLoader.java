package kit.route.a.lot.io;

import kit.route.a.lot.heightinfo.HeightTile;
import kit.route.a.lot.heightinfo.RAMHeightTile;
import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.controller.State;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.log4j.Logger;


public class SRTMLoader implements HeightLoader {

    protected static final int WIDTH = 1201, HEIGHT = 1201, MAX_DEVIATION = 50;
    private static final String FILE_EXTENSION = "hgt";
    private static Logger logger = Logger.getLogger(SRTMLoader.class);

    @Override
    public void load(File dataDirectory) { 
        File[] dateien = dataDirectory.listFiles();
        if (dateien == null) {
            return;
        }
        logger.info("Loading hgt files...");
        for (File file: dateien) {
            String[] fileNameParts = file.getName().split("\\.");
            if ((fileNameParts.length != 2) || (fileNameParts[0].length() != 7)
                    || !fileNameParts[1].equals(FILE_EXTENSION)) {
                continue;
            }
            String fileName = fileNameParts[0];  
            float lat, lon;
            try {
                lat = Float.parseFloat(fileName.substring(1, 3)) * ((fileName.charAt(0) == 'S') ? -1 : 1);
                lon = Float.parseFloat(fileName.substring(4, 7)) * ((fileName.charAt(3) == 'W') ? -1 : 1);
            } catch (NumberFormatException e) {
                logger.info("Invalid hgt file name: '" + file.getName() + "'.");
                continue;
            }
            logger.debug("Loading hgt file :'" + file.getName() + "'...");
            HeightTile tile = loadHeightTile(file, new Coordinates(lat, lon));
            /* in HeightMap einfügen */
            State.getInstance().getLoadedHeightmap().addHeightTile(tile);
        }// end for dateien
    }
    
    public HeightTile loadHeightTile(File file, Coordinates position) {
        HeightTile tile = new RAMHeightTile(WIDTH, HEIGHT, position);
        try {
            DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
            for (int i = HEIGHT - 1; i >= 0; i--) {
                for (int j = 0; j < WIDTH; j++) {
                    tile.setHeight(j, i, in.readShort());
                }
            }
            in.close();
        } catch (IOException e) {
            logger.error("Invalid hgt file: '" + file.getName() + "'. Loading aborted.");
            return null;
        }     
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                float exp1 = tile.getHeight(x-1, y);
                float exp2 = tile.getHeight(x, y-1);
                float exp3 = tile.getHeight(x-1, y-1);
                int exp1a = (exp1 > -500) ? 1 : 0;
                int exp2a = (exp2 > -500) ? 1 : 0;
                int exp3a = (exp3 > -500) ? 1 : 0;
                float areaHeight = (exp1a + exp2a + exp3a == 0) ? 100 :
                        (exp1 * exp1a + exp2 * exp2a + exp3 * exp3a) / (exp1a + exp2a + exp3a);
                if (tile.getHeight(x, y) < -500) {
                    tile.setHeight(x,y, areaHeight);
                }              
            }
        }
        return tile;
    }
}
