
/**
Copyright (c) 2012, Matthias Grundmann, Daniel Krauß, Josua Stabenow
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
        * Redistributions of source code must retain the above copyright
          notice, this list of conditions and the following disclaimer.
        * Redistributions in binary form must reproduce the above copyright
          notice, this list of conditions and the following disclaimer in the
          documentation and/or other materials provided with the distribution.
        * The names of the contributors may not be used to endorse or promote products
          derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
**/

package kit.ral.io;

import kit.ral.common.Progress;
import kit.ral.common.RandomReadStream;
import kit.ral.common.RandomWriteStream;
import kit.ral.controller.State;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

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
