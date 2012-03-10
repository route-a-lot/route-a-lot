package kit.ral.io;

import java.io.File;

import kit.ral.common.Progress;




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
