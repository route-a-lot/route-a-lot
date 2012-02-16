package kit.route.a.lot.io;

import java.io.File;
import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.heightinfo.HDDHeightTile;
import kit.route.a.lot.heightinfo.HeightTile;


public class SRTMLoaderHDD extends SRTMLoader {

    public HeightTile loadHeightTile(File file, Coordinates position) {
        return new HDDHeightTile(file, position);
    }
    
}
