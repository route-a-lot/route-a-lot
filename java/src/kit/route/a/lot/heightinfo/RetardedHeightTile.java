package kit.route.a.lot.heightinfo;

import java.io.File;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.io.SRTMLoader;

public class RetardedHeightTile extends RAMHeightTile {

    File source;
    boolean initialized = false;
    
    public RetardedHeightTile(File file, Coordinates origin) {
        super(1201, 1201, origin);
        source = file;
    }
    
    @Override
    public int getHeight(int x, int y) {
        if (!initialized) {
            loadFromSource();
            initialized = true;
        }
        return super.getHeight(x, y);
    }

    @Override
    public void setHeight(int x, int y, float height) {
        if (!initialized) {
            loadFromSource();
            initialized = true;
        }
        super.setHeight(x, y, height);
    }

    private void loadFromSource() {
        HeightTile tile = (new SRTMLoader()).loadHeightTile(source, origin);
        data = ((RAMHeightTile) tile).data;
    }
    

}
