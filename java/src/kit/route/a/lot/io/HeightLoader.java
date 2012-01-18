package kit.route.a.lot.io;

import kit.route.a.lot.heightinfo.HeightTile;
import java.io.File;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.EOFException;



public class HeightLoader {

    int count = 0;
    int input;
    int width = 1201;
    int height = 1201;
    int lat = 0;
    int lon = 0;

    HeightTile tile = null;
    FileInputStream in = null;
    DataInputStream bin = null;

    
    /**
     * Operation load
     * 
     * @param file
     *            -
     * @return
     * @return
     */
    public void load(File file) {
        //einlesen der einzelnen Tiles
        File[] dateien = file.listFiles();
    }
}
