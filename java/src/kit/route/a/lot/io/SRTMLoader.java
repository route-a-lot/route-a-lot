package kit.route.a.lot.io;

import kit.route.a.lot.heightinfo.HeightTile;
import kit.route.a.lot.heightinfo.IHeightmap;
import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.controller.State;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.log4j.Logger;


public class SRTMLoader implements HeightLoader {

    IHeightmap heightmap = State.getInstance().getLoadedHeightmap();
    private static final int WIDTH = 1201;
    private static final int HEIGHT = 1201;
    private static final int MAX_DEVIATION = 70;
    private static final String FILE_EXTENSION = "hgt";
    private static Logger logger = Logger.getLogger(SRTMLoader.class);

    @Override
    public void load(File dataDirectory) { 
        File[] dateien = dataDirectory.listFiles();
        if (dateien == null) {
            return;
        }
        HeightTile tile;
        DataInputStream in;

        for (File file: dateien) {
            String[] fileNameParts = file.getName().split("\\.");
            if ((fileNameParts.length != 2) || (fileNameParts[0].length() != 7)
                    || !fileNameParts[1].equals(FILE_EXTENSION)) {
                continue;
            }
            String fileName = fileNameParts[0];  
            try {
                float lat = Float.parseFloat(fileName.substring(1, 3));
                if (fileName.charAt(0) == 'S') {
                    lat = -lat;
                }
                float lon = Float.parseFloat(fileName.substring(4, 7));
                if (fileName.charAt(3) == 'W') {
                    lon = -lon;
                }
                tile = new HeightTile(WIDTH, HEIGHT, new Coordinates(lat, lon));
            } catch (NumberFormatException e) {
                continue;
            }
            logger.info("Loading height file '" + file.getName() + "'...");

            try {
                in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
                int oldheight = 0;
                for (int i = HEIGHT - 1; i >= 0; i--) {
                    for (int j = 0; j < WIDTH; j++) {
                        int height = in.readShort();
                        if(j == 0) {
                            oldheight = height;
                        }
                        if (height < oldheight - MAX_DEVIATION) {
                            height = oldheight;
                        }
                        if (height > oldheight + MAX_DEVIATION) {
                            height = oldheight;
                        }
                        tile.setHeight(j, i, height);
                        oldheight = height;
                    }// for width
                }// for height
                in.close();
            } catch (IOException e) {
                logger.error("Invalid hgt file: '" + file.getName() + "'. Loading aborted.");
            }
            /* in HeightMap einfügen */
            heightmap.addHeightTile(tile);
        }// end for dateien
    }
}
