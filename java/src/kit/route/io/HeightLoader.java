package kit.route.a.lot.io;

import java.io.File;

import kit.route.a.lot.common.Progress;




public interface HeightLoader {

    /**
     * Operation load
     * 
     * @param file
     *            -
     * @param p 
     * @return
     * @return
     */
    public void load(File file, Progress p);
}
