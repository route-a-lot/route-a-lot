package kit.ral.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kit.ral.common.Coordinates;
import kit.ral.common.Selection;
import kit.ral.controller.State;
import kit.ral.map.rendering.Renderer;
import kit.ral.map.rendering.Renderer3D;


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
