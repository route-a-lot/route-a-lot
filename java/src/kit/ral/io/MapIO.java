package kit.ral.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.log4j.Logger;

import kit.ral.common.Progress;
import kit.ral.common.RandomReadStream;
import kit.ral.common.RandomWriteStream;
import kit.ral.controller.State;

public class MapIO {
    
    private static String FORMAT = "SRAL v0.7";
    
    private static Logger logger = Logger.getLogger(MapIO.class);

    /**
     * Loads the given SRAL map file into the MapInfo and RoutingGraph data
     * structures that are accessible via State.
     * Also changes the current map name in state. Exceptions occur if the
     * file cannot be opened or has an invalid format. An IllegalStateException
     * will be thrown if the destination data structures don't exist.
     * 
     * @param file the file to be loaded
     * @throws IOException
     */
    public static void loadMap(File file, Progress p) throws IOException {
        // Verify requirements
        if (file == null) {
            throw new IllegalArgumentException();
        }
        State state = State.getInstance();
        if ((state.getMapInfo() == null) || (state.getLoadedGraph() == null)) {
            throw new IllegalStateException("No map initialized!");
        }
        // Open file stream, abort on failure
        RandomReadStream input = new RandomReadStream(file, new FileInputStream(file));
        
        // Read data from stream, abort on error
        for (byte b : FORMAT.getBytes()) {
            if (input.readByte() != b) {
                throw new IOException("Is either not a map file or"
                            + " wrong format version: " + file.getName());
            }
        }
        
        state.getLoadedGraph().loadFromInput(input);
        
        state.getMapInfo().loadFromInput(input);
        
        input.close();
    }

    /**
     * Saves the MapInfo and RoutingGraph data structures that are accessible
     * via State into the given file using the SRAL map format v0.7.
     * An existing file will be replaced.
     * IOExceptions occur if the file cannot be opened, created, or written to.
     * An IllegalStateException will be thrown if the destination data
     * structures don't exist.
     * 
     * @param file the file to be loaded
     * @throws IOException
     */
    public static void saveMap(File file, Progress p) throws IOException {   
        // Verify requirements
        if (file == null) {
            throw new IllegalArgumentException();
        }
        
        State state = State.getInstance();
        if ((state.getMapInfo() == null) || (state.getLoadedGraph() == null)) {
            throw new IllegalStateException("No map loaded!");
        }
        
        // Open / create file stream, abort on failure
        RandomWriteStream output = new RandomWriteStream(file, new FileOutputStream(file));
        
        // Write data to stream, abort on error
        output.writeBytes(FORMAT);
        p.addProgress(0.05);
        
        logger.info("save graph...");
        state.getLoadedGraph().saveToOutput(output); 
        p.addProgress(0.25);
        
        logger.info("save map info...");
        state.getMapInfo().saveToOutput(output);
        p.addProgress(0.7);
        
        logger.info("map saving finished");      
        output.close();
    }
    
}
