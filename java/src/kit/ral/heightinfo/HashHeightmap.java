package kit.ral.heightinfo;

import java.util.HashMap;
import kit.ral.common.Coordinates;

public class HashHeightmap extends Heightmap {

    private HashMap<Long, HeightTile> map = new HashMap<Long, HeightTile>();
      
    // ADMINISTRATIVE FUNCTIONS
    
    @Override
    public void addHeightTile(HeightTile tile) {
        if (tile != null) {
            map.put(tile.getSpecifier(), tile);
        }
    }
    
    // RETRIEVAL FUNCTIONS
    
    @Override
    public float getHeight(Coordinates pos) {
        HeightTile tile = map.get(HeightTile.getSpecifier(
                (int) pos.getLatitude(), (int) pos.getLongitude()));
        return (tile != null) ? tile.getHeight(pos) : UNDEFINED_HEIGHT;
    }
        
    // MISCELLANEOUS
    
    public boolean equals(Object other) {
        if(other == this) {
            return true;
        }
        if(!(other instanceof HashHeightmap)) {
            return false;
        }
        HashHeightmap comparee = (HashHeightmap) other;
        return map.equals(comparee.map);
    }
    
}
