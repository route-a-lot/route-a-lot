package kit.ral.io;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.apache.log4j.Logger;

import kit.ral.common.Progress;
import kit.ral.controller.State;

public class MapIO {
    
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
        DataInputStream input = new DataInputStream(new BufferedInputStream(
                new ProgressInputStream(new FileInputStream(file), p, file.length())));
        
        // Read data from stream, abort on error
        if ((input.readChar() != 'S') || (input.readChar() != 'R')
                || (input.readChar() != 'A') || (input.readChar() != 'L')) {
            throw new IOException("Is not a map file: " + file.getName());
        }
        if (!input.readUTF().equals("0.6")) {
            throw new IOException("Wrong format version: " + file.getName());
        } 

        state.getMapInfo().loadFromInput(input);
        state.getLoadedGraph().loadFromInput(input);
        input.close();
    }

    /**
     * Saves the MapInfo and RoutingGraph data structures that are accessible
     * via State into the given file using the SRAL map format v0.5.
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
        //DataOutputStream output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
        RandomAccessFile output = new RandomAccessFile(file, "rw");
        
        // Write data to stream, abort on error
        output.writeChars("SRAL");  // magic number
        output.writeUTF("0.6");     // version number
        // TODO: maybe add date or name
        p.addProgress(0.05);
        logger.info("save map info...");
        state.getMapInfo().saveToOutput(output);
        p.addProgress(0.7);
        logger.info("save graph...");
        state.getLoadedGraph().saveToOutput(output); 
        //output.close();     
        logger.info("map saving finished");
        p.addProgress(0.25);
    }
    
}
