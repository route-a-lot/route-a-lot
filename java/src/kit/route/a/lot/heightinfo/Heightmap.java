package kit.route.a.lot.heightinfo;

import java.util.Set;

import kit.route.a.lot.common.Coordinates;


public class Heightmap implements IHeightmap {

    /** Associations */
    private IHeightTile tiles;

    @Override
    public Set<HeightTile>
            getTiles(Coordinates upLeft, Coordinates bottomRight) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public float getHeight(Coordinates pos) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void addHeightTile(HeightTile tile) {
        // TODO Auto-generated method stub

    }
}
