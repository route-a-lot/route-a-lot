package kit.ral.heightinfo;

import java.util.LinkedList;
import java.util.List;

import kit.ral.common.Coordinates;

public class ListHeightmap extends Heightmap {

    private List<HeightTile> map;

    // CONSTRUCTOR
    
    public ListHeightmap() {
        this.map = new LinkedList<HeightTile>();
    }
      
    // ADMINISTRATIVE FUNCTIONS
    
    @Override
    public void addHeightTile(HeightTile tile) {
        if (tile != null) {
            map.add(tile);
        }
    }
    
    // RETRIEVAL FUNCTIONS
    
    @Override
    public float getHeight(Coordinates pos) {
        int lat = (int) pos.getLatitude();
        int lon = (int) pos.getLongitude();
        Coordinates origin = new Coordinates((float) lat, (float) lon);
        HeightTile tmpTile = new RAMHeightTile(0, 0, origin);
        for (HeightTile tile : map) {
            if (tile.equals(tmpTile)) {
                return tile.getHeight(pos);
            }
        }
        return UNDEFINED_HEIGHT;
    }
        
    // MISCELLANEOUS
    
    public boolean equals(Object other) {
        if(other == this) {
            return true;
        }
        if(!(other instanceof ListHeightmap)) {
            return false;
        }
        ListHeightmap comparee = (ListHeightmap) other;
        return map.equals(comparee.map);
    }
    
}
