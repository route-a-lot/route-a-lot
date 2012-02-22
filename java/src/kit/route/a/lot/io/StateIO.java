package kit.route.a.lot.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kit.route.a.lot.common.Coordinates;
import kit.route.a.lot.common.Selection;
import kit.route.a.lot.controller.State;


public class StateIO {

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
        state.setCenterCoordinates(Coordinates.loadFromStream(stream));
        state.setDetailLevel(stream.readInt()); 
        
        int len = stream.readInt();
        ArrayList<Selection> navNodes = new ArrayList<Selection>(len);  
        for (int i = 0; i < len; i++) {
            navNodes.add(new Selection(stream.readInt(), stream.readInt(),
                    stream.readFloat(), Coordinates.loadFromStream(stream),
                    stream.readUTF()));
        }
        state.setNavigationNodes(navNodes);
        
        // miscellaneous data  
        state.setClickRadius(stream.readInt());
        state.setHeightMalus(stream.readInt());
        state.setHighwayMalus(stream.readInt());
        state.setSpeed(stream.readInt());
              
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
        
        state.getCenterCoordinates().saveToStream(stream);
        stream.writeInt(state.getDetailLevel());
        
        List<Selection> navNodes = state.getNavigationNodes();
        stream.writeInt(navNodes.size());
        for (Selection navNode: navNodes) {
            stream.writeInt(navNode.getFrom());
            stream.writeInt(navNode.getTo());
            stream.writeFloat(navNode.getRatio());
            navNode.getPosition().saveToStream(stream);
            stream.writeUTF(navNode.getName());
        }

        // miscellaneous data
        stream.writeInt(state.getClickRadius());
        stream.writeInt(state.getHeightMalus());
        stream.writeInt(state.getHighwayMalus());
        stream.writeInt(state.getSpeed());
        
        stream.close();
    }
}
