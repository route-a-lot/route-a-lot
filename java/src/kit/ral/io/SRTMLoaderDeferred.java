package kit.ral.io;

import java.io.File;

import kit.ral.common.Coordinates;
import kit.ral.heightinfo.HeightTile;
import kit.ral.heightinfo.DeferredHeightTile;


public class SRTMLoaderDeferred extends SRTMLoader {

    public HeightTile loadHeightTile(File file, Coordinates position) {
        return new DeferredHeightTile(file, position);
    }
    
}
