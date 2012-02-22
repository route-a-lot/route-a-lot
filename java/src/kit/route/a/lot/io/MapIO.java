package kit.route.a.lot.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;

import kit.route.a.lot.common.Progress;
import kit.route.a.lot.common.ProgressInputStream;
import kit.route.a.lot.controller.State;

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
        if ((state.getLoadedMapInfo() == null) || (state.getLoadedGraph() == null)) {
            throw new IllegalStateException("No map initialized!");
        }
        // Open file stream, abort on failure
        DataInputStream stream = new DataInputStream(new BufferedInputStream(
                new ProgressInputStream(new FileInputStream(file), p, file.length())));
        
        // Read data from stream, abort on error
        if ((stream.readChar() != 'S') || (stream.readChar() != 'R')
                || (stream.readChar() != 'A') || (stream.readChar() != 'L')) {
            throw new IOException("Is not a map file: " + file.getName());
        }
        if (!stream.readUTF().equals("0.5")) {
            throw new IOException("Wrong format version: " + file.getName());
        } 

        state.getLoadedMapInfo().loadFromStream(stream);
        state.getLoadedGraph().loadFromStream(stream);
        stream.close();
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
        if ((state.getLoadedMapInfo() == null) || (state.getLoadedGraph() == null)) {
            throw new IllegalStateException("No map loaded!");
        }
        
        // Open / create file stream, abort on failure
        DataOutputStream stream = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(file)));
        
        // Write data to stream, abort on error
        stream.writeChars("SRAL");  // magic number
        stream.writeUTF("0.5");     // version number
        // TODO: maybe add date or name
        p.add(0.05);
        logger.info("save map info...");
        state.getLoadedMapInfo().saveToStream(stream);
        p.add(0.7);
        logger.info("save graph...");
        state.getLoadedGraph().saveToStream(stream); 
        stream.close();     
        logger.info("map saving finished");
        p.add(0.25);
    }
    
}
