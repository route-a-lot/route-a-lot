package kit.route.a.lot.heightinfo;

import java.util.LinkedList;
import java.util.List;

import kit.route.a.lot.common.Coordinates;

public class Heightmap implements IHeightmap {

    private static final float UNDEFINED_HEIGHT = 0;
    private List<HeightTile> map;

    /* Konstruktor */
    public Heightmap() {
        this.map = new LinkedList<HeightTile>();
    }
    
    public boolean equals(Object other) {
        if(other == this) {
            return true;
        }
        if(!(other instanceof Heightmap)) {
            return false;
        }
        Heightmap comparee = (Heightmap) other;
        return map.equals(comparee.map);
    }

    @Override
    public float getHeight(Coordinates pos) {
        int lat = (int) pos.getLatitude();
        int lon = (int) pos.getLongitude();
        Coordinates origin = new Coordinates((float) lat, (float) lon);
        HeightTile tmpTile = new RAMHeightTile(0, 0, origin);
        for (HeightTile tile : map) {
            if (tile.equals(tmpTile)) {
                return tile.getHeight(pos);
            }// end if
        }// end while
        return UNDEFINED_HEIGHT;
    }// end getHeight

    @Override
    public void addHeightTile(HeightTile tile) {
        if (tile != null) {
            map.add(tile);
        }
    }

    @Override
    public void reduceSection(Coordinates topLeft, Coordinates bottomRight, float[][] heightdata, int heightBorder) {
        Coordinates dimensions = bottomRight.clone().subtract(topLeft);
        float stepSizeX = dimensions.getLongitude() / (heightdata.length - 2*heightBorder - 1);
        float stepSizeY = dimensions.getLatitude() / (heightdata[0].length - 2*heightBorder - 1);
        for (int x = 0; x < heightdata.length; x++) {
            for (int y = 0; y < heightdata[x].length; y++) {
                dimensions.setLatitude(topLeft.getLatitude() + (y - heightBorder) * stepSizeY);
                dimensions.setLongitude(topLeft.getLongitude() + (x - heightBorder) * stepSizeX);
                heightdata[x][y] = getHeight(dimensions);
            }
        }
    }
}// end class
