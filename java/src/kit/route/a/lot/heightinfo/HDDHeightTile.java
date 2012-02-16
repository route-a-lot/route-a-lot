package kit.route.a.lot.heightinfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.apache.log4j.Logger;

import kit.route.a.lot.common.Coordinates;


public class HDDHeightTile extends HeightTile {

    private static Logger logger = Logger.getLogger(HDDHeightTile.class);
    private RandomAccessFile data;

    public HDDHeightTile(File file, Coordinates origin) {
        super(1201, 1201, origin);
        try {
            this.data = new RandomAccessFile(file, "rw");
        } catch (FileNotFoundException e) {
            logger.error("Could not open HGT file " + file + " for reading.");
        }
    }
    
    @Override
    public int getHeight(int x, int y) {
        if ((x >= 0) && (x < tileWidth) && (y >= 0) && (y < tileHeight)) {
            try {
                data.seek(((tileHeight - y - 1) * tileWidth + x) * 2);
                return data.readShort();
            } catch (IOException e) {
                logger.error("Height data read access error.");
            }
        }
        return 0;
    }

    @Override
    public void setHeight(int x, int y, float height) {
        if ((x >= 0) && (x < tileWidth) && (y >= 0) && (y < height)) {
            try {
                data.seek(((tileHeight - y - 1) * tileWidth + x) * 2);
                data.writeShort((int) height);
            } catch (IOException e) {
                logger.error("Height data write access error.");
            }
        }
    }
    
    @Override
    protected void finalize() throws Throwable {
        data.close();
        super.finalize();
    } 
    
}
