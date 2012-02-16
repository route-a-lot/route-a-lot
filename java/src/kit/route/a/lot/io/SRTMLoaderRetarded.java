package kit.route.a.lot.io;

import java.io.File;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.heightinfo.HeightTile;
import kit.route.a.lot.heightinfo.RetardedHeightTile;


public class SRTMLoaderRetarded extends SRTMLoader {

    public HeightTile loadHeightTile(File file, Coordinates position) {
        return new RetardedHeightTile(file, position);
    }
    
}
