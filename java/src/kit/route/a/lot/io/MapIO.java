package kit.route.a.lot.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException; // TODO: possibly not the right exception type

import kit.route.a.lot.controller.State;


/*
 * DISCUSS: should mapInfo etc be created by loadMap() or by the controller?
 */

public class MapIO {

    /**
     * Loads the given SRAL map file into the MapInfo and RoutingGraph data
     * structures that are accessible via State.
     * Also changes the current map name in state. Exceptions occur if the
     * file cannot be opened or has an invalid format. An IllegalStateException
     * will be thrown if the destination data structures don't exist.
     * 
     * @param file the file to be loaded
     * @throws IOException
     * @throws DataFormatException
     */
    public static void loadMap(File file) throws IOException, DataFormatException {
        // Verify requirements
        if (file == null) {
            throw new IllegalArgumentException();
        }
        State state = State.getInstance();
        if ((state.loadedMapInfo == null) || (state.loadedGraph == null)
                || (state.loadedMapName == null)) {
            throw new IllegalStateException("No map initialized!");
        }
        
        // Open file stream, abort on failure
        DataInputStream stream;
        stream = new DataInputStream(new FileInputStream(file));
        
        // Read data from stream, abort on error
        if ((stream.readChar() != 'S') || (stream.readChar() != 'R')
                || (stream.readChar() != 'A') || (stream.readChar() != 'L')) {
            throw new DataFormatException("Is not a map file: " + file.getName());
        }
        if (!stream.readUTF().equals("0.5")) {
            throw new DataFormatException("Wrong format version: " + file.getName());
        }
        state.loadedMapName = stream.readUTF();
        state.loadedMapInfo.loadFromStream(stream);
        state.loadedGraph.loadFromStream(stream);
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
    public static void saveMap(File file) throws IOException {   
        // Verify requirements
        if (file == null) {
            throw new IllegalArgumentException();
        }
        State state = State.getInstance();
        if ((state.loadedMapInfo == null) || (state.loadedGraph == null)
                || (state.loadedMapName == null)) {
            throw new IllegalStateException("No map loaded!");
        }
        
        // Open / create file stream, abort on failure
           DataOutputStream stream = new DataOutputStream(new FileOutputStream(file));
        
        // Write data to stream, abort on error
        stream.writeBytes("SRAL");  // magic number
        stream.writeUTF("0.5");     // version number
        // TODO: maybe add date
        stream.writeUTF(State.getInstance().loadedMapName);
        state.loadedMapInfo.saveToStream(stream);
        state.loadedGraph.saveToStream(stream); 
        stream.close();      
    }
}
