
/**
Copyright (c) 2012, Matthias Grundmann, Josua Stabenow
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

import kit.ral.common.Coordinates;
import kit.ral.common.Selection;
import kit.ral.controller.State;
import kit.ral.map.rendering.Renderer;
import kit.ral.map.rendering.Renderer3D;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class StateIO {
    private static final boolean LOAD_MAP_MODE = false, LOAD_POSITION = true;
    
    /**
     * Loads the current application state from a file.
     * Note that this does not load the map, a call to MapIO.loadMap()
     * should be made after calling this method.
     * 
     * @param file the state file
     * @throws IOException a read error occurred
     * @throws IllegalArgumentException file is null
     */
    public static void loadState(File file) throws IOException {
        // Verify requirements
        if (file == null) {
            throw new IllegalArgumentException();
        }
        // Open file stream, abort on failure
        DataInputStream stream = new DataInputStream(new FileInputStream(file));
        State state = State.getInstance();
        
        // essential data
        String path = stream.readUTF();
        state.setLoadedMapFile((path == null) ? null : new File(path));
        Coordinates center = Coordinates.loadFromInput(stream);
        int detailLevel = stream.readInt();
        if (LOAD_POSITION) {
            state.setCenterCoordinates(center);
            state.setDetailLevel(detailLevel); 
        }
        
        int len = stream.readInt();
        ArrayList<Selection> navNodes = new ArrayList<Selection>(len);  
        for (int i = 0; i < len; i++) {
            navNodes.add(new Selection(Coordinates.loadFromInput(stream),
                    stream.readInt(), stream.readInt(),
                    stream.readFloat(), stream.readUTF()));
        }
        state.setNavigationNodes(navNodes);
        
        // miscellaneous data  
        state.setClickRadius(stream.readInt());
        state.setHeightMalus(stream.readInt());
        state.setHeightMalus(0);
        state.setHighwayMalus(stream.readInt());
        state.setHighwayMalus(0);
        state.setSpeed(stream.readInt());
        boolean render3D = stream.readBoolean();
        if (LOAD_MAP_MODE) {
            state.setActiveRenderer((render3D) ? new Renderer3D() : new Renderer());
        }              
        stream.close();
    }

    /**
     * Saves the current application state to a file.
     * Note that this does not save the map.
     * 
     * @param file the state file
     * @throws IOException a write error occurred
     * @throws IllegalArgumentException file is null
     */
    public static void saveState(File file) throws IOException {
        // Verify requirements
        if (file == null) {
            throw new IllegalArgumentException();
        }
        // Open file stream, abort on failure
        DataOutputStream stream = new DataOutputStream(new FileOutputStream(file));
        State state = State.getInstance();
        
        // essential data
        if (state.getLoadedMapFile() == null) {
            stream.writeUTF("");
        } else {
            stream.writeUTF(state.getLoadedMapFile().getPath());
        }
        
        state.getCenterCoordinates().saveToOutput(stream);
        stream.writeInt(state.getDetailLevel());
        
        List<Selection> navNodes = state.getNavigationNodes();
        stream.writeInt(navNodes.size());
        for (Selection navNode: navNodes) {
            navNode.getPosition().saveToOutput(stream);
            stream.writeInt(navNode.getFrom());
            stream.writeInt(navNode.getTo());
            stream.writeFloat(navNode.getRatio());
            stream.writeUTF(navNode.getName());
        }

        // miscellaneous data
        stream.writeInt(state.getClickRadius());
        stream.writeInt(state.getHeightMalus());
        stream.writeInt(state.getHighwayMalus());
        stream.writeInt(state.getSpeed());
        stream.writeBoolean(state.getActiveRenderer() instanceof Renderer3D);
        
        stream.close();
    }
}
